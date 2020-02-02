package com.biomanuel97.basmebook;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionListFragment extends Fragment implements DialogHelper.OnTransactionDetailDialogInteractionListener, MainActivity.OnJumpButtonClickListener {
    static final long ALL_TRANSACTIONS_GROUP_ID = -1;
    static final long RECENT_TRANSACTIONS_GROUP_ID= -2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DialogHelper mDialogHelper;
    // TODO: Rename and change types of parameters
    private Group mGroup;
    private long mGroupId;
    private UtilManager mManager;
    private ArrayList<Transaction> mTransactions;
    private RecyclerView mTransactionListRV;
    private LinearLayoutManager mLayoutManager;
    private TransactionAdapter mTransactionsAdapter;
    private TextView mNoTransactionsTV;
    private ProgressBar mLoadingProgress;

    public TransactionListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param group The Group transactions to display.
     * @param groupId The Id of the group of transactions to display.
     *                Should be the id of the customer if the group is CUSTOMER_TRANSACTIONS
     * @return A new instance of fragment TransactionListFragment.
     */
    // TODO: Rename and change types and number of parameters
    static TransactionListFragment newInstance(Group group, long groupId) {
        TransactionListFragment fragment = new TransactionListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, group);
        args.putLong(ARG_PARAM2, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDeletedTransactionDialogCancel() {
        populateList();
        mTransactionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCopyTransactionButtonClickListener(Transaction transaction) {
        mDialogHelper.showEditNewTransactionDialog(transaction, true);
        populateList();
        mTransactionsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean jumpToBottom() {
        if(mTransactions.size() > 1){
            mTransactionListRV.scrollToPosition(mTransactions.size() - 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean jumpToTop() {
        mTransactionListRV.scrollToPosition(0);
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = UtilManager.getInstance();
        mDialogHelper = new DialogHelper(getContext());
        if (getArguments() != null) {
            mGroup = (Group) getArguments().getSerializable(ARG_PARAM1);
            mGroupId = getArguments().getLong(ARG_PARAM2);

            populateList();
        }
    }

    private void populateList() {
//        if(mTransactions != null) mTransactions.clear();
        if(mGroup == Group.ALL_TRANSACTIONS || mGroupId == ALL_TRANSACTIONS_GROUP_ID)
            mTransactions = mManager.getAllTransactions();
        else if (mGroup == Group.RECENT_TRANSACTIONS || mGroupId == RECENT_TRANSACTIONS_GROUP_ID)
            mTransactions = mManager.getRecentTransactions();
        else if (mGroup == Group.CUSTOMER_TRANSACTIONS) {
            Customer customer = mManager.getCustomerById(mGroupId);
            mTransactions = customer.getTransactions();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        mNoTransactionsTV = fragView.findViewById(R.id.tv_no_transactions_to_display);
        mTransactionListRV = fragView.findViewById(R.id.transaction_list_rv);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mTransactionsAdapter = new TransactionAdapter(mTransactions);

        mTransactionListRV.setLayoutManager(mLayoutManager);

        mTransactionsAdapter.setItemClickListener(new TransactionAdapter.ClickListener() {
            @Override
            public void onItemClick(int clickedTransactionPosition, View v) {
                mDialogHelper.showTransactionDetails(mTransactions.get(clickedTransactionPosition));
                mDialogHelper.setOnTransactionDetailDialogInteractionListener(TransactionListFragment.this);
            }
        });
        mTransactionsAdapter.setItemLongClickListener(new TransactionAdapter.LongClickListener() {
            @Override
            public void onItemLongClick(int clickedTransactionPosition, View v) {
                mDialogHelper.showEditNewTransactionDialog(mTransactions.get(clickedTransactionPosition), false);
                mTransactionsAdapter.notifyItemChanged(clickedTransactionPosition);
            }
        });

        String emptyTransactionLisText = getContext().getString(R.string.no_recent_transactions);

        if (mTransactions.size() < 1) {
            mNoTransactionsTV.setVisibility(View.VISIBLE);
            mNoTransactionsTV.setText(emptyTransactionLisText);
        }
        mTransactionListRV.setAdapter(mTransactionsAdapter);
        if (mGroup == Group.ALL_TRANSACTIONS && mTransactions.size() > 9)
            mTransactionListRV.scrollToPosition(mTransactions.size() - 1);

        mLoadingProgress = fragView.findViewById(R.id.pb_loading);
        final SwipeRefreshLayout rootView = fragView.findViewById(R.id.pull_to_refresh);
        rootView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateList();
                new CountDownTimer(3000,1000){
                    public void onFinish(){
                        rootView.setRefreshing(false);
                        mTransactionsAdapter.notifyDataSetChanged();
                    }
                    public void onTick(long millisUntilFinished){

                    }
                }.start();
            }
        });


        return fragView;
    }

    public enum Group{
        ALL_TRANSACTIONS,
        RECENT_TRANSACTIONS,
        CUSTOMER_TRANSACTIONS
    }
}
