package fr.yncrea.pyjabank.recyclers;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.models.Account;

public class AccountHolder extends RecyclerView.ViewHolder {

    private final ConstraintLayout mContainer;

    private final TextView mName;
    private final TextView mAmount;
    private final TextView mIban;
    private final TextView mCurrency;

    public AccountHolder(@NonNull final View itemView) {
        super(itemView);

        mContainer = itemView.findViewById(R.id.item_acc_disp_container);

        mName = itemView.findViewById(R.id.item_acc_disp_text_name);
        mAmount = itemView.findViewById(R.id.item_acc_disp_text_amount);
        mCurrency = itemView.findViewById(R.id.item_acc_disp_text_currency);
        mIban = itemView.findViewById(R.id.item_acc_disp_text_iban);
    }
    
    public ConstraintLayout getContainer() {
        return mContainer;
    }

    public void setInitialDisplay(final AccountAdapter adapter, final Account account) {
        if (this == adapter.getLastHolderSelected()) {//pos verif pour extremes
            mContainer.callOnClick();
        }

        mName.setText(account.getAccount_name());
        mAmount.setText(account.getAmountStr());
        mCurrency.setText(account.getCurrency());
        mIban.setText(account.getIban());
    }

    public void setInteractions(final AccountAdapter adapter) {
        mContainer.setOnClickListener(v -> {
            if (this != adapter.getLastHolderSelected()) {
                if (adapter.getLastHolderSelected() != null) {
                    adapter.getLastHolderSelected().getContainer().setBackgroundColor(Color.TRANSPARENT);
                }

                mContainer.setBackgroundColor(MaterialColors.getColor(v, R.attr.colorBackgroundFloating));
                adapter.setLastHolderSelected(this);
            }
        });
    }
}
