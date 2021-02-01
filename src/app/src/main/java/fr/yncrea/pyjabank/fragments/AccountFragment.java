package fr.yncrea.pyjabank.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.concurrent.Executors;

import fr.yncrea.pyjabank.AppActivity;
import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.models.Account;
import fr.yncrea.pyjabank.database.models.User;
import fr.yncrea.pyjabank.recyclers.AccountAdapter;
import fr.yncrea.pyjabank.services.RestApi;
import fr.yncrea.pyjabank.database.BankDatabase;
import fr.yncrea.pyjabank.services.Utils;

public class AccountFragment extends Fragment {

    private AccountAdapter mAdapter;

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.menu_disconnect).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.menu_cleanDB) {
            mAdapter.setAccounts(null);
            Executors.newSingleThreadExecutor().execute(() ->
                    BankDatabase.getDatabase().userDao().insert(AppActivity.getLogged()));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        //attribution des layouts
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        //shortcuts
        assert getActivity() != null && getContext() != null;
        assert ((AppCompatActivity) getActivity()).getSupportActionBar() != null;
        BankDatabase database = BankDatabase.getDatabase();
        User logged = AppActivity.getLogged();

        //listage des components à manipuler (appels multiples)
        Button refresh = view.findViewById(R.id.frag_acc_btn_refresh);

        //initialisation
        String str = logged.getName() + " " + logged.getLastname();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(str);

        mAdapter = new AccountAdapter(view.findViewById(R.id.frag_acc_recycler_accounts), null);

        //réaction aux interactions
        refresh.setOnClickListener(v -> {
            if (!Utils.haveInternet(getContext())) {
                String str2 = getString(R.string.toast_invalid_internet);
                Toast.makeText(getContext(), str2, Toast.LENGTH_SHORT).show();
                return;
            }

            getActivity().runOnUiThread(() -> refresh.setEnabled(false));
            new RestApi<>(getActivity()).setHandler( //Routine de rafraichissement des données
                () -> Executors.newSingleThreadExecutor().execute(() -> {
                    List<Account> accounts = database.accountDao().getAll(/*AppActivity.mLogged.getUsername()*/);
                    String str2 = getString(R.string.toast_api_success_accounts, accounts.size());
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), str2, Toast.LENGTH_SHORT).show();
                        mAdapter.setAccounts(accounts);
                        refresh.setEnabled(true);
                    });
                }),
                () -> {
                    String str2 = getString(R.string.toast_api_empty_accounts);
                    Toast.makeText(getContext(), str2, Toast.LENGTH_SHORT).show();
                    refresh.setEnabled(true);
                },
                () -> {
                    String str2 = getString(R.string.toast_api_failure);
                    Toast.makeText(getContext(), str2, Toast.LENGTH_SHORT).show();
                    refresh.setEnabled(true);
                }
            ).retrieveStoreAccountList(database);
        });

        //récupération des données bdd
        Executors.newSingleThreadExecutor().execute(() -> {
            assert getActivity() != null;

            List<Account> accounts = database.accountDao().getAll(/*AppActivity.mLogged.getUsername()*/);
            if (accounts.isEmpty()) {
                if (Utils.haveInternet(getContext())) getActivity().runOnUiThread(refresh::callOnClick);
                else {
                    String str2 = getString(R.string.toast_invalid_internet);
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), str2, Toast.LENGTH_SHORT).show());
                }
            }
            else getActivity().runOnUiThread(() -> mAdapter.setAccounts(accounts));
        });

        /*view.findViewById(R.id.button).setOnClickListener(v -> {
            new RestApi<>(getActivity()).sendStoreAccount(database, new Account());
            //refresh.callOnClick();
        });*/

        return view;
    }
}
