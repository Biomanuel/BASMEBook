package com.biomanuel97.basmebook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.biomanuel97.basmebook.databinding.ItemTransactionListBinding;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    ArrayList<Transaction> mTransactions;
    private Context mContext;
    private ClickListener clickListener;
    private LongClickListener longClickListener;

    public TransactionAdapter(ArrayList<Transaction> transactions) {
        mTransactions = transactions;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ItemTransactionListBinding itemTransactionListBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_transaction_list, parent, false);
        return new ViewHolder(itemTransactionListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        Transaction currentTransaction = mTransactions.get(position);
        holder.itemViewBinding.setTransaction(currentTransaction);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ItemTransactionListBinding itemViewBinding;

        public ViewHolder(@NonNull ItemTransactionListBinding binding) {
            super(binding.getRoot());
            this.itemViewBinding = binding;
            itemViewBinding.getRoot().setOnClickListener(this);
            itemViewBinding.getRoot().setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            longClickListener.onItemLongClick(getAdapterPosition(), v);
            return true;
        }
    }

    public void setItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
    public void setItemLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public interface ClickListener{
        void onItemClick(int clickedTransactionPosition, View v);
    }
    public interface LongClickListener{
        void onItemLongClick(int clickedTransactionPosition, View v);
    }
}
