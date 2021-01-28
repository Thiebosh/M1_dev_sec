package fr.yncrea.pyjabank.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;
import java.util.concurrent.Executors;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.interfaces.Utils;
import fr.yncrea.pyjabank.interfaces.FragmentSwitcher;
import fr.yncrea.pyjabank.services.api.RestApi;
import fr.yncrea.pyjabank.database.BankDatabase;

public class HomeFragment extends Fragment {

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_disconnect).setVisible(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //attribution des layouts et éléments clés
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        //database
        assert getActivity() != null && getContext() != null;
        BankDatabase database = ((Utils) getActivity()).getDatabase();

        //listage des components à manipuler (appels multiples)
        Button next = view.findViewById(R.id.frag_home_button_login);

        //réaction aux interactions
        next.setOnClickListener(v -> {
            next.setEnabled(false);
            Executors.newSingleThreadExecutor().execute(() -> {
                if (!database.userDao().isUser()) {
                    if (!((Utils) getActivity()).haveInternet()) {
                        String str = "Error : internet not active";
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                            next.setEnabled(true);
                        });
                        return;
                    }

                    new RestApi<>(getActivity()).setHandler(
                            () -> {
                                //((UserCredentials) getActivity()).connectUser(username);//si ici, password ok. get username by ui component
                                ((FragmentSwitcher) getActivity()).loadFragment(new AccountFragment(), true);
                            },
                            () -> {
                                String str = "Non existing user";
                                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                                next.setEnabled(true);
                            },
                            () -> {
                                String str = "Execution failure : please, retry";
                                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                                next.setEnabled(true);
                            }
                    ).retrieveStoreUser(database);// retrieveStoreUser(database, username, password)
                }
                else getActivity().runOnUiThread(() -> ((FragmentSwitcher) getActivity()).loadFragment(new AccountFragment(), true));
            });
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        assert getActivity() != null;
        String str = "user";
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setSubtitle(str);
    }
}
