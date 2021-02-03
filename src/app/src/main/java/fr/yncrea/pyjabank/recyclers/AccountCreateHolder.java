package fr.yncrea.pyjabank.recyclers;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import fr.yncrea.pyjabank.AppActivity;
import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.BankDatabase;
import fr.yncrea.pyjabank.database.models.Account;
import fr.yncrea.pyjabank.services.RestApi;

public class AccountCreateHolder extends RecyclerView.ViewHolder {

    private final TextInputEditText mName;
    private final TextInputEditText mIban;
    private final TextInputEditText mAmount;

    private final TextInputLayout mNameField;
    private final TextInputLayout mIbanField;
    private final TextInputLayout mAmountField;

    private final Spinner mCurrency;

    private final Button mValid;

    public AccountCreateHolder(@NonNull final View itemView) {
        super(itemView);

        mName = itemView.findViewById(R.id.item_acc_crea_text_name_edit);
        mIban = itemView.findViewById(R.id.item_acc_crea_text_iban_edit);
        mAmount = itemView.findViewById(R.id.item_acc_crea_text_amount_edit);

        mNameField = itemView.findViewById(R.id.item_acc_crea_text_name_field);
        mIbanField = itemView.findViewById(R.id.item_acc_crea_text_iban_field);
        mAmountField = itemView.findViewById(R.id.item_acc_crea_text_amount_field);

        mCurrency = itemView.findViewById(R.id.item_acc_crea_spin_curr);

        mValid = itemView.findViewById(R.id.item_acc_crea_button_valid);
    }

    public void setInteractions(final AccountAdapter adapter, final Activity activity) {
        assert mName.getText() != null;
        assert mIban.getText() != null;
        assert mAmount.getText() != null;

        Context context = activity.getApplicationContext();

        //field reactions
        mName.setOnFocusChangeListener((v, focus) -> {
            String msg = null;
            if (!focus && isNameInvalid(mName.getText().length())) {
                msg = "error";
                //msg = context.getString(R.string.frag_conn_error_username);
            }
            mNameField.setError(msg);
        });

        mIban.setOnFocusChangeListener((v, focus) -> {
            String msg = null;
            if (!focus && isIbanInvalid(mIban.getText().length())) {
                msg = "error";
                //msg = context.getString(R.string.frag_conn_error_username);
            }
            mIbanField.setError(msg);
        });

        mAmount.setOnFocusChangeListener((v, focus) -> {
            String msg = null;
            if (!focus && isAmountInvalid(mAmount.getText().toString())) {
                msg = "error";
                //msg = context.getString(R.string.frag_conn_error_username);
            }
            mAmountField.setError(msg);
        });


        mValid.setOnClickListener(v -> {
            boolean isValid = true;
            if (isNameInvalid(mName.getText().length())) {
                isValid = false;
                mNameField.setError("error");//context.getString(R.string.frag_conn_error_username));
            }
            if (isIbanInvalid(mIban.getText().length())) {
                isValid = false;
                mIbanField.setError("error");//context.getString(R.string.frag_conn_error_password));
            }
            if (isAmountInvalid(mAmount.getText().toString())) {
                isValid = false;
                mAmountField.setError("error");//context.getString(R.string.frag_conn_error_password));
            }
            if (!isValid) return;

            Account account = new Account();
            account.setAccount_name(mName.getText().toString());
            account.setIban(mIban.getText().toString());
            account.setAmount(Double.parseDouble(mAmount.getText().toString()));
            account.setCurrency(mCurrency.getSelectedItem().toString());

            mName.getText().clear();
            mIban.getText().clear();
            mAmount.getText().clear();
            mCurrency.setSelection(0);

            adapter.addAccount(account);

            if (AppActivity.isSendOnline()) {
                //new RestApi<>(activity).sendStoreAccount(BankDatabase.getDatabase(), account);
                Toast.makeText(context, "add account online", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNameInvalid(final int length) {
        return length < 4;
    }

    private boolean isIbanInvalid(final int length) {
        return length < 6;
    }

    private boolean isAmountInvalid(final String input) {
        try {
            Double.parseDouble(input);
        }
        catch(Exception ignore) {
            return true;
        }
        return false;
    }
}
