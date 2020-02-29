package com.biomanuel97.basmebook;

import android.content.Intent;
import android.os.Bundle;

import com.biomanuel97.basmebook.TransactionListFragment.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.biomanuel97.basmebook.TransactionListFragment.ALL_TRANSACTIONS_GROUP_ID;
import static com.biomanuel97.basmebook.TransactionListFragment.RECENT_TRANSACTIONS_GROUP_ID;

public class MainActivity extends AppCompatActivity {

    // region Declarations

    private UtilManager mManager;

    private DialogHelper mDialogHelper;
    public static final int X = 1;

    private Menu mMenu;
    private SpeedDialView mSpeedDialView;
    private Toolbar mToolbar;
    private TextView mTvWeekAgg;
    private TextView mTvMonthAgg;
    private ImageButton mBtnDownwardScroll;
    private ImageButton mBtnUpwardScroll;
    private TransactionListFragment mRecentTransactionsListFragment;
    private TransactionListFragment mAllTransactionsListFragment;
    private FragmentManager mFragmentManager;
    OnJumpButtonClickListener mOnJumpButtonClickListener;
    private Boolean mDbClosed;

    //endregion Declaration

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilManager.closeDb();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mManager.getAllTransactions().size() == 0){
            mManager = null;
            mManager = UtilManager.getInitInstance(this.getApplicationContext());
        }

        mTvWeekAgg.setText(mManager.getWeekAggregate());
        mTvMonthAgg.setText(mManager.getMonthAggregate());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mManager = UtilManager.getInstance();

        setContentView(R.layout.activity_main);
        setupToolbar();

        mDialogHelper = new DialogHelper(this);

        mAllTransactionsListFragment = TransactionListFragment
                .newInstance(Group.ALL_TRANSACTIONS, ALL_TRANSACTIONS_GROUP_ID);
        mRecentTransactionsListFragment = TransactionListFragment
                .newInstance(Group.RECENT_TRANSACTIONS, RECENT_TRANSACTIONS_GROUP_ID);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction FT = mFragmentManager.beginTransaction().add(R.id.fragment_container, mRecentTransactionsListFragment);
        setOnJumpButtonClickListener(mRecentTransactionsListFragment);
        FT.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        FT.commit();

        addNewTransactionInterface();
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mTvWeekAgg = findViewById(R.id.txt_weekly_data_sales);
        mTvMonthAgg = findViewById(R.id.txt_monthly_data_sales);
        mBtnDownwardScroll = findViewById(R.id.btn_fast_scroll_transaction_to_bottom);
        mBtnUpwardScroll = findViewById(R.id.btn_fast_scroll_transaction_to_top);

        mBtnDownwardScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnJumpButtonClickListener.jumpToBottom()) {
                    mBtnDownwardScroll.setVisibility(View.GONE);
                    mBtnUpwardScroll.setVisibility(View.VISIBLE);
                }
            }
        });
        mBtnUpwardScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnJumpButtonClickListener.jumpToTop()) {
                    mBtnUpwardScroll.setVisibility(View.GONE);
                    mBtnDownwardScroll.setVisibility(View.VISIBLE);
                }
            }
        });

//        mTvWeekAgg.setText(mManager.getWeekAggregate());
//        mTvMonthAgg.setText(mManager.getMonthAggregate());
        setSupportActionBar(mToolbar);
    }

    private void addNewTransactionInterface() {
        mSpeedDialView = (SpeedDialView) findViewById(R.id.speedDial);
        mSpeedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.record_transaction, R.drawable.ic_playlist_add_white)
                        .setLabel(R.string.record_transaction)
                        .setFabBackgroundColor(getApplicationContext().getColor(R.color.colorPrimary))
                        .setFabSize(FloatingActionButton.SIZE_NORMAL)
                        .setLabel(R.string.new_transaction)
                        .create());

        mSpeedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.new_transaction, R.drawable.ic_mode_edit_white_24dp)
                        .setFabBackgroundColor(getApplicationContext().getColor(R.color.colorPrimary))
                        .setFabSize(FloatingActionButton.SIZE_NORMAL)
                        .setLabel(R.string.record_transaction)
                        .create());


        // Set main action clickListener.
        mSpeedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {

                return false;
                // True to keep the Speed Dial open
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
                Log.d("com.biomanuel97.basmebook.MainActivity", "Speed dial toggle state changed. Open " + isOpen);
            }
        });

        mSpeedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.new_transaction:
                        Toast.makeText(MainActivity.this, "Main action clicked!", Toast.LENGTH_LONG).show();
                        mSpeedDialView.close();
                        return true;
                    case R.id.record_transaction:
                        mDialogHelper.showAddNewTransactionDialog();
                        mSpeedDialView.close();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_all || id == R.id.action_recent) {
            if (id == R.id.action_all) {
                showAllTransactions(item);
                return true;
            } else {
                showRecentTransactions(item);
                return true;
            }
        } else if (id == R.id.action_customer) {
            goToCustomerActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToCustomerActivity() {
        startActivity(new Intent(this, CustomerActivity.class));
    }

    private void showAllTransactions(MenuItem item) {
        FragmentTransaction FT = mFragmentManager.beginTransaction();
        FT.replace(R.id.fragment_container, mAllTransactionsListFragment);
        setOnJumpButtonClickListener(mAllTransactionsListFragment);
        //            showAllTransactions();
        item.setVisible(false);
        ((MenuItem) mMenu.findItem(R.id.action_recent)).setVisible(true);
        mBtnDownwardScroll.setClickable(true);
        mBtnUpwardScroll.setClickable(true);
        FT.commit();
    }

    private void showRecentTransactions(MenuItem item) {

        setOnJumpButtonClickListener(mRecentTransactionsListFragment);
        FragmentTransaction FT = mFragmentManager.beginTransaction();
        FT.replace(R.id.fragment_container, mRecentTransactionsListFragment);
        //            showRecentTransactions();
        item.setVisible(false);
        ((MenuItem) mMenu.findItem(R.id.action_all)).setVisible(true);
        FT.commit();
    }

    public interface OnJumpButtonClickListener {
        boolean jumpToBottom();
        boolean jumpToTop();
    }

    public void setOnJumpButtonClickListener(OnJumpButtonClickListener onJumpButtonClickListener) {
        mOnJumpButtonClickListener = null;
        mOnJumpButtonClickListener = onJumpButtonClickListener;
    }
}
