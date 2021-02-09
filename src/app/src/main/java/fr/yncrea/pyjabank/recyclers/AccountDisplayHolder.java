package fr.yncrea.pyjabank.recyclers;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.models.Account;

public class AccountDisplayHolder extends RecyclerView.ViewHolder {

    private final Context mContext;

    private final ConstraintLayout mContainer;

    private final TextView mName;
    private final TextView mAmount;
    private final TextView mIban;
    private final TextView mCurrency;

    public AccountDisplayHolder(@NonNull final View itemView) {
        super(itemView);

        mContext = itemView.getContext();

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
        mContainer.setBackgroundColor(adapter.getLastHolderId() == getAdapterPosition() ?
                MaterialColors.getColor(mContainer.getRootView(), R.attr.colorBackgroundFloating) :
                Color.TRANSPARENT);

        mName.setText(account.getAccount_name());
        mAmount.setText(account.getAmountStr());
        mCurrency.setText(account.getCurrency());
        mIban.setText(mContext.getString(R.string.item_acc_disp_text_iban, account.getIban()));
    }

    public void setInteractions(final AccountAdapter adapter) {
        if (!mContainer.hasOnClickListeners()) mContainer.setOnClickListener(v -> {
            if (getAdapterPosition() != adapter.getLastHolderId()) {
                if (adapter.getLastHolderId() != -1) {
                    adapter.getLastHolderSelected().getContainer().setBackgroundColor(Color.TRANSPARENT);
                }

                mContainer.setBackgroundColor(MaterialColors.getColor(v, R.attr.colorBackgroundFloating));
                adapter.setLastHolderId(getAdapterPosition());
                adapter.setLastHolderSelected(this);
            }
        });
    }
}
