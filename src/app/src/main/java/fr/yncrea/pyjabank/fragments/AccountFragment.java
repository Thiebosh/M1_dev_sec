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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.models.Account;
import fr.yncrea.pyjabank.interfaces.FragmentSwitcher;
import fr.yncrea.pyjabank.interfaces.Utils;
import fr.yncrea.pyjabank.database.models.User;
import fr.yncrea.pyjabank.recyclers.AccountAdapter;
import fr.yncrea.pyjabank.services.api.RestApi;
import fr.yncrea.pyjabank.database.BankDatabase;

public class AccountFragment extends Fragment {

    private AccountAdapter mAdapter;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_disconnect).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_cleanDB) {
            mAdapter.setAccounts(null);
            new RestApi<>(getActivity()).retrieveStoreUser(((Utils) getActivity()).getDatabase());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //attribution des layouts
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        //database
        assert getActivity() != null && getContext() != null;
        BankDatabase database = ((Utils) getActivity()).getDatabase();

        //listage des components à manipuler (appels multiples)
        Button refresh = view.findViewById(R.id.frag_acc_btn_refresh);
        RecyclerView recycler = view.findViewById(R.id.frag_acc_recycler_accounts);

        //initialisation
        mAdapter = new AccountAdapter(null);
        recycler.setAdapter(mAdapter);

        //réaction aux interactions
        refresh.setOnClickListener(v -> {
            if (!((Utils) getActivity()).haveInternet()) {
                String str = "Error : internet not active";
                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                return;
            }

            getActivity().runOnUiThread(() -> refresh.setEnabled(false));
            new RestApi<>(getActivity()).setHandler( //Routine de rafraichissement des données
                () -> Executors.newSingleThreadExecutor().execute(() -> {
                    List<Account> accounts = database.accountDao().getAll();// getAll(((UserCredentials) getActivity()).getUsername())
                    String str = "account reception success : retrieve " + accounts.size() + " accounts";
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                        mAdapter.setAccounts(accounts);
                        refresh.setEnabled(true);
                    });
                }),
                () -> {
                    String str = "No accounts";
                    Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                    refresh.setEnabled(true);
                },
                () -> {
                    String str = "account reception failure";
                    Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                    refresh.setEnabled(true);
                }
            ).retrieveStoreAccountList(database);
        });

        //récupération des données bdd
        Executors.newSingleThreadExecutor().execute(() -> {
            User data = database.userDao().get(1);// get(((UserCredentials) getActivity()).getUsername()) -> convertit nom en objet user
            String str = data.getName() + " " + data.getLastname();
            getActivity().runOnUiThread(() ->
                Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setSubtitle(str)
            );

            List<Account> accounts = database.accountDao().getAll();// getAll(((UserCredentials) getActivity()).getUsername())
            if (accounts.isEmpty()) {// ou wifi désactivé
                if (((Utils) getActivity()).haveInternet()) Objects.requireNonNull(getActivity()).runOnUiThread(refresh::callOnClick);
                else {
                    String str2 = "Error : internet not active";
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), str2, Toast.LENGTH_SHORT).show());
                }
            }
            else Objects.requireNonNull(getActivity()).runOnUiThread(()->mAdapter.setAccounts(accounts));
        });

        return view;
    }
}