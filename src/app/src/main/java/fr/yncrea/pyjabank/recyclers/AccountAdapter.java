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

    private final RecyclerView mContainer;
    private List<Account> mAccounts;

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

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new AccountHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_account, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AccountHolder holder, final int position) {
        holder.setInitialDisplay(mAccounts.get(position));
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }
}
