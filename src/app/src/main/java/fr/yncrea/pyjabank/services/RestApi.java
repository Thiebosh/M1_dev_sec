package fr.yncrea.pyjabank.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.BankDatabase;
import fr.yncrea.pyjabank.database.models.Account;
import fr.yncrea.pyjabank.database.models.User;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class RestApi<T> {

    private interface ApiRoutes { //Create Read Update Delete
        @GET("m1/config/1")
        Call<User> readUser();//(@Field("username") String username, @Field("password") String password)

        @GET("m1/accounts")
        Call<ArrayList<Account>> readAccounts();//(@Field("username") String username)

        @FormUrlEncoded
        @POST("m1/accounts")
        Call<JsonObject> createAccount(@Field("account") Account account);//(@Field("username") String username, ...)
    }

    private RestApi() {//not instanciable from outside
    }

    public static class Holder {
        @SuppressLint("StaticFieldLeak")
        private static final RestApi INSTANCE = new RestApi<>(); //self-hosted instance
        private static boolean mIsBuild = false;

        public static RestApi getInstance() {//instance's getter (singleton)
            return mIsBuild ? INSTANCE : null;
        }

        public static void buildInstance(final Activity activity) {//new constructor
            mIsBuild = true;

            INSTANCE.mActivity = activity;

            String url = ImageStegano.decrypt(INSTANCE.mActivity.getResources(), R.drawable.resolution);
            String pin = ImageStegano.decrypt(INSTANCE.mActivity.getResources(), R.drawable.tractopelle);

            INSTANCE.mApiInterface = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder().certificatePinner(new CertificatePinner.Builder().add(url,pin).build()).build())
                    /*.client(new OkHttpClient().newBuilder().addNetworkInterceptor(chain -> {
                        //tentative d'utilisation de "certificate transparency" à la place du "certificate pinning"
                        Log.d("testy", "je suis dans ta connexion hehehrhrhehahrherhht");
                        return null;
                    }).build())*/
                    .build()
                    .create(ApiRoutes.class);
        }

    }

    private ApiRoutes mApiInterface;
    protected Activity mActivity;

    private static final Executor mBackgroundThread = Executors.newSingleThreadExecutor();

    private int mFlag = -1;
    private static final int FLAG_READ_USER = 0;
    private static final int FLAG_READ_ACCOUNT = 1;
    private static final int FLAG_SEND_ACCOUNT = 2;

    private Handler mHandler;
    private static final int EXECUTION_RESULT = 0;
    private static final int RESPONSE_SUCCESS = 0;
    private static final int RESPONSE_EMPTY = 1;
    private static final int RESPONSE_ERROR = 2;

    private static final String ERROR_FLAG = "Unexpected flag";
    private static final String ERROR_TYPE = "Unexpected type of data";


    public RestApi<T> setHandler(final Runnable successFunc,
                                 final Runnable emptyFunc,
                                 final Runnable errorFunc) {
        mActivity.runOnUiThread(() -> mHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(final Message msg) {
                    if (msg.what == EXECUTION_RESULT) {
                        switch (msg.arg1) {
                            case RESPONSE_SUCCESS:
                                successFunc.run();
                                break;
                            case RESPONSE_EMPTY:
                                emptyFunc.run();
                                break;
                            case RESPONSE_ERROR:
                                errorFunc.run();
                                break;
                        }
                    }
                }
        });

        return this;
    }

    @SuppressWarnings("unchecked")
    public void retrieveStoreUser(@NonNull final BankDatabase db, final String username, final String password) {
        mFlag = FLAG_READ_USER;
        retrieveStoreData((Call<T>) mApiInterface.readUser(), db,
                username, password, null);
    }

    @SuppressWarnings("unchecked")
    public void retrieveStoreAccountList(@NonNull final BankDatabase db/*, final String username*/) {
        mFlag = FLAG_READ_ACCOUNT;
        retrieveStoreData((Call<T>) mApiInterface.readAccounts(/*username*/), db,
                null, null, null);
    }

    @SuppressWarnings("unchecked")
    public void sendStoreAccount(@NonNull final BankDatabase db, @NonNull final Account account) {
        mFlag = FLAG_SEND_ACCOUNT;
        retrieveStoreData((Call<T>) mApiInterface.createAccount(account), db,
                null, null, account);
    }

    private void retrieveStoreData(final Call<T> request, final BankDatabase db,
                                   final String username, final String password, final Account account) {
        mBackgroundThread.execute(() -> {
            try {
                Response<T> response = request.execute();

                if (response.isSuccessful() && response.body() != null) {
                    T data = response.body();

                    switch (mFlag) {
                        case FLAG_READ_USER:
                            if (data instanceof User) {
                                ((User) data).setUsername(username);
                                ((User) data).setPassword(password);
                                db.userDao().insert((User) data);
                            }
                            else throw new Exception(ERROR_TYPE);
                            break;

                        case FLAG_READ_ACCOUNT:
                            if (data instanceof ArrayList) {
                                @SuppressWarnings("unchecked")
                                ArrayList<Account> typedData = (ArrayList<Account>) data;
                                db.accountDao().insertAll(typedData);
                            }
                            else throw new Exception(ERROR_TYPE);
                            break;

                        case FLAG_SEND_ACCOUNT:
                            db.accountDao().insert(account);
                            break;

                        default:
                            data = null;
                            throw new Exception(ERROR_FLAG);
                    }
                    data = null;

                    if (mHandler != null) mHandler.obtainMessage(EXECUTION_RESULT, RESPONSE_SUCCESS, -1).sendToTarget();
                }
                else if (mHandler != null) mHandler.obtainMessage(EXECUTION_RESULT, RESPONSE_EMPTY, -1).sendToTarget();
            }
            catch (Exception e) {
                if (mHandler != null) mHandler.obtainMessage(EXECUTION_RESULT, RESPONSE_ERROR, -1).sendToTarget();
            }
        });
    }
}
