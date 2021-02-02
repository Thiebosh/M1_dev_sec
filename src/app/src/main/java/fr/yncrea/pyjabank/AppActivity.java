package fr.yncrea.pyjabank;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import java.util.Stack;
import java.util.concurrent.Executors;

import fr.yncrea.pyjabank.database.models.User;
import fr.yncrea.pyjabank.fragments.ConnectFragment;
import fr.yncrea.pyjabank.interfaces.FragmentSwitcher;
import fr.yncrea.pyjabank.database.BankDatabase;

public class AppActivity extends AppCompatActivity implements FragmentSwitcher {

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /*
     * Section FragmentSwitcher
     */

    @Override
    public void loadFragment(final Fragment fragment, final boolean addToBackstack) {
        String searched = fragment.getClass().getSimpleName();
        @SuppressWarnings("unchecked")
        Stack<Fragment> searcher = (Stack<Fragment>) mFragStack.clone();
        int position = searcher.size() - 1;//size -> index
        while (!searcher.empty()) {
            if (searcher.pop().getClass().getSimpleName().equals(searched)) break;
            --position;
        }
        if (position != -1) for (int i = mFragStack.size(); i > position; --i) mFragStack.pop();

        if (addToBackstack) mFragStack.push(fragment);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.act_app_fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mFragStack.size() < 2) {
            mFragStack.clear();
            AppActivity.this.finish();
        }
        else {
            mFragStack.pop();//ecran actuel
            loadFragment(mFragStack.peek(), true);
        }
    }


    /*
     * Section SharedPreferences
     */

    private static final String PREF_SLOT_ACCESS = "PyjaBankUserPrefs";
    private static final String PREF_ACCESS_SEND_ONLINE = "AutoSend";

    private static SharedPreferences mPrefs;


    /*
     * Section Menu
     */

    private static MenuItem mSendOnline = null;

    public static boolean isSendOnline() {
        return mSendOnline.isChecked();
    }

    @SuppressLint({"RestrictedApi", "ResourceAsColor"})
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (menu instanceof MenuBuilder) ((MenuBuilder) menu).setOptionalIconsVisible(true);
        MenuCompat.setGroupDividerEnabled(menu, true);
        getMenuInflater().inflate(R.menu.app, menu);

        mSendOnline = menu.findItem(R.id.menu_app_send_online);
        mSendOnline.setChecked(mPrefs.getBoolean(PREF_ACCESS_SEND_ONLINE, false));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        final int disconnect = R.id.menu_app_disconnect;
        final int cleanDB = R.id.menu_app_cleanDB;
        final int refreshAuto = R.id.menu_app_send_online;

        switch (item.getItemId()) {
            case disconnect:
                loadFragment(new ConnectFragment(), true);
                return true; //event totally handled

            case cleanDB:
                Executors.newSingleThreadExecutor().execute(() -> {
                    BankDatabase.getDatabase().userDao().deleteAll();
                    BankDatabase.getDatabase().accountDao().deleteAll();

                    String str = getString(R.string.toast_db_clean);
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show());
                });
                return false; //fragment can do more with it

            case refreshAuto:
                item.setChecked(!item.isChecked());

                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                item.setActionView(new View(this));
                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return false;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return false;
                    }
                });
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
     * Section Cycle de vie
     */

    private static User mLogged = null;

    public static User getLogged() {
        return mLogged;
    }

    public static void setLogged(@NonNull final User user) {
        mLogged = user;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        mPrefs = getApplicationContext().getSharedPreferences(PREF_SLOT_ACCESS, MODE_PRIVATE);

        BankDatabase.buildDatabase(getApplicationContext());

        loadFragment(new ConnectFragment(), true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mLogged = null;
        loadFragment(new ConnectFragment(), true);

        mPrefs.edit().putBoolean(PREF_ACCESS_SEND_ONLINE, mSendOnline.isChecked()).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BankDatabase.getDatabase().close();
    }
}
