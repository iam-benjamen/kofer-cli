# Kofer - Personal Finance CLI Manager

Kofer is a secure command-line interface (CLI) tool designed for managing personal finances on Linux systems. It provides a simple yet powerful way to track expenses, income, and loans while keeping your financial data encrypted locally.

Built with privacy and simplicity in mind. It allows you to manage your finances directly from your terminal. All data is stored locally and encrypted using AES encryption, ensuring your financial information remains private and secure.

## Features

- **Transaction Management**
    - Add income and expense transactions
    - Categorize transactions (e.g., groceries, utilities)
    - View transaction history
    - Get financial summaries

- **Loan Management**
    - Record loans with lender details
    - Track borrowed amounts
    - Monitor repayment progress
    - View remaining balances

- **Secure Data Storage**
    - AES encryption for all stored data
    - Password-protected access
    - Local storage with no external dependencies

## Getting Started

### Prerequisites

- Java 20 or higher
- Linux-based operating system
- Maven (for building from source)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/iam-benjamen/kofer.git
cd kofer
```
2. Build the project:
``` bash
mvn clean package
```
3. Run the application:
``` bash
java -jar target/kofer.jar
```

### First Run
1. When you first run Kofer, you'll be prompted to create a password
2. This password will be used to encrypt your data
3. Make sure to remember this password as it cannot be recovered

## Project Structure
``` 
kofer/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── cli/       # CLI interface
│   │   │   ├── manager/   # Business logic
│   │   │   ├── model/     # Data models
│   │   │   ├── store/     # Data persistence
│   │   │   └── util/      # Utilities
│   │   └── resources/
│   └── test/              # Test cases
└── pom.xml
```
## Future Plans
- Export data to CSV format
- Monthly and yearly reports
- Budget planning features
- Loan management system
- Transaction categorization improvements
