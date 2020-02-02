package com.biomanuel97.basmebook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

final class DialogHelper {

    // region Declarations
    private AlertDialog mNewTransactionDialogObject;
    private AlertDialog mEditTransactionDialogObject;
    private AlertDialog mTransactionDetailsDialogObject;
    private Context mContext;
    private Spinner mDataQuantitySpinner;
    private UtilManager mManager;
    private AlertDialog mRegisterCustomerDialogObject;

    private OnTransactionDetailDialogInteractionListener mOnTransactionDetailDialogInteractionListener;
    private OnDialogCancelNotifier mOnDialogCancelNotifier;

    //endregion Declarations

    DialogHelper(Context context){
        mContext = context;
        mManager = UtilManager.getInstance();
    }

    // region Builders
    private void buildNewTransactionDialog() {
        View newTransactionDialogView = LayoutInflater.from(mContext).inflate(R.layout.new_transaction_dialog, null, false);
        mDataQuantitySpinner = newTransactionDialogView.findViewById(R.id.data_quantity_spinner_new_transaction_dialog);
        String[] dataQuantities = {"500MB", "1000MB", "2000MB", "3000MB", "4000MB", "5000MB"};
        ArrayAdapter<String> adapterDataQuantity = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, dataQuantities);
        adapterDataQuantity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDataQuantitySpinner.setAdapter(adapterDataQuantity);

        final AlertDialog.Builder newTransactionDialogBuilder = new AlertDialog.Builder(mContext);

        newTransactionDialogBuilder.setView(newTransactionDialogView);
        final EditText etPhone = newTransactionDialogView.findViewById(R.id.et_phone_transaction_dialog);
        final EditText etAlias = newTransactionDialogView.findViewById(R.id.et_alias_new_transaction_dialog);
        final CheckBox paidCheckBox = newTransactionDialogView.findViewById(R.id.paid_checkBox);
        final String autoAlias = mManager.generateCustomerAlias();
        etAlias.setAutofillHints(autoAlias);

        Button submitButton = newTransactionDialogView.findViewById(R.id.btn_submit_new_transaction_dialog);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(String.valueOf(etPhone.getText())).equals("")) {
                    String phoneNumber = etPhone.getText().toString();
                    String customerAlias = etAlias.getText().toString().equals("") ? autoAlias : etAlias.getText().toString();
                    String dataQuantity = mDataQuantitySpinner.getSelectedItem().toString();
                    mManager.newTransaction(phoneNumber, customerAlias, dataQuantity, paidCheckBox.isChecked());
                    mNewTransactionDialogObject.cancel();
                }else Toast.makeText(mContext, "Please fill in the Phone Number", Toast.LENGTH_SHORT).show();
            }
        });

        mNewTransactionDialogObject = newTransactionDialogBuilder.create();
        mNewTransactionDialogObject.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                mRecentTransactions = mManager.getRecentTransactions();
//                mRecentTransactionsAdapter.notifyDataSetChanged();
                mNewTransactionDialogObject = null;
            }
        });

    }
    
    private void buildEditTransactionDialog(final Transaction transaction, final boolean copy) {
        View mEditTransactionDialogView = LayoutInflater.from(mContext).inflate(R.layout.new_transaction_dialog, null, false);
        mDataQuantitySpinner = mEditTransactionDialogView.findViewById(R.id.data_quantity_spinner_new_transaction_dialog);
        String[] dataQuantities = {"500MB", "1000MB", "2000MB", "3000MB", "4000MB", "5000MB"};
        ArrayAdapter<String> adapterDataQuantity = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, dataQuantities);
        adapterDataQuantity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDataQuantitySpinner.setAdapter(adapterDataQuantity);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);

        dialogBuilder.setView(mEditTransactionDialogView);

        final EditText etPhone = mEditTransactionDialogView.findViewById(R.id.et_phone_transaction_dialog);
        final EditText etAlias = mEditTransactionDialogView.findViewById(R.id.et_alias_new_transaction_dialog);
        final CheckBox paidCheckBox = mEditTransactionDialogView.findViewById(R.id.paid_checkBox);
        Button submitButton = mEditTransactionDialogView.findViewById(R.id.btn_submit_new_transaction_dialog);

        submitButton.setText(mContext.getResources().getString(R.string.btn_save));
        mDataQuantitySpinner.setSelection((getPositionInArray(dataQuantities, transaction.getDataQuantity())));
        etPhone.setText(transaction.getPhone());
        etAlias.setText(transaction.getCustomerAlias());
        paidCheckBox.setChecked(transaction.isPaid());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (copy){
                    if (!(String.valueOf(etPhone.getText())).equals("")) {
                        String phoneNumber = etPhone.getText().toString();
                        String customerAlias = etAlias.getText().toString().equals("") ? transaction.getCustomerAlias() : etAlias.getText().toString();
                        String dataQuantity = mDataQuantitySpinner.getSelectedItem().toString();
                        mManager.newTransaction(phoneNumber, customerAlias, dataQuantity, paidCheckBox.isChecked());
                        mEditTransactionDialogObject.cancel();
                    }else Toast.makeText(mContext, "Please fill in the Phone Number", Toast.LENGTH_SHORT).show();
                }else{
                if (!(String.valueOf(etPhone.getText())).equals("")) {
                    String phoneNumber = etPhone.getText().toString();
                    String customerAlias = etAlias.getText().toString().equals("") ? transaction.getCustomerAlias() : etAlias.getText().toString();
                    String dataQuantity = mDataQuantitySpinner.getSelectedItem().toString();
                    mManager.editTransaction(transaction.getId(), phoneNumber, customerAlias, dataQuantity, paidCheckBox.isChecked());
                    mEditTransactionDialogObject.cancel();
                }else Toast.makeText(mContext, "Please fill in the Phone Number", Toast.LENGTH_SHORT).show();
            }}
        });

        mEditTransactionDialogObject = dialogBuilder.create();
        mEditTransactionDialogObject.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                mRecentTransactions = mManager.getRecentTransactions();
//                mRecentTransactionsAdapter.notifyDataSetChanged();
                mEditTransactionDialogObject = null;
            }
        });

    }

    private void buildTransactionDetailsDialog(final Transaction transaction) {
        View transactionDetailsDialogView = LayoutInflater.from(mContext).inflate(
                R.layout.transaction_details_dialog, null, false);

        final AlertDialog.Builder transactionDetailsBuilder = new AlertDialog.Builder(mContext);
//        changeNameBuilder.setTitle("Change Room Name");

        TextView tvDataQuantity = transactionDetailsDialogView.findViewById(R.id.txt_data_value);
        TextView tvPhone = transactionDetailsDialogView.findViewById(R.id.txt_phone);
        TextView tvAlias = transactionDetailsDialogView.findViewById(R.id.txt_alias);
        CheckBox paidCheckbox = transactionDetailsDialogView.findViewById(R.id.paid_checkBox);
        ImageButton copyTransactionBtn = transactionDetailsDialogView.findViewById(R.id.btn_copy_transaction);

        tvDataQuantity.setText(transaction.getDataQuantity());
        tvAlias.setText(transaction.getCustomerAlias());
        tvPhone.setText(transaction.getPhone());
        paidCheckbox.setChecked(transaction.isPaid());
        paidCheckbox.setEnabled(false);

        transactionDetailsBuilder.setView(transactionDetailsDialogView);
        ImageButton deleteButton = transactionDetailsDialogView.findViewById(R.id.btn_delete_transaction);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.deleteTransaction(transaction);
                mTransactionDetailsDialogObject.cancel();
                mOnTransactionDetailDialogInteractionListener.onDeletedTransactionDialogCancel();
            }
        });
        copyTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTransactionDetailsDialogObject.cancel();
                mOnTransactionDetailDialogInteractionListener.onCopyTransactionButtonClickListener(transaction);
            }
        });

        mTransactionDetailsDialogObject = transactionDetailsBuilder.create();
        mTransactionDetailsDialogObject.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mTransactionDetailsDialogObject = null;
            }
        });
    }

    private void buildRegisterCustomerDialog(){
        View registerCustomerDialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.register_customer_dialog_view, null, false);

        AlertDialog.Builder registerCustomerDialogBuilder = new AlertDialog.Builder(mContext);

        registerCustomerDialogBuilder.setView(registerCustomerDialogView);
        final EditText etPhone = registerCustomerDialogView.findViewById(R.id.et_phone);
        final EditText etAlias = registerCustomerDialogView.findViewById(R.id.et_alias);
        Button registerButton = registerCustomerDialogView.findViewById(R.id.btn_register_customer);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = String.valueOf(etPhone.getText());
                String alias = String.valueOf(etAlias.getText());

                mManager.registerCustomer(phone, alias);
                mRegisterCustomerDialogObject.cancel();
            }
        });
        mRegisterCustomerDialogObject = registerCustomerDialogBuilder.create();

        mRegisterCustomerDialogObject.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mRegisterCustomerDialogObject = null;
                mOnDialogCancelNotifier.notifyDialogCancel();
            }
        });
    }
    //endregion Builders

    // region Showers
    void showTransactionDetails(Transaction transaction){
        buildTransactionDetailsDialog(transaction);
        mTransactionDetailsDialogObject.show();
    }

    void showAddNewTransactionDialog(){
        buildNewTransactionDialog();
        mNewTransactionDialogObject.show();
    }

    void showEditNewTransactionDialog(Transaction transaction, boolean copy){
        buildEditTransactionDialog(transaction, copy);
        mEditTransactionDialogObject.show();
    }

    void showRegisterNewCustomerDialog(){
        buildRegisterCustomerDialog();
        mRegisterCustomerDialogObject.show();
    }

    //endregion Showers

    // region Utils
    private int getPositionInArray(String[] dataQuantities, String dataQuantity) {
        int i = 0;
        for (String s : dataQuantities) {
            if (dataQuantity.equals(s)){
                return i;
            }else ++i;
        }
        return 0;
    }

    void setOnTransactionDetailDialogInteractionListener(OnTransactionDetailDialogInteractionListener listener){
        this.mOnTransactionDetailDialogInteractionListener = listener;
    }

    void setOnDialogCancelNotifier(OnDialogCancelNotifier onDialogCancelNotifier){
        this.mOnDialogCancelNotifier = onDialogCancelNotifier;
    }

    public interface OnTransactionDetailDialogInteractionListener {
        void onDeletedTransactionDialogCancel();
        void onCopyTransactionButtonClickListener(Transaction transaction);
    }

    public interface OnDialogCancelNotifier{
        void notifyDialogCancel();
    }
    //endregion Utils
}
