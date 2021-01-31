package fr.yncrea.pyjabank.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.interfaces.Utils;
import fr.yncrea.pyjabank.interfaces.FragmentSwitcher;
import fr.yncrea.pyjabank.services.api.RestApi;
import fr.yncrea.pyjabank.database.BankDatabase;

public class ConnectFragment extends Fragment {

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_disconnect).setVisible(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //attribution des layouts et éléments clés
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        //database
        assert getActivity() != null && getContext() != null;
        BankDatabase database = BankDatabase.getDatabase();

        //listage des components à manipuler (appels multiples)
        //TextView username = view.findViewById(R.id.frag_conn_text_username);
        TextView password = view.findViewById(R.id.frag_conn_text_password);

        Button digit0 = view.findViewById(R.id.frag_conn_button_digit0);
        Button digit1 = view.findViewById(R.id.frag_conn_button_digit1);
        Button digit2 = view.findViewById(R.id.frag_conn_button_digit2);
        Button digit3 = view.findViewById(R.id.frag_conn_button_digit3);
        Button digit4 = view.findViewById(R.id.frag_conn_button_digit4);
        Button digit5 = view.findViewById(R.id.frag_conn_button_digit5);
        Button digit6 = view.findViewById(R.id.frag_conn_button_digit6);
        Button digit7 = view.findViewById(R.id.frag_conn_button_digit7);
        Button digit8 = view.findViewById(R.id.frag_conn_button_digit8);
        Button digit9 = view.findViewById(R.id.frag_conn_button_digit9);
        Button erase = view.findViewById(R.id.frag_conn_button_erase);

        Button confirm = view.findViewById(R.id.frag_conn_button_confirm);

        //initialisation
        List<Button> digits = Arrays.asList(digit0,digit1,digit2,digit3,digit4,digit5,digit6,digit7,digit8,digit9);
        Collections.shuffle(digits);
        for (int i = 0; i < digits.size(); ++i) digits.get(i).setText(String.valueOf(i));

        //réaction aux interactions
        setOnClick(digit0, password);
        setOnClick(digit1, password);
        setOnClick(digit2, password);
        setOnClick(digit3, password);
        setOnClick(digit4, password);
        setOnClick(digit5, password);
        setOnClick(digit6, password);
        setOnClick(digit7, password);
        setOnClick(digit8, password);
        setOnClick(digit9, password);

        erase.setOnClickListener(v -> password.setText(password.getText().subSequence(0, password.getText().length()-1)));

        confirm.setOnClickListener(v -> {
            confirm.setEnabled(false);
            Executors.newSingleThreadExecutor().execute(() -> {
                if (!database.userDao().isUser()) {
                    if (!((Utils) getActivity()).haveInternet()) {
                        String str = "Error : internet not active";
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                            confirm.setEnabled(true);
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
                                confirm.setEnabled(true);
                            },
                            () -> {
                                String str = "Execution failure : please, retry";
                                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                                confirm.setEnabled(true);
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
        String str = "Visitor";
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setSubtitle(str);
    }

    private void setOnClick(final Button digit, final TextView container){
        digit.setOnClickListener(v -> container.append(digit.getText()));
    }
}
