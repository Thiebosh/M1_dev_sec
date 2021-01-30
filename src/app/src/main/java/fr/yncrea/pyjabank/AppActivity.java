package fr.yncrea.pyjabank;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import java.util.Stack;
import java.util.concurrent.Executors;

import fr.yncrea.pyjabank.fragments.ConnectFragment;
import fr.yncrea.pyjabank.interfaces.Utils;
import fr.yncrea.pyjabank.interfaces.FragmentSwitcher;
import fr.yncrea.pyjabank.database.BankDatabase;

public class AppActivity extends AppCompatActivity implements FragmentSwitcher, Utils {

    /*
     * Section FragmentSwitcher
     */

    @Override
    public void loadFragment(final Fragment fragment, final boolean addToBackstack) {
        String searched = fragment.getClass().getSimpleName();
        @SuppressWarnings("unchecked")
        Stack<Fragment> searcher = (Stack<Fragment>) mFragmentStack.clone();
        int position = searcher.size() - 1;//size -> index
        while (!searcher.empty()) {
            if (searcher.pop().getClass().getSimpleName().equals(searched)) break;
            --position;
        }
        if (position != -1) for (int i = mFragmentStack.size(); i > position; --i) mFragmentStack.pop();

        if (addToBackstack) mFragmentStack.push(fragment);//renouvellement du frament stocké

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.act_app_fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentStack.size() < 2) {//besoin d'écran actuel et précédent
            mFragmentStack.clear();
            AppActivity.this.finish();//revient à l'activité précédente
        }
        else {
            mFragmentStack.pop();//ecran actuel
            loadFragment(mFragmentStack.peek(), true);//ecran précédent / can't be a String stack
        }
    }

    /*
     * Section Utils
     */

    private BankDatabase mDatabase;

    @Override
    public BankDatabase getDatabase() {
        return mDatabase;
    }

    @Override
    public boolean haveInternet() {
        NetworkInfo network = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return network != null && network.isConnected();
    }


    /*
     * Section Menu
     */

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) ((MenuBuilder) menu).setOptionalIconsVisible(true);
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int disconnect = R.id.menu_disconnect;
        final int cleanDB = R.id.menu_cleanDB;

        switch (item.getItemId()) {
            case disconnect:
                loadFragment(new ConnectFragment(), true);
                return true; //event totally handled
            case cleanDB:
                Executors.newSingleThreadExecutor().execute(() -> {
                    mDatabase.userDao().deleteAll();
                    mDatabase.accountDao().deleteAll();

                    String str = "database clean";
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show());
                });
                return false; //fragment can do more with it
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Section Cycle de vie
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app);

        //if pas de fichier,
        //génère clé avec aléatoire
        //récupère emprunte cryptographique
        //enregistre en fichier

        //lit clé chiffrée depuis fichier

        //lien bdd avant moindre affichage

        byte[] key = SQLiteDatabase.getBytes("passPhrase".toCharArray());
        final SupportFactory factory = new SupportFactory(key, null,false);

        mDatabase = Room.databaseBuilder(getApplicationContext(), BankDatabase.class, "PyjaBank2.db")
                .openHelperFactory(factory) //commenter pour passer sur db classique
                .build();

        loadFragment(new ConnectFragment(), true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //disconnectUser();
        loadFragment(new ConnectFragment(), true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }
}
