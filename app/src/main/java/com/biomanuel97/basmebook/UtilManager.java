package com.biomanuel97.basmebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

class UtilManager {

    Context mContext = null;

    private ArrayList<Transaction> mTransactions = new ArrayList<>();
    private ArrayList<Customer> mCustomers = new ArrayList<>();
    private HashMap<String, Customer> customers = new HashMap<>();

    private SQLiteDatabase mReadableDb, mWritableDb;
    private DatabaseDataWorker mDbDataWorker;
    static BasmebookOpenHelper mDbOpenHelper;

    private static boolean isInitialized = false;
    private int generatedCustomerNameCount = 0;
    private static boolean dbClosed = false;


    public static boolean isDbClosed() {
        return dbClosed;
    }

    private static void setDbClosed() {
        dbClosed = true;
    }

    private UtilManager() {

    }

    public static void closeDb() {
        mDbOpenHelper.close();
        setDbClosed();
    }

    public void setupDbs(Context context) {
        setContext(context);
        mDbOpenHelper = new BasmebookOpenHelper(context);
        mReadableDb = mDbOpenHelper.getReadableDatabase();
        mWritableDb = mDbOpenHelper.getWritableDatabase();
        mDbDataWorker = new DatabaseDataWorker(mWritableDb);
    }


    private static final UtilManager ourInstance = new UtilManager();

    static UtilManager getInstance() {
        return ourInstance;
    }
    static UtilManager getInitInstance(Context context) {
        if(!isInitialized) initialize(context);
        return ourInstance;
    }
    static UtilManager getSetupInstance(Context context) {
        // To be called only once, i.e when the app is launched for the first time
        ourInstance.setContext(context);
        ourInstance.enrollKnownCustomers();
        return ourInstance;
    }

    private static void initialize(Context context) {
        //ourInstance.generateSampleTransaction();
        ourInstance.setupDbs(context);
        ourInstance.loadFromDatabase();
        isInitialized = true;
    }

    private void setContext(Context context) {
        mContext = context;
    }

    private void generateSampleTransaction(){
        if(mTransactions.isEmpty()) {
            newTransaction("08135960010", "Beam", "500MB", 1574000977000L);
            newTransaction("08034562195", "Mum", "500MB", 1574212477000L);
            newTransaction("08035557032", "Dad", "500MB", 1574312477000L);
            newTransaction("08103772844", "Tayuuzze", "2000MB", 1574412477000L);
            newTransaction("08069395965", "Kenny", "500MB", 1574512477000L);
            newTransaction("08135960010", "Beam", "500MB", 1574612477000L);
            newTransaction("08103772844", "Tayuuzze", "2000MB", 1575021883000L);
            newTransaction("08135960010", "Beam", "250MB", 1575021863000L);
            newTransaction("08103772879", "Tayuuzze", "2000MB", 1575021883000L);
        }
    }

    ArrayList<Customer> getCustomers() { return mCustomers; }

    ArrayList<Transaction> getAllTransactions(){
        return mTransactions;
    }

    private void enrollKnownCustomers(){
        enrollCustomer("08135960010", "Beam", 1502272777000L);
        enrollCustomer("08103772844", "Tayuuzze", 1515578377000L);
        enrollCustomer("08034562195", "Sweet Mother", 1517479177000L);
        enrollCustomer("09064334575", "Dear Mother", 1533117577000L);
        enrollCustomer("08035557032", "Daddy", 1543658377000L);
        enrollCustomer("08069395965", "Kenny", 1504259977000L);
        enrollCustomer("08069135297", "Dad's", 1525168777000L);
        enrollCustomer("08168996595", "Chicken n Tinz", 1546682377000L);
        enrollCustomer("09069510233", "Taiwo", 1541782777000L);
        enrollCustomer("07038360804", "Bro Destiny", 1549731577000L);
        enrollCustomer("08066473277", "Eunice", 1525514377000L);
        enrollCustomer("08105960447", "Adenike", 1558530080000L);
        enrollCustomer("08037544669", "Mz Treasure", 1541584777000L);
        enrollCustomer("08161181726", "Customer Dami", 1570402320000L);
        enrollCustomer("08166636717", "Falade Timothy", 1538387977000L);
        enrollCustomer("07030104211", "Femi", 1507370377000L);
        enrollCustomer("08143722849", "Sis Toyin", 1518202777000L);
        enrollCustomer("08105522803", "Mr. Onat", 1546448377000L);
        enrollCustomer("08135452868", "Customer 5", 1541422880000L);
        enrollCustomer("09069207135", "Customer 17", 1570395320000L);
    }

    /**<p>this overload is for registering Known Customers only</p>*/
    private void enrollCustomer(String phone, String alias, long initTimeStamp){
        if(alias.equals(" ")) alias = generateCustomerAlias();
        if(!customers.keySet().contains(phone)){
            Customer customer = new Customer(phone, alias, initTimeStamp);
            customers.put(phone, customer);
            mCustomers.add(customer);
        }
    }

    String generateCustomerAlias(){
        String alias = "Customer " + generatedCustomerNameCount++;
        for(Customer customer : mCustomers){
            if(customer.getAlias().equals(alias)) alias = generateCustomerAlias();
        }
        return alias;
    }

    Transaction getTransactionById(long id){
        for (Transaction t :
                mTransactions) {
            if (t.id == id) return t;
        }
        return null;
    }

    Customer getCustomerById(long id){
        for (Customer c :
                mCustomers) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    /**
     * <p>This method updates a transaction or customer in the memory</p>
     */
    private void update(Object o){
        if(o.getClass() == Transaction.class){
            Transaction t = ourInstance.getTransactionById(((Transaction) o).getId());
            int index = t.getIndex();
            mTransactions.remove(index);
            mTransactions.add(index, t);
            Customer transactionCustomer = t.getCustomer();
            transactionCustomer.updateTransaction(t);
            update(transactionCustomer);
        }else if(o.getClass() == Customer.class){
            Customer c = ourInstance.getCustomerById(((Customer) o).getId());
            int index = c.getIndex();
            mCustomers.remove(index);
            mCustomers.add(index, c);
        }
    }

    /** <p> This method is for creating new transactions at runtime</p> */
    void newTransaction(String phone, String alias, String dataQuantity, boolean paid){
        Transaction transaction = new Transaction(phone, alias, dataQuantity, (new Date()).getTime());
        transaction.setPaid(paid);
        if(!setCustomer(transaction)) mDbDataWorker.insertCustomer(transaction.getCustomer());

        // insert into database
        long id = mDbDataWorker.insertTransaction(transaction);
        transaction.setId(id);
        // register to memory
        mTransactions.add(transaction);
    }

    void registerTransaction(String phone, String alias, String dataQuantity, long timeStamp, boolean paid, long id){
        // This method is for loading from SMS or database to memory
        Transaction transaction = new Transaction(phone, alias, dataQuantity, timeStamp, id);
        transaction.setPaid(paid);
        setCustomer(transaction);
        mTransactions.add(transaction);
    }

    /** This method is for loading from <b>SMS to Memory</b> */
    void registerTransaction(String phone, String alias, String dataQuantity, long timeStamp, boolean paid){
        Transaction transaction = new Transaction(phone, alias, dataQuantity, timeStamp);
        transaction.setPaid(paid);
        setCustomer(transaction);
        mTransactions.add(transaction);
    }

    /** This overload is only for <b>generating sample</b> transactions */
    private void newTransaction(String phone, String alias, String dataQuantity, long timeStamp) {
        Transaction transaction = new Transaction(phone, alias, dataQuantity, timeStamp, mTransactions.size());
        setCustomer(transaction);
        mTransactions.add(transaction);
    }

    void editTransaction(long id, String phone, String alias, String dataQuantity, boolean paid){
        // This method is for editing transactions
        Transaction transaction = getTransactionById(id);
        if (alias != null) transaction.setCustomerAlias(alias);
        if (dataQuantity != null) transaction.setDataQuantity(dataQuantity);
        if (phone != null && !transaction.phone.equals(phone)) {
            transaction.setPhone(phone);
            transaction.getCustomer().removeTransaction(transaction);
            setCustomer(transaction);
        }
        transaction.setPaid(paid);

        // Edit in database
        mDbDataWorker.editTransactionInDatabase(mWritableDb, transaction);
        // register to memory
        update(transaction);
    }

    /** This method is for registering Customer at runtime from the new customer interface*/
    void registerCustomer(String phone, String alias){
        Customer existingCustomer = null;
        for (Customer c :
                mCustomers) {
            if (c.getPhone().equals(phone)) existingCustomer = c;
            else if (c.getAlias().equals(alias)) existingCustomer = c;
        }
        // main
        if(existingCustomer != null){
            existingCustomer.setAlias(alias);
            update(existingCustomer);
            mDbDataWorker.editCustomerInDatabase(mWritableDb, existingCustomer);
        }else {
           newCustomer(phone, alias, (new Date()).getTime());
        }
    }

    /**<p>This is the base method for creating customers
     *     and it's also specially for loading from database</p>*/
    Customer newCustomer(String phone, String alias, long initTimeStamp, long id){
        if(alias.equals(" ")) alias = generateCustomerAlias();
        Customer customer = new Customer(phone, alias, initTimeStamp, id);
        if(!containsCustomer(customer)){
            customers.put(phone, customer);
            mCustomers.add(customer);
            return customer;
        }else{
            return customers.get(phone);
        }
    }

    /**<p> This overload is for creating and registering new customers
     * from their first transaction at runtime or the register customer interface</p>*/
    private Customer newCustomer(String phone, String alias, long initTimeStamp){
        long id = mDbDataWorker.insertCustomer(new Customer(phone, alias, initTimeStamp));

        return newCustomer(phone, alias, initTimeStamp, id);
    }

    private boolean containsCustomer(Customer customer){
        for (Customer c :
                mCustomers) {
            if (c.getPhone().equals(customer.getPhone())) return true;
        }
        return false;
    }

    ArrayList<Transaction> getRecentTransactions(){
        ArrayList<Transaction> recent = new ArrayList<>();
        int lastIndex = mTransactions.size() - 1;
        long currentTime = (new Date()).getTime();
        for(int i = 0; i<lastIndex; i++){
            if(mTransactions.size()>0){
                Transaction t = mTransactions.get(lastIndex - i);
                long timeDifference = currentTime - t.getTimeStamp();
                final long oneWeek = 7 * 24 * 60 * 60 * 1000;
                if(timeDifference < oneWeek) recent.add(t);
            }
        }
        return recent;
    }

    /**
     * <p>This method is setting a transactions customer
     * @return true if the customer already exists
     * returns false if the customer is newly created</p>*/
    private boolean setCustomer(Transaction t) {
        String phone = t.getPhone();
        String alias = t.getCustomerAlias();
        if (customers.get(phone) != null){
            t.setCustomer(Objects.requireNonNull(customers.get(phone)));
            return true;
        }else {
            Customer customer = newCustomer(phone, alias, t.getTimeStamp(), mCustomers.size());
            t.setCustomer(customer);
            return false;
        }
    }

    void switchFavorite(Customer customer){
        if (customer.isFavorite) {
            customer.setFavorite(false);
            mDbDataWorker.unStarCustomerInDatabase(mWritableDb, customer);
            update(customer);
        } else if (!customer.isFavorite) {
            customer.setFavorite(true);
            mDbDataWorker.starCustomerInDatabase(mWritableDb, customer);
            update(customer);
        }
    }

    String getMonthAggregate(){
        int aggregate = 0;
        for (Customer c :
                mCustomers) {
            aggregate += c.getMonthAggregateValue();
        }
        return getDataQuantityInString(aggregate);
    }

    String getWeekAggregate(){
        int aggregate = 0;
        for (Transaction t :
                getRecentTransactions()) {
            aggregate +=t.getDataValue();
        }
        return getDataQuantityInString(aggregate);
    }

    static String getDataQuantityInString(int dataQuantity) {
        if (dataQuantity < 10000) return String.valueOf(dataQuantity);
        else return dataQuantity / 1000 + "GB";
    }

    void deleteTransaction(Transaction t){
        if (getTransactionIndex(t) != -1) mTransactions.remove(getTransactionIndex(t));
        t.getCustomer().removeTransaction(t);
        mDbDataWorker.deleteTransactionFromDatabase(mWritableDb, t);
    }

    public void deleteCustomer(Customer c){
        if (getCustomerIndex(c) != -1) mCustomers.remove(getCustomerIndex(c));
        mDbDataWorker.deleteCustomerFromDatabase(mWritableDb, c);
    }

    int getTransactionIndex(Transaction t){
        int index = -1;
        for(int i=0; i<mTransactions.size(); i++){
            if(t.getTimeStamp() == mTransactions.get(i).getTimeStamp()) index = i;
        }
        return index;
    }

    int getCustomerIndex(Customer c){
        int index = -1;
        for(int i=0; i<mCustomers.size(); i++){
            if(c.getPhone().equals(mCustomers.get(i).getPhone())) index = i;
        }
        return index;
    }

    void loadSMS(Context context){
        // Throw an Exception here if the context is not provided
        setContext(context);
        SMSDataWorker.loadSMSTransactions(context);
        ourInstance.setupDbs(context);
    }

    void loadFromDatabase(){
        emptyLists();

        /*
         * Always remember to load customers first before transactions, every time you want to load
         * data to the memory
         */
        mDbDataWorker.loadCustomersFromDatabase(mReadableDb);
        mDbDataWorker.loadTransactionsFromDatabase(mReadableDb);
    }

    private void emptyLists(){
        if(mTransactions.isEmpty() && mCustomers.isEmpty()) return;
        else{
            mCustomers.clear();
            mTransactions.clear();
            customers.clear();
        }
    }
}
