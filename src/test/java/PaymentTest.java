import bank.Payment;
import bank.exceptions.NumericValueInvalidException;
import org.junit.jupiter.api.Test;

import javax.security.sasl.Sasl;

import static org.junit.jupiter.api.Assertions.*;


/*
PaymentTest: Tests für Konstruktor, Copy-Konstruktor, calculate() (incomingInterest und out- goingInterest), equals, toString.
 */
public class PaymentTest {

    @Test
    public void testConstructors() throws NumericValueInvalidException {
            Payment p1 = new Payment("01.01.2018", "Gehalt", 2000);
            Payment p2 = new Payment("01.01.2019", "Gehalt", 2000, 0.1, 0.2);
            Payment p3 = new Payment(p2);
            assertFalse(p2.equals(p1));
            assertTrue(p2.equals(p3));
    }

    @Test
    public void calculateTest() throws NumericValueInvalidException {
            Payment p1 = new Payment("01.01.2018", "Gehalt", 2000);
            p1.setIncomingInterest(0.1);
            assertEquals(1800, p1.calculate());
            p1.setIncomingInterest(0.2);
            assertEquals(1600, p1.calculate());

            Payment p2 = new Payment("01.01.2018", "Miete", -800);
            p2.setOutgoingInterest(0.1);
            assertEquals(-880, p2.calculate());
            p2.setOutgoingInterest(0.2);
            assertEquals(-960, p2.calculate());

    }

    @Test
    public void stringTest() throws NumericValueInvalidException {
        Payment p1 = new Payment("01.01.2018", "Gehalt", 2000);

        String testData = """
                Date: 01.01.2018
                Description: Gehalt
                Amount: 2000.0
                Calculated amount: 2000.0
                Incoming Interest: 0.0
                Outgoing Interest: 0.0
                """;

        assertEquals(testData, p1.toString());
        assertThrows(
                NumericValueInvalidException.class,
                () -> new Payment("01.01.2018", "Gehalt", 0)
        );



    }
}
