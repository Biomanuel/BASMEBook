package com.biomanuel97.basmebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.biomanuel97.basmebook.databinding.ItemCustomerCardBinding;
import com.biomanuel97.basmebook.databinding.ItemTransactionListBinding;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    ArrayList<Customer> mCustomers;
    private Context mContext;
    CardClickListeners mCardClickListeners;

    public CustomerAdapter(ArrayList<Customer> customers) {
        mCustomers = customers;
    }

    @NonNull
    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        ItemCustomerCardBinding itemCustomerCardBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_customer_card, parent, false);
        return new ViewHolder(itemCustomerCardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.ViewHolder holder, int position) {
        Customer currentTransaction = mCustomers.get(position);
        holder.itemViewBinding.setCustomer(currentTransaction);
    }

    @Override
    public int getItemCount() {
        return mCustomers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ItemCustomerCardBinding itemViewBinding;

        public ViewHolder(@NonNull ItemCustomerCardBinding binding) {
            super(binding.getRoot());
            this.itemViewBinding = binding;
            itemViewBinding.getRoot().setOnLongClickListener(this);
            itemViewBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCardClickListeners.onClickListener(itemViewBinding.getCustomer(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            mCardClickListeners.onLongClickListener(itemViewBinding.getCustomer(), v);
            return false;
        }
    }

    public void setCardClickListeners(CardClickListeners cardClickListeners) {
        mCardClickListeners = cardClickListeners;
    }

    public interface CardClickListeners{
        void onClickListener(Customer customer, View v);
        void onLongClickListener(Customer customer, View v);
    }
}
