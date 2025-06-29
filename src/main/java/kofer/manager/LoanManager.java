package kofer.manager;

import kofer.exception.KoferException;
import kofer.exception.ValidationException;
import kofer.model.Loan;
import kofer.model.Repayment;
import kofer.store.DataStore;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * LoanManager handles all loan-related operations including adding loans,
 * processing repayments, and retrieving loan information.
 */
public class LoanManager {
    private final DataStore dataStore;

    public LoanManager(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Add a new loan to the system
     */
    public void addLoan(Loan loan) {
        if (loan == null) {
            throw new ValidationException("Loan cannot be null");
        }
        dataStore.addLoan(loan);
    }

    /**
     * Create and add a new loan
     */
    public Loan createLoan(String lenderName, double amount, LocalDate dateBorrowed, String description) {
        if (lenderName == null || lenderName.trim().isEmpty()) {
            throw new ValidationException("lender", lenderName, "non-empty string");
        }
        if (amount <= 0) {
            throw new ValidationException("amount", String.valueOf(amount), "positive number");
        }
        
        Loan loan = new Loan(lenderName, amount, dateBorrowed, description);
        addLoan(loan);
        return loan;
    }

    /**
     * Find a loan by its ID
     */
    public Optional<Loan> findLoanById(String loanId) {
        if (loanId == null || loanId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return dataStore.getLoans()
                .stream()
                .filter(loan -> loan.getId().equals(loanId))
                .findFirst();
    }

    /**
     * Get all loans
     */
    public List<Loan> getAllLoans() {
        return dataStore.getLoans();
    }

    /**
     * Get all active (not closed) loans
     */
    public List<Loan> getActiveLoans() {
        return dataStore.getLoans()
                .stream()
                .filter(loan -> !loan.isClosed())
                .collect(Collectors.toList());
    }

    /**
     * Get all closed loans
     */
    public List<Loan> getClosedLoans() {
        return dataStore.getLoans()
                .stream()
                .filter(Loan::isClosed)
                .collect(Collectors.toList());
    }

    /**
     * Get loans by lender name
     */
    public List<Loan> getLoansByLender(String lenderName) {
        if (lenderName == null || lenderName.trim().isEmpty()) {
            return List.of();
        }
        
        return dataStore.getLoans()
                .stream()
                .filter(loan -> loan.getLenderName().equalsIgnoreCase(lenderName.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Add a repayment to a specific loan
     */
    public void addRepayment(String loanId, double amount, LocalDate date, String note) {
        Optional<Loan> loanOpt = findLoanById(loanId);
        if (loanOpt.isEmpty()) {
            throw new KoferException("Loan not found with ID: " + loanId);
        }

        Loan loan = loanOpt.get();
        Repayment repayment = new Repayment(amount, date, note);
        
        try {
            loan.addRepayment(repayment);
            // Save the updated data store
            dataStore.saveData();
        } catch (Exception e) {
            throw new KoferException("Failed to add repayment: " + e.getMessage(), e);
        }
    }

    /**
     * Get total amount borrowed across all loans
     */
    public double getTotalBorrowed() {
        return dataStore.getLoans()
                .stream()
                .mapToDouble(Loan::getAmountBorrowed)
                .sum();
    }

    /**
     * Get total amount repaid across all loans
     */
    public double getTotalRepaid() {
        return dataStore.getLoans()
                .stream()
                .mapToDouble(Loan::getAmountRepaid)
                .sum();
    }

    /**
     * Get total remaining amount across all active loans
     */
    public double getTotalRemaining() {
        return dataStore.getLoans()
                .stream()
                .filter(loan -> !loan.isClosed())
                .mapToDouble(Loan::getRemainingAmount)
                .sum();
    }

    /**
     * Get loan summary statistics
     */
    public LoanSummary getLoanSummary() {
        List<Loan> allLoans = getAllLoans();
        List<Loan> activeLoans = getActiveLoans();
        
        return new LoanSummary(
                allLoans.size(),
                activeLoans.size(),
                getTotalBorrowed(),
                getTotalRepaid(),
                getTotalRemaining()
        );
    }

    /**
     * Close a loan manually (if fully repaid or forgiven)
     */
    public void closeLoan(String loanId) {
        Optional<Loan> loanOpt = findLoanById(loanId);
        if (loanOpt.isEmpty()) {
            throw new KoferException("Loan not found with ID: " + loanId);
        }

        Loan loan = loanOpt.get();
        try {
            loan.markClosed();
            dataStore.saveData();
        } catch (Exception e) {
            throw new KoferException("Failed to close loan: " + e.getMessage(), e);
        }
    }

    /**
     * Inner class for loan summary statistics
     */
    public static class LoanSummary {
        private final int totalLoans;
        private final int activeLoans;
        private final double totalBorrowed;
        private final double totalRepaid;
        private final double totalRemaining;

        public LoanSummary(int totalLoans, int activeLoans, double totalBorrowed, 
                          double totalRepaid, double totalRemaining) {
            this.totalLoans = totalLoans;
            this.activeLoans = activeLoans;
            this.totalBorrowed = totalBorrowed;
            this.totalRepaid = totalRepaid;
            this.totalRemaining = totalRemaining;
        }

        public int getTotalLoans() { return totalLoans; }
        public int getActiveLoans() { return activeLoans; }
        public double getTotalBorrowed() { return totalBorrowed; }
        public double getTotalRepaid() { return totalRepaid; }
        public double getTotalRemaining() { return totalRemaining; }

        @Override
        public String toString() {
            return String.format(
                "Loan Summary:%n" +
                "  Total Loans: %d%n" +
                "  Active Loans: %d%n" +
                "  Total Borrowed: $%.2f%n" +
                "  Total Repaid: $%.2f%n" +
                "  Total Remaining: $%.2f",
                totalLoans, activeLoans, totalBorrowed, totalRepaid, totalRemaining
            );
        }
    }
}
