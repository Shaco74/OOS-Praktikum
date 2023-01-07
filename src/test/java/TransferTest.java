import bank.IncomingTransfer;
import bank.OutgoingTransfer;
import bank.exceptions.NumericValueInvalidException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferTest {
    @Test
    public void testConstructors() throws NumericValueInvalidException {
        IncomingTransfer t1 = new IncomingTransfer("01.01.2018", "Gehalt", 2000);
        t1.setSender("Frank");
        t1.setRecipient("Lisa");
        IncomingTransfer t2 = new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Ralf", "Lisa");

        OutgoingTransfer t3 = new OutgoingTransfer("01.01.2018", "Gehalt", 2000);
        t3.setRecipient("Frank");
        t3.setSender("Lisa");
        OutgoingTransfer t4 = new OutgoingTransfer("01.01.2019", "Gehalt", 2000, "Lisa", "Ralf");
        OutgoingTransfer t5 = new OutgoingTransfer(t4);

        // Test constructors, copy, equals
        assertNotEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertNotEquals(t1, t4);
        assertNotEquals(t3, t4);
        assertEquals(t4, t5);
    }

    @Test
    public void calculateTest() throws NumericValueInvalidException {
        IncomingTransfer t1 = new IncomingTransfer("01.01.2018", "Gehalt", 2000);
        t1.setSender("Frank");
        t1.setRecipient("Lisa");
        IncomingTransfer t2 = new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Ralf", "Lisa");

        OutgoingTransfer t4 = new OutgoingTransfer("01.01.2019", "Gehalt", 2000, "Lisa", "Ralf");
        OutgoingTransfer t5 = new OutgoingTransfer(t4);

        assertEquals(t2.calculate(), 2000);
        assertEquals(t1.calculate(), 2000);

        assertEquals(t4.calculate(), -2000);
        assertEquals(t4.calculate(), t5.calculate());

    }

    @Disabled("Disabled due to whitespace / newline bug related to os")
    @Test
    public void stringTest() throws NumericValueInvalidException {
        IncomingTransfer t1 = new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Ralf", "Lisa");
        OutgoingTransfer t2 = new OutgoingTransfer("01.01.2019", "Gehalt", 2000, "Lisa", "Ralf");

        String testData1 = """
                Date: 01.01.2019
                Description: Gehalt
                Amount: 2000.0
                Calculated amount: 2000.0
                Sender: Ralf
                Recipient: Lisa
                """;

        String testData2 = """
                Date: 01.01.2019
                Description: Gehalt
                Amount: 2000.0
                Calculated amount: -2000.0
                Sender: Lisa
                Recipient: Ralf
                """;

        assertEquals(testData1, t1.toString());
        assertNotEquals(testData1, t2.toString());
        assertEquals(testData2, t2.toString());
    }
}
