package bank;

import bank.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PrivateBankTest {

    private PrivateBank bank;

    @BeforeEach
    void init() {
        try {
            bank = new PrivateBank("Meine Bank", 0.2, 0.2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    public void testConstructors() throws NumericValueInvalidException {
        PrivateBank bank1 = new PrivateBank("Meine Bank", 0.2, 0.2);
        PrivateBank bank2 = new PrivateBank(bank1);
        assertEquals(bank1, bank2);
        bank2.setOutgoingInterest(0.3);
        assertNotEquals(bank1, bank2);

        // test toString
        String bank1String = bank1.toString();
        String bank2String = bank2.toString();
        assertNotEquals(bank1String, bank2String);
        bank2.setOutgoingInterest(0.2);
        assertEquals(bank1, bank2);
    }

    @Test
    public void createAccountTest() throws AccountAlreadyExistsException, NumericValueInvalidException, TransactionAlreadyExistException, TransactionAttributeException {
        bank.createAccount("Lisa");

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Hans"));
        transactions.add(new OutgoingTransfer("01.01.2019", "Einkauf", 200, "Hans", "Rewe"));

        bank.createAccount("Frank", transactions);
    }

    @Test
    public void addTransactionTest() throws AccountAlreadyExistsException, NumericValueInvalidException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        bank.createAccount("Lisa");
        bank.addTransaction("Lisa", new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Lisa"));
    }

    @Test
    public void removeTransactionTest() throws AccountAlreadyExistsException, NumericValueInvalidException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, TransactionDoesNotExistException {
        bank.createAccount("Lisa");
        IncomingTransfer t = new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Lisa");
        bank.addTransaction("Lisa", t);
        bank.removeTransaction("Lisa", t);
    }

    @Test
    public void containsTransactionTest() throws TransactionAlreadyExistException, NumericValueInvalidException, AccountDoesNotExistException, TransactionAttributeException, AccountAlreadyExistsException {
        bank.createAccount("Lisa");
        IncomingTransfer t = new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Lisa");
        IncomingTransfer bad = new IncomingTransfer("01.01.2022", "Miete", 200, "Herr Mustermann", "Lisa");
        bank.addTransaction("Lisa", t);
        assertTrue(bank.containsTransaction("Lisa", t));
        assertFalse(bank.containsTransaction("Lisa", bad));
    }

    @Test
    public void getAccountBalanceTest() throws TransactionAlreadyExistException, NumericValueInvalidException, AccountDoesNotExistException, TransactionAttributeException, AccountAlreadyExistsException {
        bank.createAccount("Lisa");
        IncomingTransfer t1 = new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Lisa");
        OutgoingTransfer t2 = new OutgoingTransfer("01.01.2019", "Miete", 800, "Lisa", "Frank");
        bank.addTransaction("Lisa", t1);
        bank.addTransaction("Lisa", t2);
        assertEquals(1200, bank.getAccountBalance("Lisa"));
    }

    @Test
    void getTransactionsTest() throws NumericValueInvalidException, AccountAlreadyExistsException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        IncomingTransfer t1 = new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Lisa");
        OutgoingTransfer t2 = new OutgoingTransfer("01.01.2019", "Miete", 800, "Lisa", "Frank");

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(t1);
        transactions.add(t2);
        bank.createAccount("Lisa", transactions);

        assertEquals(transactions, bank.getTransactions("Lisa"));

        OutgoingTransfer t3 = new OutgoingTransfer("12.01.2019", "Bad", 200, "Lisa", "Frank");
        transactions.add(t3);
        assertNotEquals(transactions, bank.getTransactions("Lisa"));
    }

    @Test
    void getTransactionSortedTest() throws NumericValueInvalidException, TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException {
        IncomingTransfer t1 = new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Lisa");
        OutgoingTransfer t2 = new OutgoingTransfer("01.01.2019", "Miete", 800, "Lisa", "Frank");

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(t1);
        transactions.add(t2);
        bank.createAccount("Lisa", transactions);

        assertEquals(transactions, bank.getTransactions("Lisa"));

        OutgoingTransfer t3 = new OutgoingTransfer("12.01.2019", "Bad", 200, "Lisa", "Frank");
        transactions.add(t3);
        assertNotEquals(transactions, bank.getTransactions("Lisa"));

        List<Transaction> sorted = new ArrayList<>();
        sorted.add(t2);
        sorted.add(t1);

        List<Transaction> sortedResult1 = bank.getTransactionsSorted("Lisa", true);
        List<Transaction> sortedResult2 = bank.getTransactionsSorted("Lisa", false);

        assertNotEquals(sorted, sortedResult1);
        assertNotEquals(sorted, sortedResult2);
    }

    @Test
    void getTransactionsByType() throws NumericValueInvalidException, TransactionAlreadyExistException, AccountAlreadyExistsException, TransactionAttributeException {
        IncomingTransfer t1 = new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Lisa");
        OutgoingTransfer t2 = new OutgoingTransfer("01.01.2019", "Miete", 800, "Lisa", "Frank");

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(t1);
        transactions.add(t2);
        bank.createAccount("Lisa", transactions);



        List<Transaction> positiveResult = bank.getTransactionsByType("Lisa", true);

        List<Transaction> negativeResult = bank.getTransactionsByType("Lisa", false);

        assertEquals(1, positiveResult.size());
        assertEquals(1, negativeResult.size());
        assertEquals(t1, positiveResult.get(0));
        assertEquals(t2, negativeResult.get(0));
    }

    @Test
    public void WriteReadTest() throws IOException {
        bank.setDirectoryName("test22");
        bank.readAccounts();
        UUID uuid = UUID.randomUUID();
        bank.setDirectoryName("test_Prak"+uuid.toString());

        bank.writeAccount("Hans");
        bank.writeAccount("Anna");

        assertFalse(bank.getTransactions("Hans").isEmpty());


    }


}
