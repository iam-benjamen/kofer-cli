# Kofer CLI

A command-line personal finance management tool built in Java. Kofer provides efficient transaction and loan tracking with local data persistence.

## Features

- **Transaction Management**: Record income/expenses with categorization and automatic type detection
- **Loan Management**: Track borrowed amounts, repayments, and outstanding balances
- **Financial Summaries**: Comprehensive reporting including net worth calculations
- **Local Storage**: Serialized data persistence with system-level security
- **CLI & Interactive Modes**: Both command-line arguments and interactive menu support

## Installation

### Prerequisites

- Java 11+
- Gradle (included via wrapper)

### Build & Run

```bash
git clone <repository-url>
cd kofer-cli
./gradlew build
./gradlew run
```

## Usage

### Command Line Interface

```bash
# Add transactions
kofer add transaction 50.00 grocery "Weekly shopping"
kofer add transaction -25.50 utilities "Electric bill"

# Manage loans
kofer add loan 1000.00 "John Doe" "Emergency loan"
kofer repay loan <loan-id> 200.00 "Partial payment"

# View data
kofer show transactions
kofer show loans
kofer summary

# Interactive mode
kofer interactive

# Help
kofer help
```

### Transaction Types

- **Positive amounts**: Income/credits
- **Negative amounts**: Expenses/debits
- **Categories**: Arbitrary strings for organization

## Architecture

```text
src/main/java/kofer/
├── cli/           # Command-line interface and error handling
├── manager/       # Business logic (TransactionsManager, LoanManager)
├── model/         # Domain models (Transaction, Loan, Repayment)
├── store/         # Data persistence (DataStore)
├── util/          # Utilities and enums
└── exception/     # Custom exception hierarchy
```

### Key Components

- **CLI Layer**: Argument parsing, command routing, user interaction
- **Manager Layer**: Business logic and data validation
- **Model Layer**: Domain entities with business rules
- **Store Layer**: Serialized data persistence to `~/.kofer/kofer.dat`
- **Exception Handling**: Structured error management with user-friendly messages

## Data Storage

- **Location**: `~/.kofer/kofer.dat`
- **Format**: Java serialization
- **Security**: System-level file permissions (no encryption)
- **Backup**: Manual file copying recommended

## Development

### Error Handling

- Custom exception hierarchy for different error types
- Debug mode: `kofer --debug <command>`
- Centralized error handling with user-friendly messages

### Testing

```bash
./gradlew test
```
