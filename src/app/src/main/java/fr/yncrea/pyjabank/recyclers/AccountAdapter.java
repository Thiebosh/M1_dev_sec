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

    public AccountAdapter(List<Account> accounts) {
        this.setAccounts(accounts);
    }

    public void setAccounts(List<Account> accounts) {
        mAccounts = (accounts != null ? accounts : new ArrayList<>());
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
        holder.setInitialDisplay(mAccounts.get(position));
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }
}
