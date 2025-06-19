package kofer.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a loan taken from a lender.
 * This class manages the loan details, repayments, and status.
 * It allows adding repayments, checking remaining amounts, and marking the loan as closed.
 */
public class Loan implements Serializable {
    private String id;               // Unique loan ID
    private String lenderName;       // Who you borrowed from
    private double amountBorrowed;   // Total amount borrowed
    private double amountRepaid;     // Automatically updated
    private LocalDate dateBorrowed;   // When the loan was taken
    private String description;      // Optional notes
    private List<Repayment> repayments;
    private boolean isClosed;

    public Loan(String lenderName, double amountBorrowed, LocalDate dateBorrowed, String description) {
        this.id = UUID.randomUUID().toString();
        this.lenderName = lenderName;
        this.amountBorrowed = amountBorrowed;
        this.dateBorrowed = dateBorrowed != null ? dateBorrowed : LocalDate.now();
        this.description = description;
        this.repayments = new ArrayList<>();
        this.isClosed = false;
    }

    public String getId() {
        return id;
    }
    public String getLenderName() {
        return lenderName;
    }
    public double getAmountBorrowed() {
        return amountBorrowed;
    }
    public double getAmountRepaid() {
        return amountRepaid;
    }
    public LocalDate getDateBorrowed() {
        return dateBorrowed;
    }
    public String getDescription() {
        return description;
    }
    public List<Repayment> getRepayments() {
        return repayments;
    }
    public boolean isClosed() {
        return isClosed;
    }

    public void addRepayment(Repayment repayment) {
        // Validate the repayment before adding
        if (isClosed) {
            throw new IllegalStateException("Cannot add repayment to a closed loan.");
        }
        
        // Validate repayment details
        if (repayment.getAmount() <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive.");
        }

        // Check if repayment exceeds the remaining amount
        if (getRemainingAmount() < repayment.getAmount()) {
            throw new IllegalArgumentException("Repayment exceeds remaining loan amount.");
        }

        // Validate repayment date
        if (repayment.getDate().isBefore(dateBorrowed)) {
            throw new IllegalArgumentException("Repayment date cannot be before the loan date.");
        }

        // Check if the repayment date is in the future
        if (repayment.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Repayment date cannot be in the future.");
        }

        // Add the repayment to the list and update the total repaid amount
        repayments.add(repayment);
        amountRepaid += repayment.getAmount();
        if (getRemainingAmount() <= 0) {
            markClosed();
        }
    }
 
    public double getRemainingAmount() {
        return amountBorrowed - amountRepaid;
    }
 
    public void markClosed() {
        if (isClosed) {
            throw new IllegalStateException("Loan is already closed.");
        }
        isClosed = true;
    }

    @Override
    public String toString() {
        return String.format("[Loan] %s: %.2f borrowed from %s on %s. Repaid: %.2f. Remaining: %.2f. Description: %s",
                id, amountBorrowed, lenderName, dateBorrowed.toString(), amountRepaid, getRemainingAmount(), description == null ? "" : description);
    }
}
