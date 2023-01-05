import bank.IncomingTransfer;
import bank.OutgoingTransfer;
import bank.PrivateBank;
import bank.Transaction;
import bank.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PrivateBankTest {

    private PrivateBank bank;
    private List<Transaction> transactionsLisa;
    @BeforeEach
    void init() {
        try {
            bank = new PrivateBank("Meine Bank", 0.2, 0.2);
            transactionsLisa = new ArrayList<>();
            transactionsLisa.add(new OutgoingTransfer("01.01.2019", "Miete", 800, "Lisa", "Frank"));
            transactionsLisa.add(new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Lisa"));
            bank.createAccount("Lisa",transactionsLisa);
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

        assertThrows(NumericValueInvalidException.class, () -> new PrivateBank("Meine Bank", 0.3, -0.1));
    }

    @Test
    public void toStringTest() throws NumericValueInvalidException {
        PrivateBank bankCopy = new PrivateBank(bank);

        String bankString = bank.toString();
        String bankCopyString = bankCopy.toString();
        assertEquals(bankString, bankCopyString);

        bankCopy.setOutgoingInterest(0.12);
        bankCopyString = bankCopy.toString();
        assertNotEquals(bankString, bankCopyString);
    }

    @Test
    public void createAccountTest() throws AccountAlreadyExistsException, NumericValueInvalidException, TransactionAlreadyExistException, TransactionAttributeException {
        bank.createAccount("Frank");

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Bernd"));
        transactions.add(new OutgoingTransfer("01.01.2019", "Einkauf", 200, "Bernd", "Rewe"));
        bank.createAccount("Bernd", transactions);

        assertTrue(bank.containsTransaction("Bernd", transactions.get(0)));
        assertTrue(bank.containsTransaction("Bernd", transactions.get(1)));
        assertFalse(bank.containsTransaction("Frank", transactions.get(1)));

        assertThrows(AccountAlreadyExistsException.class, () -> bank.createAccount("Lisa"));
    }

    @Test
    public void addTransactionTest() throws  NumericValueInvalidException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        IncomingTransfer transfer = new IncomingTransfer("02.02.2019", "TestTransaction", 2000, "Herr Peters", "Lisa");
        bank.addTransaction("Lisa", transfer);

        assertTrue(bank.containsTransaction("Lisa",transfer));
        assertEquals(3, bank.getTransactions("Lisa").size());

        assertThrows(
                TransactionAlreadyExistException.class,
                () -> bank.addTransaction("Lisa", transfer)
        );
        assertThrows(
                AccountDoesNotExistException.class,
                () -> bank.addTransaction("Hans", transfer)
        );
        assertThrows(
                NumericValueInvalidException.class,
                () -> bank.addTransaction("Lisa", new OutgoingTransfer("02.02.2019", "TestTransaction", -2000, "Lisa", "Hans"))
        );
        assertThrows(
                TransactionAttributeException.class,
                () -> bank.addTransaction("Lisa", new OutgoingTransfer("02.02.2019", "TestTransaction", 0, "Lisa", "Hans"))

        );

    }

    @Test
    public void removeTransactionTest() throws AccountDoesNotExistException, TransactionDoesNotExistException {
        int size = bank.getTransactions("Lisa").size();
        bank.removeTransaction("Lisa", transactionsLisa.get(0));

        assertEquals(size - 1, bank.getTransactions("Lisa").size());

        assertThrows(
                AccountDoesNotExistException.class,
                () -> bank.removeTransaction("Hans", transactionsLisa.get(0))
        );
        assertThrows(
                TransactionDoesNotExistException.class,
                () -> bank.removeTransaction("Lisa", new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Hans"))
        );
    }

    @Test
    public void containsTransactionTest() throws  NumericValueInvalidException {
        assertTrue(bank.containsTransaction("Lisa", transactionsLisa.get(0)));
        assertFalse(bank.containsTransaction("Lisa", new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Hans")));
    }

    @Test
    public void getAccountBalanceTest() throws AccountDoesNotExistException {
        assertEquals(1200, bank.getAccountBalance("Lisa"));
        assertThrows(
                AccountDoesNotExistException.class,
                () -> bank.getAccountBalance("NotExistingAccount")
        );
    }

    @Test
    void getTransactionsTest() throws NumericValueInvalidException, TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException {
        assertEquals(transactionsLisa, bank.getTransactions("Lisa"));

        bank.addTransaction("Lisa", new OutgoingTransfer("01.01.2019", "Einkauf", 70, "Lisa", "Rewe"));
        assertNotEquals(transactionsLisa, bank.getTransactions("Lisa"));
    }

    @Test
    void getTransactionSortedTest() {
        assertEquals(transactionsLisa, bank.getTransactions("Lisa"));

        List<Transaction> sorted = new ArrayList<Transaction>(transactionsLisa);
        sorted.sort(Comparator.comparing(t -> String.valueOf(t.calculate())));

        List<Transaction> sortedResultAsc = bank.getTransactionsSorted("Lisa", true);
        List<Transaction> sortedResultDesc = bank.getTransactionsSorted("Lisa", false);

        assertNotEquals(sorted, sortedResultDesc);
        assertEquals(sorted, sortedResultAsc);
        assertNotEquals(sortedResultAsc, sortedResultDesc);
    }

    @Test
    void getTransactionsByType() {
        List<Transaction> positiveResult = bank.getTransactionsByType("Lisa", true);
        List<Transaction> negativeResult = bank.getTransactionsByType("Lisa", false);

        assertEquals(1, positiveResult.size());
        assertEquals(1, negativeResult.size());
        assertEquals(transactionsLisa.get(1), positiveResult.get(0));
        assertEquals(transactionsLisa.get(0), negativeResult.get(0));
    }

    @Test
    public void WriteReadTest() throws IOException {
        bank.setDirectoryName("fixtures");
        bank.readAccounts();
        UUID uuid = UUID.randomUUID();
        bank.setDirectoryName("test_Prak"+uuid.toString());

        bank.writeAccount("Hans");
        bank.writeAccount("Anna");

        assertFalse(bank.getTransactions("Hans").isEmpty());
    }


    @ParameterizedTest
    @ValueSource(strings = {"Anna", "Frank"})
    public void paramterizedTest(String pName){
        assertDoesNotThrow(() -> bank.createAccount(pName));
    }

}
