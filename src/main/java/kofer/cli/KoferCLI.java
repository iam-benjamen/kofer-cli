package kofer.cli;

import kofer.exception.KoferException;
import kofer.exception.ValidationException;
import kofer.exception.DataAccessException;
import kofer.manager.LoanManager;
import kofer.manager.TransactionsManager;
import kofer.model.Loan;
import kofer.model.Repayment;
import kofer.model.Transaction;
import kofer.store.DataStore;
import kofer.util.TransactionType;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * KoferCLI represents the Command-Line Interface (CLI) for the Kofer application.
 * This class allows users to interact with the application via command-line arguments
 * to manage financial transactions, loans, and view summaries.
 */
public class KoferCLI {

    private final TransactionsManager transactionsManager;
    private final LoanManager loanManager;
    private final Scanner scanner;
    private final CLIErrorHandler errorHandler;

    public KoferCLI() throws KoferException {
        this(false);
    }

    public KoferCLI(boolean debugMode) throws KoferException {
        this.scanner = new Scanner(System.in);
        this.errorHandler = new CLIErrorHandler(debugMode);
        DataStore dataStore = new DataStore();
        this.transactionsManager = new TransactionsManager(dataStore);
        this.loanManager = new LoanManager(dataStore);
    }

    /**
     * Process command line arguments and execute the appropriate command
     */
    public void processCommand(String[] args) {
        if (args.length == 0) {
            showHelp();
            return;
        }

        String command = args[0].toLowerCase();

        try {
            switch (command) {
                case "help", "--help", "-h" -> showHelp();
                case "add" -> handleAddCommand(args);
                case "show", "list" -> handleShowCommand(args);
                case "repay" -> handleRepayCommand(args);
                case "summary" -> showSummary();
                case "interactive" -> startInteractiveMode();
                default -> {
                    System.err.println("Unknown command: " + command);
                    System.err.println("Use 'kofer help' to see available commands.");
                    System.exit(1);
                }
            }
        } catch (ValidationException e) {
            System.err.println("Input validation failed: " + e.getMessage());
            if (e.isRetryable()) {
                System.err.println("Please correct your input and try again.");
            }
            System.exit(1);
        } catch (DataAccessException e) {
            errorHandler.handleDataError(command, e);
            System.exit(1);
        } catch (KoferException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            errorHandler.handleError("command processing", e);
            System.exit(1);
        }
    }

    /**
     * Display help documentation
     */
    public void showHelp() {
        System.out.println("Kofer CLI - Personal Finance Management Tool");
        System.out.println();
        System.out.println("USAGE:");
        System.out.println("    kofer <COMMAND> [OPTIONS]");
        System.out.println();
        System.out.println("COMMANDS:");
        System.out.println("    help                           Show this help message");
        System.out.println("    add transaction <amount> <category> [description]");
        System.out.println("                                   Add a new transaction");
        System.out.println("                                   Examples:");
        System.out.println("                                     kofer add transaction 50.00 grocery \"Weekly shopping\"");
        System.out.println("                                     kofer add transaction -25.50 utilities \"Electric bill\"");
        System.out.println();
        System.out.println("    add loan <amount> <lender> [description]");
        System.out.println("                                   Record a new loan");
        System.out.println("                                   Example:");
        System.out.println("                                     kofer add loan 1000.00 \"John Doe\" \"Emergency loan\"");
        System.out.println();
        System.out.println("    repay loan <loan-id> <amount> [description]");
        System.out.println("                                   Make a loan repayment");
        System.out.println("                                   Example:");
        System.out.println("                                     kofer repay loan abc123 200.00 \"Partial payment\"");
        System.out.println();
        System.out.println("    show transactions              List all transactions");
        System.out.println("    show loans                     List all loans");
        System.out.println("    summary                        Show financial summary");
        System.out.println("    interactive                    Start interactive mode");
        System.out.println();
        System.out.println("NOTES:");
        System.out.println("    - Positive amounts are credits (income)");
        System.out.println("    - Negative amounts are debits (expenses)");
        System.out.println("    - Use quotes for multi-word descriptions");
        System.out.println("    - Dates default to today if not specified");
    }

    /**
     * Handle 'add' command
     */
    private void handleAddCommand(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: kofer add <transaction|loan> [options]");
            return;
        }

        String subCommand = args[1].toLowerCase();
        switch (subCommand) {
            case "transaction" -> addTransactionFromArgs(args);
            case "loan" -> addLoanFromArgs(args);
            default -> {
                System.err.println("Unknown add command: " + subCommand);
                System.err.println("Available: transaction, loan");
            }
        }
    }

    /**
     * Handle 'show' command
     */
    private void handleShowCommand(String[] args) {
        if (args.length < 2) {
            // Default to showing transactions
            showTransactions();
            return;
        }

        String subCommand = args[1].toLowerCase();
        switch (subCommand) {
            case "transactions", "transaction" -> showTransactions();
            case "loans", "loan" -> showLoans();
            default -> {
                System.err.println("Unknown show command: " + subCommand);
                System.err.println("Available: transactions, loans");
            }
        }
    }

    /**
     * Handle 'repay' command
     */
    private void handleRepayCommand(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: kofer repay loan <loan-id> <amount> [description]");
            return;
        }

        String subCommand = args[1].toLowerCase();
        if ("loan".equals(subCommand)) {
            repayLoanFromArgs(args);
        } else {
            System.err.println("Unknown repay command: " + subCommand);
            System.err.println("Available: loan");
        }
    }

    /**
     * Add transaction from command line arguments
     * Usage: kofer add transaction <amount> <category> [description]
     */
    private void addTransactionFromArgs(String[] args) {
        if (args.length < 4) {
            errorHandler.handleMissingArguments("add transaction",
                "kofer add transaction <amount> <category> [description]");
            throw new ValidationException("Insufficient arguments for add transaction command");
        }

        // Validate and parse amount
        Double amount = errorHandler.parseAmount(args[2], "transaction amount");
        if (amount == null) {
            throw new ValidationException("amount", args[2], "decimal number (use negative for expenses)");
        }

        // Validate category
        String category = errorHandler.parseString(args[3], "category", true);
        if (category == null) {
            throw new ValidationException("category", args[3], "non-empty string");
        }

        // Parse optional description
        String description = "";
        if (args.length > 4) {
            description = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            description = description.replaceAll("^\"|\"$", ""); // Remove quotes
        }

        try {
            // Determine transaction type based on amount sign
            TransactionType type = amount >= 0 ? TransactionType.CREDIT : TransactionType.DEBIT;
            double absAmount = Math.abs(amount); // Store as positive value

            LocalDate date = LocalDate.now();
            Transaction transaction = new Transaction(date, absAmount, type, category, description);

            transactionsManager.addTransaction(transaction);

            System.out.println("✓ Transaction added successfully!");
            System.out.printf("  %s: $%.2f in category '%s'%s%n",
                type.name().toLowerCase(), absAmount, category,
                description.isEmpty() ? "" : " - " + description);

        } catch (KoferException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("add transaction", e.getMessage(), e);
        }
    }

    /**
     * Add loan from command line arguments
     * Usage: kofer add loan <amount> <lender> [description]
     */
    private void addLoanFromArgs(String[] args) {
        if (args.length < 4) {
            errorHandler.handleMissingArguments("add loan",
                "kofer add loan <amount> <lender> [description]");
            throw new ValidationException("Insufficient arguments for add loan command");
        }

        // Validate and parse amount
        Double amount = errorHandler.parseAmount(args[2], "loan amount");
        if (amount == null || amount <= 0) {
            throw new ValidationException("amount", args[2], "positive number");
        }

        // Validate lender
        String lender = errorHandler.parseString(args[3].replaceAll("^\"|\"$", ""), "lender", true);
        if (lender == null) {
            throw new ValidationException("lender", args[3], "non-empty string");
        }

        // Parse optional description
        String description = "";
        if (args.length > 4) {
            description = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            description = description.replaceAll("^\"|\"$", ""); // Remove quotes
        }

        try {
            LocalDate date = LocalDate.now();
            Loan loan = loanManager.createLoan(lender, amount, date, description);

            System.out.println("✓ Loan recorded successfully!");
            System.out.printf("  Loan ID: %s%n", loan.getId());
            System.out.printf("  Amount: $%.2f from %s%s%n", amount, lender,
                description.isEmpty() ? "" : " - " + description);

        } catch (KoferException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("add loan", e.getMessage(), e);
        }
    }

    /**
     * Repay loan from command line arguments
     * Usage: kofer repay loan <loan-id> <amount> [description]
     */
    private void repayLoanFromArgs(String[] args) {
        if (args.length < 4) {
            errorHandler.handleMissingArguments("repay loan",
                "kofer repay loan <loan-id> <amount> [description]");
            throw new ValidationException("Insufficient arguments for repay loan command");
        }

        // Validate loan ID
        String loanId = errorHandler.parseString(args[2], "loan ID", true);
        if (loanId == null) {
            throw new ValidationException("loan-id", args[2], "non-empty string");
        }

        // Validate and parse amount
        Double amount = errorHandler.parseAmount(args[3], "repayment amount");
        if (amount == null || amount <= 0) {
            throw new ValidationException("amount", args[3], "positive number");
        }

        // Parse optional description
        String description = "";
        if (args.length > 4) {
            description = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
            description = description.replaceAll("^\"|\"$", ""); // Remove quotes
        }

        try {
            LocalDate date = LocalDate.now();
            loanManager.addRepayment(loanId, amount, date, description);

            System.out.println("✓ Loan repayment recorded successfully!");
            System.out.printf("  Repaid $%.2f towards loan %s%s%n", amount, loanId,
                description.isEmpty() ? "" : " - " + description);

        } catch (KoferException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("repay loan", e.getMessage(), e);
        }
    }

    /**
     * Show all transactions
     */
    private void showTransactions() {
        List<Transaction> transactions = transactionsManager.getAllTransaction();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("All Transactions:");
        System.out.println("=================");
        transactions.forEach(System.out::println);
    }

    /**
     * Show all loans
     */
    private void showLoans() {
        List<Loan> loans = loanManager.getAllLoans();
        if (loans.isEmpty()) {
            System.out.println("No loans found.");
            return;
        }

        System.out.println("All Loans:");
        System.out.println("==========");
        loans.forEach(System.out::println);

        // Show summary
        LoanManager.LoanSummary summary = loanManager.getLoanSummary();
        System.out.println("\n" + summary);
    }

    /**
     * Show financial summary
     */
    private void showSummary() {
        double credit = transactionsManager.getTotalByType(TransactionType.CREDIT);
        double debit = transactionsManager.getTotalByType(TransactionType.DEBIT);

        System.out.println("Financial Summary:");
        System.out.println("==================");
        System.out.printf("Total Income (Credit): $%.2f%n", credit);
        System.out.printf("Total Expenses (Debit): $%.2f%n", debit);
        System.out.printf("Net Balance: $%.2f%n", credit - debit);

        // Add loan summary
        LoanManager.LoanSummary loanSummary = loanManager.getLoanSummary();
        if (loanSummary.getTotalLoans() > 0) {
            System.out.println("\n" + loanSummary);

            // Calculate net worth including loans
            double netWorthWithLoans = (credit - debit) - loanSummary.getTotalRemaining();
            System.out.printf("%nNet Worth (including outstanding loans): $%.2f%n", netWorthWithLoans);
        } else {
            System.out.println("\nNo loans recorded.");
        }
    }

    /**
     * Start interactive mode (original menu-driven interface)
     */
    public void startInteractiveMode() {
        System.out.println("\nWelcome to Kofer CLI - Interactive Mode\n");

        boolean running = true;
        while (running) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> addTransactionInteractive();
                case "2" -> showTransactions();
                case "3" -> showSummary();
                case "0" -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
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

    private void addTransactionInteractive() {
        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter category, e.g. groceries, utilities: ");
        String category = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount;

        while(true) {
            try {
                amount = Double.parseDouble(scanner.nextLine());
                break;
            } catch (NumberFormatException e){
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }

        System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
        LocalDate date;
        String dateInput = scanner.nextLine().trim();

        if (dateInput.isEmpty()) {
            date = LocalDate.now();
        } else {
            while(true) {
                try {
                    date = LocalDate.parse(dateInput);
                    break;
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date. Please enter a valid date in the format YYYY-MM-DD.");
                    System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
                    dateInput = scanner.nextLine().trim();
                    if (dateInput.isEmpty()) {
                        date = LocalDate.now();
                        break;
                    }
                }
            }
        }

        System.out.print("Enter type (CREDIT, DEBIT): ");
        TransactionType type;
        while(true) {
            try {
                type = TransactionType.valueOf(scanner.nextLine().toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid type. Please enter a valid type (CREDIT, DEBIT).");
            }
        }

        Transaction transaction = new Transaction(date, amount, type, category, description);
        transactionsManager.addTransaction(transaction);
        System.out.println("Transaction added successfully!");
    }
}
