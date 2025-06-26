package kofer.store;

import kofer.exception.KoferException;
import kofer.model.Loan;
import kofer.model.Transaction;
import kofer.util.Encryption;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The DataStore class provides a centralized storage for managing
 * financial data such as transactions and loans. It includes the
 * functionality to persist and retrieve data securely using encryption.
 */
public class DataStore implements Serializable {
    public static final String APP_DATA_FILE = System.getProperty("user.home") + "/.kofer/kofer.enc/";
    private static String password;

    private final List<Transaction> transactions;
    private final List<Loan> loans;

    public DataStore() throws KoferException {
        if(password == null) {
            throw new KoferException("Password cannot be null");
        }

        try{
            File file = new File(APP_DATA_FILE);
            if(file.exists()){
                DataStore decryptedData = loadData();

                if(decryptedData == null){
                    throw new Exception("Failed to decrypt data store");
                }
                this.transactions = decryptedData.transactions;
                this.loans = decryptedData.loans;
            } else {
                System.out.println("No data store found. Creating new one.");

                this.transactions = new ArrayList<>();
                this.loans = new ArrayList<>();

                saveData();
            }
        } catch (Exception e){
            throw new KoferException("Failed to load data store: " + e.getMessage(), e);
        }

    }

    public static DataStore loadData() throws KoferException {
        File file = new File(APP_DATA_FILE);
        if (!file.exists()) {
            return null;
        }

        try {
            Object decrypted = Encryption.decryptFromFile(password, file);
            if (!(decrypted instanceof DataStore)) {
                throw new KoferException("Corrupted data store: invalid format");
            }
            return (DataStore) decrypted;
        } catch (Exception e) {
            throw new KoferException("Failed to load data store: " + e.getMessage(), e);
        }
    }

    public void saveData() throws KoferException {
        File file = new File(APP_DATA_FILE);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try{
            Encryption.encryptToFile(this, password, file);
        } catch (Exception e){
            throw new KoferException("Failed to save data : " + e.getMessage(), e);
        }
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DataStore.password = password;
    }

    /**
     * Retrieves the list of transactions stored in the data store.
     *
     * @return a list of {@link Transaction} objects representing the financial transactions.
     */
    public List<Transaction> getTransactions() {
        if(transactions.isEmpty()){
            System.out.println("Empty transaction list.");
            return transactions;
        }
        return transactions;
    }

    /**
     * Adds a transaction to the data store. If the transaction is successfully added,
     * the updated data is persisted. In case of a failure during persistence,
     * the transaction is removed from the list.
     *
     * @param transaction the {@link Transaction} object to add. Must not be null.
     * @throws IllegalArgumentException if the provided transaction is null.
     * @throws RuntimeException if there is an error while saving the data to persistent storage.
     */
    public void addTransaction(Transaction transaction) throws IllegalArgumentException, KoferException {
        if(transaction == null){
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        transactions.add(transaction);

        try{
            saveData();
        }catch (Exception e){
            transactions.remove(transaction);
            throw new KoferException("Failed to persist data", e);
        }
    }

    public List<Loan> getLoans() {
        if (loans.isEmpty() ){
            System.out.println("Empty loan list.");
            return loans;
        }
        return loans;
    }

    /**
     * Adds a loan to the data store. If the loan is successfully added, the updated data
     * is persisted. In case of a failure during persistence, the loan is removed from the list.
     *
     * @param loan the {@link Loan} object to add. Must not be null.
     * @throws IllegalArgumentException if the provided loan is null.
     * @throws RuntimeException if there is an error while saving the data to persistent storage.
     */
    public void addLoan(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan cannot be null");
        }

        loans.add(loan);

        try {
            saveData();
        } catch (Exception e) {
            System.err.println("Failed to save loan: " + e.getMessage());
            loans.remove(loan);
            throw new KoferException("Failed to persist loan", e);
        }

    }
}
