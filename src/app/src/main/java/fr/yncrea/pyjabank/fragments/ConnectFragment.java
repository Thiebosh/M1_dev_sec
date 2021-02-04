package fr.yncrea.pyjabank.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.security.AccessControlException;
import java.security.GuardedObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PropertyPermission;
import java.util.concurrent.Executors;

import fr.yncrea.pyjabank.AppActivity;
import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.interfaces.FragmentSwitcher;
import fr.yncrea.pyjabank.services.RestApi;
import fr.yncrea.pyjabank.database.BankDatabase;
import fr.yncrea.pyjabank.services.Utils;

public class ConnectFragment extends Fragment {

    private ConstraintLayout mKeypad;

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        mKeypad.setVisibility(View.GONE);
        menu.findItem(R.id.menu_app_disconnect).setVisible(false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        //attribution des layouts et éléments clés
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        setHasOptionsMenu(true);//call onPrepareOptionsMenu

        //shortcuts
        assert getActivity() != null && getContext() != null;
        BankDatabase database = BankDatabase.getDatabase();

        //listage des components à manipuler (appels multiples)
        TextInputEditText username = view.findViewById(R.id.frag_conn_text_username_edit);
        TextInputEditText password = view.findViewById(R.id.frag_conn_text_password_edit);

        TextInputLayout usernameField = view.findViewById(R.id.frag_conn_text_username_field);
        TextInputLayout passwordField = view.findViewById(R.id.frag_conn_text_password_field);

        mKeypad = view.findViewById(R.id.frag_conn_const_keypad);

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
            setOnClick(digits.get(i), password, passwordField);
        }

        erase.setOnClickListener(v -> {
            CharSequence str = password.getText();
            if (str.length() > 0) password.setText(str.subSequence(0, str.length()-1));
        });

        view.findViewById(R.id.frag_conn_const_global).setOnClickListener(v-> mKeypad.setVisibility(View.GONE));

        username.setOnFocusChangeListener((v, focus) -> {
            String msg = null;
            if (!focus && isUsernameInvalid(username.getText().length())) {
                msg = getString(R.string.frag_conn_error_username);
            }
            else mKeypad.setVisibility(View.GONE);//if (focus)
            usernameField.setError(msg);
        });

        username.setOnKeyListener((v, keyCode, event) -> keyCode == KeyEvent.KEYCODE_ENTER);

        password.setOnTouchListener((v, event) -> {
            mKeypad.setVisibility(View.VISIBLE);
            return true; //intercepte evenement
        });

        confirm.setOnClickListener(v -> {
            if (AppActivity.isSound()) MediaPlayer.create(this.getContext(), R.raw.confirm).start();
            mKeypad.setVisibility(View.GONE);

            boolean isValid = true;
            if (isUsernameInvalid(username.getText().length())) {
                isValid = false;
                usernameField.setError(getString(R.string.frag_conn_error_username));
            }
            if (isPasswordInvalid(password.getText().length())) {
                isValid = false;
                passwordField.setError(getString(R.string.frag_conn_error_password));
            }
            if (!isValid) return;

            confirm.setEnabled(false);
            Executors.newSingleThreadExecutor().execute(() -> {
                String _username = username.getText().toString();
                String _password = password.getText().toString();

                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-512");
                    byte[] bytes = md.digest(password.getText().toString().getBytes());
                    StringBuilder sb = new StringBuilder();
                    for (byte aByte : bytes) sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                    _password = sb.toString();
                    sb = null;
                }
                catch (NoSuchAlgorithmException ignore) { }

                if (database.userDao().isUser()) {
                    AppActivity.setLogged(database.userDao().get(_username, _password));

                    if (AppActivity.getLogged() != null) {
                        getActivity().runOnUiThread(() -> {
                            username.setText("");
                            password.setText("");
                            ((FragmentSwitcher) getActivity())
                                    .loadFragment(new AccountFragment(), true);
                        });

                        _username = null;
                        _password = null;
                        return;
                    }

                    //else {
                    String str = getString(R.string.toast_invalid_identifiers);
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                        confirm.setEnabled(true);
                    });

                    _username = null;
                    _password = null;
                    return;
                    //}
                }

                //else {
                if (!Utils.haveInternet(getContext())) {
                    String str = getString(R.string.toast_invalid_internet);
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                        confirm.setEnabled(true);
                    });

                    _username = null;
                    _password = null;
                    return;
                }

                GuardedObject guardedName = new GuardedObject(_username, new PropertyPermission("java.home", "read"));
                GuardedObject guardedPass = new GuardedObject(_password, new PropertyPermission("java.home", "read"));
                new RestApi<>(getActivity()).setHandler(
                        () -> {
                            username.setText("");
                            password.setText("");
                            Executors.newSingleThreadExecutor().execute(() -> {
                                try {
                                    AppActivity.setLogged(database.userDao()
                                            .get((String) guardedName.getObject(), (String) guardedPass.getObject()));

                                    getActivity().runOnUiThread(() ->
                                            ((FragmentSwitcher) getActivity())
                                                    .loadFragment(new AccountFragment(), true));
                                }
                                catch (AccessControlException ignore) { }
                            });
                        },
                        () -> {
                            String str = getString(R.string.toast_api_empty_user);
                            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                            confirm.setEnabled(true);
                        },
                        () -> {
                            String str = getString(R.string.toast_api_failure);
                            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                            confirm.setEnabled(true);
                        }
                ).retrieveStoreUser(database, _username, _password);

                _username = null;
                _password = null;
                //}
            });
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        assert getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null;
        String str = getString(R.string.app_default_user);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(str);
    }

    private void setOnClick(final Button digit, final TextInputEditText container, final TextInputLayout field) {
        digit.setOnClickListener(v -> {
            if (AppActivity.isSound()) MediaPlayer.create(this.getContext(), R.raw.bip).start();

            if (AppActivity.isVibrate()) {
                Vibrator vibrator = ((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE));
                int duration = getResources().getInteger(R.integer.vibr_dur_digit);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) vibrator.vibrate(duration);
                else vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
            }

            container.append(digit.getText());
            field.setError(null);
        });
    }

    private boolean isUsernameInvalid(final int length) {
        return length < getContext().getResources().getInteger(R.integer.length_min_username);
    }

    private boolean isPasswordInvalid(final int length) {
        return length != getContext().getResources().getInteger(R.integer.length_min_password);
    }
}
