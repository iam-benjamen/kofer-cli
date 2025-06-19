package kofer.cli;

import kofer.manager.TransactionsManager;
import kofer.model.Transaction;
import kofer.store.DataStore;
import kofer.util.TransactionType;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * KoferCLI represents the Command-Line Interface (CLI) for the Kofer application.
 * This class allows users to interact with the application via the terminal
 * to manage financial transactions, view summaries, and save data securely.
 * The interface provides options to add transactions, view existing ones,
 * display transaction summaries, and exit the application.
 */
public class KoferCLI {
    private final Scanner scanner;
    private final DataStore dataStore;
    private final TransactionsManager transactionManager;

    public KoferCLI(DataStore dataStore) {
        this.scanner = new Scanner(System.in);
        this.dataStore = dataStore;
        this.transactionManager = new TransactionsManager(dataStore);
    }

    public void start() {
        System.out.println("\nWelcome to Kofer CLI 🪙\n");

        boolean running = true;
        while (running) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> addTransaction();
                case "2" -> showTransactions();
                case "3" -> showSummary();
                case "0" -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }

        try {
            dataStore.saveData(getPassword());
            System.out.println("\nGoodbye! Your data has been saved securely.");
        } catch (Exception e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    private void printMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Add Transaction");
        System.out.println("2. Show All Transactions");
        System.out.println("3. Show Summary");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private void showTransactions() {
        transactionManager.getAllTransaction();
    }

    private void addTransaction() {
        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter date (yyyy-mm-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter type (CREDIT, DEBIT): ");
        TransactionType type = TransactionType.valueOf(scanner.nextLine().toUpperCase());

        Transaction transaction = new Transaction(date, amount, type, category, description);
        transactionManager.addTransaction(transaction);
        System.out.println("Transaction added successfully!");
    }

    private void showSummary() {
        double credit = transactionManager.getTotalByType(TransactionType.CREDIT);
        double debit = transactionManager.getTotalByType(TransactionType.DEBIT);

        System.out.printf("Total Credit: %.2f\n", credit);
        System.out.printf("Total Debit: %.2f\n", debit);
        System.out.printf("Balance: %.2f\n", credit - debit);
    }

    private String getPassword() {
        // In production, use a password field with masking
        System.out.print("\nEnter your encryption password to save: ");
        return scanner.nextLine();
    }
}
