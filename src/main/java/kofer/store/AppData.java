package kofer.store;

import kofer.model.Loan;
import kofer.model.Transaction;

import java.util.List;

public class AppData {
    private List<Transaction> transaction;
    private List<Loan> loan;

    public AppData(List<Transaction> transaction, List<Loan> loan) {
        this.transaction = transaction;
        this.loan = loan;
    }

    public List<Transaction> getTransaction() {
        return transaction;
    }

    public List<Loan> getLoan() {
        return loan;
    }

    public void setTransaction(List<Transaction> transaction) {
        this.transaction = transaction;
    }

    public void setLoan(List<Loan> loan) {
        this.loan = loan;
    }
}
