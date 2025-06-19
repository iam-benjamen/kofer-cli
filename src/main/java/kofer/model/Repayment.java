package kofer.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a repayment made towards a loan.
 * This class encapsulates the details of a repayment including
 * the amount, date, and an optional note.
 */
public class Repayment implements Serializable {
    private double amount; // Amount repaid
    private LocalDate date; // Date of the repayment
    private String note; // Optional note for the repayment

    public Repayment(double amount, LocalDate date, String note) {
        this.amount = amount;
        this.date = date != null ? date : LocalDate.now();
        this.note = note;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive.");
        }
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return String.format("[Repayment] %.2f on %s - %s",
                amount,
                date.toString(),
                note == null ? "" : note);
    }
}
