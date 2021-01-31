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

import fr.yncrea.pyjabank.AppActivity;
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
        TextView username = view.findViewById(R.id.frag_conn_text_username);
        TextView password = view.findViewById(R.id.frag_conn_text_password);

        List<Button> digits = Arrays.asList(view.findViewById(R.id.frag_conn_button_digit0),
                                            view.findViewById(R.id.frag_conn_button_digit1),
                                            view.findViewById(R.id.frag_conn_button_digit2),
                                            view.findViewById(R.id.frag_conn_button_digit3),
                                            view.findViewById(R.id.frag_conn_button_digit4),
                                            view.findViewById(R.id.frag_conn_button_digit5),
                                            view.findViewById(R.id.frag_conn_button_digit6),
                                            view.findViewById(R.id.frag_conn_button_digit7),
                                            view.findViewById(R.id.frag_conn_button_digit8),
                                            view.findViewById(R.id.frag_conn_button_digit9));
        Button erase = view.findViewById(R.id.frag_conn_button_erase);

        Button confirm = view.findViewById(R.id.frag_conn_button_confirm);

        //initialisation
        Collections.shuffle(digits);
        for (int i = 0; i < digits.size(); ++i) {
            digits.get(i).setText(String.valueOf(i));

            //réaction aux interactions
            setOnClick(digits.get(i), password);
        }

        erase.setOnClickListener(v -> password.setText(password.getText().subSequence(0, password.getText().length()-1)));

        confirm.setOnClickListener(v -> {
            confirm.setEnabled(false);
            Executors.newSingleThreadExecutor().execute(() -> {
                if (database.userDao().isUser()) {
                    AppActivity.mLogged = database.userDao().get(username.getText().toString(), password.getText().toString());

                    if (AppActivity.mLogged != null) {
                        getActivity().runOnUiThread(() -> {
                            password.setText("");
                            ((FragmentSwitcher) getActivity()).loadFragment(new AccountFragment(), true);
                        });
                    }
                    else {
                        String str = "user doesn't exist";
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                            confirm.setEnabled(true);
                        });
                    }
                }
                else {
                    if (!((Utils) getActivity()).haveInternet()) {
                        String str = "Error : internet not active";
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                            confirm.setEnabled(true);
                        });
                        return;
                    }

                    String _username = username.getText().toString();
                    String _password = password.getText().toString();

                    //hash password : https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/

                    new RestApi<>(getActivity()).setHandler(
                            () -> {
                                password.setText("");
                                //save user instance ?
                                AppActivity.mLogged = database.userDao().get(_username, _password);
                                ((FragmentSwitcher) getActivity()).loadFragment(new AccountFragment(), true);
                            },
                            () -> {
                                String str = "No user exist";
                                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                                confirm.setEnabled(true);
                            },
                            () -> {
                                String str = "Execution failure : please, retry";
                                Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                                confirm.setEnabled(true);
                            }
                    ).retrieveStoreUser(database, _username, _password);
                }
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
