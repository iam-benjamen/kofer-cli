package kofer.store;

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

    private final List<Transaction> transactions;
    private final List<Loan> loans;

    public DataStore(String password) throws Exception {
        DataStore loadedData = loadData(password);

        if(loadedData != null){
            this.transactions = loadedData.transactions;
            this.loans = loadedData.loans;
        } else {
            this.transactions = new ArrayList<>();
            this.loans = new ArrayList<>();
        }
    }

    public static DataStore loadData(String password) throws Exception {
        try{
            File file = new File(APP_DATA_FILE);

            if (file.exists()) {
                return (DataStore) Encryption.decryptFromFile(password, file);
            } else {
                System.out.println("No data found. Creating new data store.");
                file.getParentFile().mkdirs();
            }

        } catch (Exception e){
            System.err.println("Failed to load data: " + e.getMessage());
        }

        return null;
    }

    public void saveData(String password) throws Exception {
        File file = new File(APP_DATA_FILE);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        Encryption.encryptToFile(this, password, file);
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void addLoan(Loan loan) {
        loans.add(loan);
    }
}
