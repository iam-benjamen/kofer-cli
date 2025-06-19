package kofer.model;

import kofer.util.TransactionType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a financial transaction.
 * This class encapsulates the details of a transaction, including
 * the date, amount, type, category, and description.
 */
public class Transaction implements Serializable {
    private String id; // Unique identifier for the transaction
    private LocalDate date; // Date of the transaction
    private Double amount; // Amount of the transaction
    private TransactionType type; // Type of the transaction
    private String category;    // Category of the transaction (e.g., groceries, utilities)
    private String description; // Description of the transaction

    public Transaction(LocalDate date, Double amount, TransactionType type, String category, String description) {
        this.id = UUID.randomUUID().toString();
        this.date = date != null ? date : LocalDate.now();
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f (%s) - %s",
                date.toString(), type.toUpperCase(), amount, category, description == null ? "" : description);
    }
}