package bank;

import bank.exceptions.*;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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
    private String directoryName;

    public PrivateBank(String name, double incomingInterest, double outgoingInterest) throws NumericValueInvalidException {
        this.name = name;
        setIncomingInterest(incomingInterest);
        setOutgoingInterest(outgoingInterest);
    }

    public PrivateBank(PrivateBank bank) throws NumericValueInvalidException {
        this(bank.name, bank.incomingInterest, bank.outgoingInterest);
        this.accountsToTransactions = new HashMap<String, List<Transaction>>(bank.accountsToTransactions);
    }


    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException {
        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException();
        }else {
            accountsToTransactions.put(account, new ArrayList<Transaction>());
        }
    }


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

    @Override
    public void addTransaction(String account, Transaction transaction) throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, NumericValueInvalidException {
        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException();
        }
        if (accountsToTransactions.get(account) != null && accountsToTransactions.get(account).contains(transaction)) {
            throw new TransactionAlreadyExistException();
        }

        if (transaction.getAmount() == 0) {
            throw new TransactionAttributeException();
        }

        if (transaction instanceof Payment payment) {
            //((Payment) transaction).setIncomingInterest(this.incomingInterest);
            //((Payment) transaction).setOutgoingInterest(this.outgoingInterest);
            ((Payment) transaction).setIncomingInterest(payment.getIncomingInterest());
            ((Payment) transaction).setOutgoingInterest(payment.getOutgoingInterest());
            accountsToTransactions.get(account).add(transaction);
        } else if (transaction instanceof Transfer transfer) {
            accountsToTransactions.get(account);
            if(accountsToTransactions.get(account) == null){
                ArrayList<Transaction> transactions = new ArrayList<>();
                transactions.add(transaction);
                accountsToTransactions.put(account, new ArrayList<>(transactions));
            }else{
                accountsToTransactions.get(account).add(transaction);
            }
        }
    }

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


    @Override
    public boolean containsTransaction(String account, Transaction transaction) {
        return accountsToTransactions.get(account).contains(transaction);
    }


    @Override
    public double getAccountBalance(String account) throws AccountDoesNotExistException {
        double balance = 0;
        if(!accountsToTransactions.containsKey(account)){
            throw new AccountDoesNotExistException();
        }
        for (Transaction transaction : accountsToTransactions.get(account)) {
                balance += transaction.calculate();
        }
        return balance;
    }


    @Override
    public List<Transaction> getTransactions(String account) {
        return accountsToTransactions.get(account);
    }


    @Override
    public List<Transaction> getTransactionsSorted(String account, boolean asc) {
        List<Transaction> transactions = new ArrayList<>(getTransactions(account));

        if (asc) {
            transactions.sort(Comparator.comparing(Transaction::calculate));
        } else {
            transactions.sort(Comparator.comparing(Transaction::calculate).reversed());
        }

        return transactions;
    }


    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> transactions = getTransactions(account);
        List<Transaction> transactionsByType = new ArrayList<>();

        for (Transaction transaction : transactions) {

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
        if(map.size() > 0){
            mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
        }else{
            mapAsString.append("}");
        }
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




    @Override
    public void writeAccount(String account) throws IOException {
        JsonSerializerImpl serializer = new JsonSerializerImpl();
        List<String> list = new ArrayList<>();
        for (Transaction transaction : accountsToTransactions.get(account)) {
            list.add(serializer.serialize(transaction));
        }

        File file = new File("persist/"+directoryName+"/Konto_"+account+".json");
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.append(list.toString());
        writer.close();
    }

    @Override
    public void readAccounts() {
        final File folder = new File("persist/"+directoryName+"/");
        for (final File fileEntry : folder.listFiles()) {
            String accountOwner = fileEntry.getName();
            accountOwner = accountOwner.substring(accountOwner.indexOf("_") + 1);
            accountOwner = accountOwner.substring(0, accountOwner.indexOf("."));

            try (FileReader fr = new FileReader(fileEntry)) {
                char[] chars = new char[(int) fileEntry.length()];
                fr.read(chars);

                String fileContent = new String(chars);

                List<LinkedTreeMap> transactionsJson = new Gson().fromJson(fileContent, ArrayList.class);
                List<Transaction> transactions = new ArrayList<>();


                for (LinkedTreeMap transaction : transactionsJson) {
                    String CLASSNAME = transaction.get("CLASSNAME").toString();
                    Object INSTANCE = transaction.get("INSTANCE");

                    JsonDeserializerImpl deserializer = new JsonDeserializerImpl();
                    Gson gson = new Gson();
                    Transaction t = (Transaction) deserializer.deserialize(gson.toJson(transaction));
                    transactions.add(t );
                }

                createAccount(accountOwner, transactions);
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (TransactionAlreadyExistException e) {
                throw new RuntimeException(e);
            } catch (NumericValueInvalidException e) {
                throw new RuntimeException(e);
            } catch (AccountAlreadyExistsException e) {
                throw new RuntimeException(e);
            } catch (TransactionAttributeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deleteAccount(String account) throws AccountDoesNotExistException, IOException {
        //accountsToTransactions
        if(!accountsToTransactions.containsKey(account)){
            throw new AccountDoesNotExistException();
        }
        accountsToTransactions.remove(account);
        File file = new File("persist/"+directoryName+"/Konto_"+account+".json");

        try {
            Files.delete(file.toPath());
        }catch (IOException e) {
          throw e;
        }
    }

    @Override
    public List<String> getAllAccounts() {
        return new ArrayList<>(accountsToTransactions.keySet());
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

    /**
     * Set the directory name to save/persist files to
     *
     * @return void
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * Get the directory name to save/persist files to
     *
     * @param directoryName the directory name
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    //endregion

}

