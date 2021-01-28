package fr.yncrea.pyjabank.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.models.Account;

public class AccountHolder extends RecyclerView.ViewHolder {

    private final TextView mName;
    private final TextView mAmount;
    private final TextView mIban;
    private final TextView mCurrency;

    public AccountHolder(@NonNull View itemView) {
        super(itemView);

        mName = itemView.findViewById(R.id.item_acc_text_name);
        mAmount = itemView.findViewById(R.id.item_acc_text_amount);
        mCurrency = itemView.findViewById(R.id.item_acc_text_currency);
        mIban = itemView.findViewById(R.id.item_acc_text_iban);
    }

    public void setInitialDisplay(Account account) {
        mName.setText(account.getAccount_name());
        mAmount.setText(account.getAmountStr());
        mCurrency.setText(account.getCurrency());
        mIban.setText(account.getIban());
    }
}
