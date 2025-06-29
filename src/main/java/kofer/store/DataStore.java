package kofer.store;

import kofer.exception.KoferException;
import kofer.model.Loan;
import kofer.model.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The DataStore class provides a centralized storage for managing
 * financial data such as transactions and loans. It includes the
 * functionality to persist and retrieve data using standard serialization.
 */
public class DataStore implements Serializable {
    public static final String APP_DATA_FILE = System.getProperty("user.home") + "/.kofer/kofer.dat";

    private final List<Transaction> transactions;
    private final List<Loan> loans;

    public DataStore() throws KoferException {
        try {
            File file = new File(APP_DATA_FILE);
            if (file.exists()) {
                DataStore loadedData = loadData();

                if (loadedData == null) {
                    throw new Exception("Failed to load data store");
                }
                this.transactions = loadedData.transactions;
                this.loans = loadedData.loans;
            } else {
                System.out.println("No data store found. Creating new one.");

                this.transactions = new ArrayList<>();
                this.loans = new ArrayList<>();

                saveData();
            }
        } catch (Exception e) {
            throw new KoferException("Failed to load data store: " + e.getMessage(), e);
        }
    }

    public static DataStore loadData() throws KoferException {
        File file = new File(APP_DATA_FILE);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object loaded = ois.readObject();
            if (!(loaded instanceof DataStore)) {
                throw new KoferException("Corrupted data store: invalid format");
            }
            return (DataStore) loaded;
        } catch (Exception e) {
            throw new KoferException("Failed to load data store: " + e.getMessage(), e);
        }
    }

    public void saveData() throws KoferException {
        File file = new File(APP_DATA_FILE);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        } catch (Exception e) {
            throw new KoferException("Failed to save data: " + e.getMessage(), e);
        }
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
