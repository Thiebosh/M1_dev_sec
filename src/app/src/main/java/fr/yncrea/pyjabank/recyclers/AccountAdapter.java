package fr.yncrea.pyjabank.recyclers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.models.Account;

public class AccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static int TYPE_DISPLAY = 0;
    public final static int TYPE_CREATE = 1;

    private final Activity mActivity;

    private List<Account> mAccounts;

    private final RecyclerView mContainer;
    private AccountDisplayHolder mLastHolderSelected = null;

    public AccountAdapter(final Activity activity, final RecyclerView container, final List<Account> accounts) {
        this.mActivity = activity;
        (this.mContainer = container).setAdapter(this);
        this.setAccounts(accounts);
    }

    public void setAccounts(final List<Account> accounts) {
        if (accounts != null) (mAccounts = accounts).add(new Account(TYPE_CREATE));
        else mAccounts = new ArrayList<>(Collections.singletonList(new Account(TYPE_CREATE)));

        this.notifyDataSetChanged();
        mContainer.scheduleLayoutAnimation();
    }

    protected void addAccount(final Account account) {
        mAccounts.add(mAccounts.size()-1, account);
        this.notifyItemInserted(mAccounts.size()-2);
    }

    public AccountDisplayHolder getLastHolderSelected() {
        return mLastHolderSelected;
    }

    public void setLastHolderSelected(final AccountDisplayHolder holder) {
        mLastHolderSelected = holder;
    }

    @Override
    public int getItemViewType(int position) {
        return mAccounts.get(position).getTypeItem();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        if (viewType == TYPE_DISPLAY) {
            return new AccountDisplayHolder(LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_account_display, parent, false));
        }
        else { //if (viewType == TYPE_CREATE) {
            return new AccountCreateHolder(LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_account_create, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_DISPLAY) {
            ((AccountDisplayHolder) holder).setInitialDisplay(this, mAccounts.get(position));
            ((AccountDisplayHolder) holder).setInteractions(this);
        }
        else if (getItemViewType(position) == TYPE_CREATE) {
            ((AccountCreateHolder) holder).setInteractions(this, mActivity);
        }
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }
}
