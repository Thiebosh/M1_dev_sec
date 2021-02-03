package fr.yncrea.pyjabank.recyclers;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.models.Account;

public class AccountAdapter extends RecyclerView.Adapter<AccountHolder> {

    private List<Account> mAccounts;

    private final RecyclerView mContainer;
    private AccountHolder mLastHolderSelected = null;

    public AccountAdapter(final RecyclerView container, final List<Account> accounts) {
        this.mContainer = container;
        this.mContainer.setAdapter(this);
        this.setAccounts(accounts);
    }

    public void setAccounts(final List<Account> accounts) {
        mAccounts = (accounts != null ? accounts : new ArrayList<>());
        this.notifyDataSetChanged();
        mContainer.scheduleLayoutAnimation();
    }

    public AccountHolder getLastHolderSelected() {
        return mLastHolderSelected;
    }

    public void setLastHolderSelected(final AccountHolder holder) {
        mLastHolderSelected = holder;
    }

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new AccountHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_account_display, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AccountHolder holder, final int position) {
        holder.setInitialDisplay(this, mAccounts.get(position));
        holder.setInteractions(this);
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }
}
