package kofer.util;

public enum TransactionType {
    CREDIT, DEBIT, LOAN;

    public String toUpperCase() {
        return this.name();
    }
}
