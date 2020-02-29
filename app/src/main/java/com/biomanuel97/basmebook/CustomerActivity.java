package com.biomanuel97.basmebook;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class CustomerActivity extends AppCompatActivity {

    private static int FAB_STATE_CONSTANT = 0;
    private RecyclerView mCustomerRV;
    UtilManager mManager;
    private DialogHelper mDialogHelper;
    private CustomerAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private FloatingActionButton mFab;
    private Menu mMenu;
    private Toolbar mToolbar;
    private TextView mTvWeekAgg;
    private TextView mTvMonthAgg;
    private ImageButton mBtnDownwardScroll;
    private ImageButton mBtnUpwardScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = UtilManager.getInstance();

        setContentView(R.layout.activity_customer);
        setupToolbar();

        mDialogHelper = new DialogHelper(this);

        mCustomerRV = findViewById(R.id.customers_rv);
        mAdapter = new CustomerAdapter(mManager.getCustomers());
        mLayoutManager = new GridLayoutManager(this, 2);
        mAdapter.setCardClickListeners(new CustomerAdapter.CardClickListeners() {
            @Override
            public void onClickListener(Customer customer, View v) {

            }

            @Override
            public void onLongClickListener(Customer customer, View v) {

            }
        });

        mCustomerRV.setAdapter(mAdapter);
        mCustomerRV.setLayoutManager(mLayoutManager);
        mCustomerRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(FAB_STATE_CONSTANT == 0) {
                    mFab.hide();
                    FAB_STATE_CONSTANT = 1;
                }
                else if(FAB_STATE_CONSTANT == 1){
                    mFab.show();
                    FAB_STATE_CONSTANT = 0;
                }
            }
        });

        mFab = findViewById(R.id.reg_customer_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialogHelper.showRegisterNewCustomerDialog();
                mDialogHelper.setOnDialogCancelNotifier(new DialogHelper.OnDialogCancelNotifier() {
                    @Override
                    public void notifyDialogCancel() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.actions_to_transactions) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToTransactionActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mTvWeekAgg = findViewById(R.id.txt_weekly_data_sales);
        mTvMonthAgg = findViewById(R.id.txt_monthly_data_sales);
        mBtnDownwardScroll = findViewById(R.id.btn_fast_scroll_transaction_to_bottom);
        mBtnUpwardScroll = findViewById(R.id.btn_fast_scroll_transaction_to_top);

        mBtnDownwardScroll.setVisibility(View.GONE);
        mBtnUpwardScroll.setVisibility(View.GONE);
        mBtnDownwardScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomerRV.smoothScrollToPosition(mManager.getCustomers().size() - 1);
                mBtnDownwardScroll.setVisibility(View.GONE);
                mBtnUpwardScroll.setVisibility(View.VISIBLE);
            }
        });
        mBtnUpwardScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomerRV.smoothScrollToPosition(0);
                mBtnUpwardScroll.setVisibility(View.GONE);
                mBtnDownwardScroll.setVisibility(View.VISIBLE);
            }
        });

        mTvWeekAgg.setText(mManager.getWeekAggregate());
        mTvMonthAgg.setText(mManager.getMonthAggregate());
        setSupportActionBar(mToolbar);
    }

}
