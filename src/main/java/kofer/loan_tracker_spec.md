**KOFER**

**Budget Tracker CLI Tool with Loan Management - Specification Document**

---

### 📄 USER REQUIREMENTS

#### Functional Requirements

**General Features**:

- Add and view income/expense transactions
- Categorize transactions
- View monthly and yearly summaries
- Persist data locally and securely
- Access data anytime via terminal

**Loan Management Module**:

- Add a loan:**Management**
  - Lender name
  - Amount borrowed
  - Date borrowed
  - Optional description
- Track repayments:
  - Amount, date, and optional note
  - Automatically update repaid and remaining amount
- View loan details:
  - Full repayment history, current balance, and metadata
- List all active/closed loans
- Automatically mark loan as closed when fully paid
- Integrate repayments into general transaction history and summaries

#### Non-Functional Requirements

- **Security**: Data must be encrypted using AES or a similar method
- **Portability**: Should run on any Linux system via terminal
- **Persistence**: Data must be stored locally and safely
- **Modularity**: Each feature (loans, transactions, encryption) should be modular for easy testing and maintenance
- **Usability**: Intuitive CLI commands and help messages

---

### 🛠️ SYSTEM ARCHITECTURE OVERVIEW

#### 1. TransactionManager

- Handles all generic transactions
- Methods:
  - `addTransaction(type, amount, category, description?, date?)`
  - `getTransactions(filter?)`
  - `summarize(month?, year?)`
  - `exportToCSV()`
- Repayments from loans are added here with `type = loan_repayment`

#### 2. LoanManager

- Tracks loans and repayments
- Methods:
  - `addLoan(lender, amount, description?)`
  - `repayLoan(lender, amount, note?)`
  - `viewLoan(lender)`
  - `listLoans()`
  - `closeLoan(lender)`
- Also adds a loan repayment transaction via `TransactionManager`

#### 3. DataStore

- Reads/writes to local encrypted JSON file
- Loads data into memory on app start
- Uses passphrase for encryption/decryption

#### 4. CLI

- Handles user interaction
- Routes commands to the appropriate modules
- Provides usage hints and help

#### 5. EncryptionService

- Uses AES encryption
- Derives key from user-supplied passphrase
- Encrypts/decrypts data before storage or after loading

#### 6. Models

- `Transaction`
- `Loan`
- `Repayment`

---

### 🧰 SAMPLE COMMANDS

```bash
loan add michael 50000 "Borrowed for house rent"
loan repay michael 10000 "Paid part of it"
loan view michael
loan list
loan close michael
```

All loan repayments will also show up in transaction summaries categorized by lender name.

---

### ⏳ PROJECT TIMELINE – KOFER-CLI

#### Week 1: Project Setup & Scaffolding

- Initialize Java project in IntelliJ (Maven or Gradle)
- Set up package structure: `cli`, `manager`, `model`, `store`, `util`
- Implement simple CLI parsing scaffold with placeholders

#### Week 2: Build the Models & DataStore

- Create models: `Transaction`, `Loan`, `Repayment`
- Design and implement `DataStore` to read/write encrypted JSON files
- Use AES for encryption with user passphrase
- Load and save all data on startup and shutdown

#### Week 3: Transaction Management

- Implement `TransactionManager`
- Support adding, retrieving, filtering transactions
- Implement summary views (monthly/yearly)
- Integrate CLI commands for transactions

#### Week 4: Loan Module

- Build `LoanManager` with add, repay, view, list, close
- Ensure loan repayments feed into `TransactionManager`
- Integrate CLI commands for loans
- Add auto-close logic when fully paid

#### Week 5: CLI Refinement & Usability

- Improve CLI experience with usage help, error messages
- Add interactive command prompts (optional)
- Add command history and tab-complete (bonus)

#### Week 6: Polish, Docs & Testing

- Write unit tests for all core components
- Polish command output formatting
- Manual testing and bug fixing
- Prepare full GitHub documentation:
  - README with usage
  - Setup instructions
  - Sample data/demo

#### Bonus (Any Time):

- Export to CSV
- Optional encryption algorithm toggle
- Add categories and filtering logic for summaries

---
#### Commands to add
- get total debts amount(unpaid) 

Ready to start with the project setup?

