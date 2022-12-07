package bank;

import bank.exceptions.*;

import java.util.*;

public class PrivateBank implements Bank {

    private HashMap<String, List<Transaction>> accountsToTransactions = new HashMap<>();
    /**
     * The name of the bank
     * The transactions of the bank mapped to their accounts
     * The incomingInterest is the interest on deposits
     * The outgoingInterest is the interest on withdrawals
     */
    private String name;
    private double incomingInterest;
    private double outgoingInterest;


    /**
     * Constructor for the PrivateBank class
     *
     * @param name             The name of the bank
     * @param incomingInterest The interest on deposits
     * @param outgoingInterest The interest on withdrawals
     * @throws NumericValueInvalidException If the interest is not between 0 and 1
     */
    public PrivateBank(String name, double incomingInterest, double outgoingInterest) throws NumericValueInvalidException {
        this.name = name;
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    /**
     * @param bank a bank Object to copy from
     * @throws NumericValueInvalidException if the interest is not between 0 and 1
     */
    public PrivateBank(PrivateBank bank) throws NumericValueInvalidException {
        this(bank.name, bank.incomingInterest, bank.outgoingInterest);
        this.accountsToTransactions = new HashMap<String, List<Transaction>>(bank.accountsToTransactions);
    }


    /**
     * Adds an account to the bank.
     *
     * @param account the account to be added
     * @throws AccountAlreadyExistsException if the account already exists
     */
    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException {
        if (accountsToTransactions.putIfAbsent(account, null) != null) {
            throw new AccountAlreadyExistsException();
        }
    }

    /**
     * Adds an account (with specified transactions) to the bank.
     * Important: duplicate transactions must not be added to the account!
     *
     * @param account      the account to be added
     * @param transactions a list of already existing transactions which should be added to the newly created account
     * @throws AccountAlreadyExistsException    if the account already exists
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void createAccount(String account, List<Transaction> transactions) throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, NumericValueInvalidException {
        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException();
        }else{
            accountsToTransactions.put(account, new ArrayList<>());
        }
        try {
            for (Transaction transaction : transactions) {
                addTransaction(account, transaction);
            }
        } catch (TransactionAlreadyExistException | TransactionAttributeException | NumericValueInvalidException e) {
            accountsToTransactions.remove(account);
            throw e;
        } catch (AccountDoesNotExistException e) {
            // This should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a transaction to an already existing account.
     *
     * @param account     the account to which the transaction is added
     * @param transaction the transaction which should be added to the specified account
     * @throws TransactionAlreadyExistException if the transaction already exists
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionAttributeException    if the validation check for certain attributes fail
     */
    @Override
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, NumericValueInvalidException {
        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException();
        }
        if (accountsToTransactions.get(account).contains(transaction)) {
            throw new TransactionAlreadyExistException();
        }

        if (transaction.getAmount() == 0) {
            throw new TransactionAttributeException();
        }

        if (transaction instanceof Payment payment) {
            ((Payment) transaction).setIncomingInterest(this.incomingInterest);
            ((Payment) transaction).setOutgoingInterest(this.outgoingInterest);
            accountsToTransactions.get(account).add(transaction);
        } else if (transaction instanceof Transfer transfer) {
            accountsToTransactions.get(account).add(transaction);
        }
    }

    /**
     * Removes a transaction from an account. If the transaction does not exist, an exception is
     * thrown.
     *
     * @param account     the account from which the transaction is removed
     * @param transaction the transaction which is removed from the specified account
     * @throws AccountDoesNotExistException     if the specified account does not exist
     * @throws TransactionDoesNotExistException if the transaction cannot be found
     */
    @Override
    public void removeTransaction(String account, Transaction transaction) throws AccountDoesNotExistException, TransactionDoesNotExistException {
        if (accountsToTransactions.get(account) == null) {
            throw new AccountDoesNotExistException();
        }
        if (!accountsToTransactions.get(account).contains(transaction)) {
            throw new TransactionDoesNotExistException();
        }
        accountsToTransactions.get(account).remove(transaction);
    }

    /**
     * Checks whether the specified transaction for a given account exists.
     *
     * @param account     the account from which the transaction is checked
     * @param transaction the transaction to search/look for
     */
    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        return accountsToTransactions.get(account).contains(transaction);
    }

    /**
     * Calculates and returns the current account balance.
     *
     * @param account the selected account
     * @return the current account balance
     */
    @Override
    public double getAccountBalance(String account) {
        double balance = 0;
        for (Transaction transaction : accountsToTransactions.get(account)) {
                balance += transaction.calculate();
        }
        return balance;
    }

    /**
     * Returns a list of transactions for an account.
     *
     * @param account the selected account
     * @return the list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactions(String account) {
        return accountsToTransactions.get(account);
    }

    /**
     * Returns a sorted list (-> calculated amounts) of transactions for a specific account. Sorts the list either in ascending or descending order
     * (or empty).
     *
     * @param account the selected account
     * @param asc     selects if the transaction list is sorted in ascending or descending order
     * @return the sorted list of all transactions for the specified account
     */
    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        List<Transaction> transactions = getTransactions(account);
        transactions.sort(Comparator.comparing(Transaction::calculate));

        if (!asc) {
            Collections.reverse(transactions);
        }
        return transactions;
    }

    /**
     * Returns a list of either positive or negative transactions (-> calculated amounts).
     *
     * @param account  the selected account
     * @param positive selects if positive or negative transactions are listed
     * @return the list of all transactions by type
     */
    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> transactions = getTransactions(account);
        List<Transaction> transactionsByType = new ArrayList<>();

        for (Transaction transaction : transactions) {
            // all transactions are positive
            if (transaction instanceof Transfer && positive) {
                transactionsByType.add(transaction);
            }

            if (positive && transaction.calculate() > 0) {
                transactionsByType.add(transaction);
            } else if (!positive && transaction.calculate() < 0) {
                transactionsByType.add(transaction);
            }
        }
        return transactionsByType;
    }

    @Override
    public String toString() {
        String newLine = System.getProperty("line.separator");
        return ("Name: " + name + newLine + "Incoming Interest: " + incomingInterest + newLine + "Outgoing Interest: " + outgoingInterest +
                newLine + "Accounts: " + newLine + mapToString(accountsToTransactions));
    }

    public String mapToString(HashMap<String, List<Transaction>> map){
        String newLine = System.getProperty("line.separator");
        StringBuilder mapAsString = new StringBuilder("{");
        for (String key : map.keySet()) {
            mapAsString.append(key).append("=").append(newLine).append(map.get(key));
        }
        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
        return mapAsString.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PrivateBank)) {
            return false;
        }
        PrivateBank bank = (PrivateBank) obj;
        return (name.equals(bank.name) && incomingInterest == bank.incomingInterest && outgoingInterest == bank.outgoingInterest &&
                accountsToTransactions.equals(bank.accountsToTransactions));
    }

//region Getters and Setters

    /**
     * Get outgoing interest on withdrawals
     *
     * @return the outgoing interest
     */
    public double getOutgoingInterest() {
        return outgoingInterest;
    }

    /**
     * Set outgoing interest on withdrawals
     *
     * @param outgoingInterest the outgoing interest
     */
    public void setOutgoingInterest(double outgoingInterest) throws NumericValueInvalidException {
        if (outgoingInterest < 0 || outgoingInterest > 1) {
            throw new NumericValueInvalidException("Error: Negative Input for outgoing interest: Withdrawal)!");
        }
        this.outgoingInterest = outgoingInterest;
    }

    /**
     * Get incoming interest on deposits
     *
     * @return the incoming interest
     */
    public double getIncomingInterest() {
        return incomingInterest;
    }

    /**
     * Set incoming interest on deposits
     *
     * @param incomingInterest the incoming interest
     */
    public void setIncomingInterest(double incomingInterest) throws NumericValueInvalidException {
        if (incomingInterest < 0 || incomingInterest > 1) {
            throw new NumericValueInvalidException("Error: Negative Input for incoming interest: Deposit)!");
        } else {
            this.incomingInterest = incomingInterest;
        }
    }
//endregion

}

