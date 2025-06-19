package kofer.manager;

import kofer.model.Transaction;
import kofer.store.DataStore;
import kofer.util.TransactionType;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionsManager {
    private final DataStore dataStore;

    public TransactionsManager(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void addTransaction(Transaction transaction) {
        dataStore.addTransaction(transaction);
    }

    public List<Transaction> getAllTransaction() {
        return dataStore.getTransactions();
    }

    public List<Transaction> getTransactionsByType(TransactionType type) {
        return dataStore.getTransactions()
                .stream()
                .filter(tx -> tx.getType() == type)
                .collect(Collectors.toList());
    }

    public double getTotalByType(TransactionType type) {
        return dataStore.getTransactions()
                .stream()
                .filter(tx -> tx.getType() == type)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalCredits() {
        return getTotalByType(TransactionType.CREDIT);
    }

    public double getTotalDebits() {
        return getTotalByType(TransactionType.DEBIT);
    }
}
