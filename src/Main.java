import bank.*;
import bank.exceptions.NumericValueInvalidException;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            PrivateBank bank = new PrivateBank("Meine Bank", 0.2, 0.2);
            PrivateBankAlt bankAlt = new PrivateBankAlt("Meine BankAlt", 0.2, 0.2);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Hans"));
            transactions.add(new OutgoingTransfer("01.01.2019", "Einkauf", 200, "Hans", "Rewe"));
            transactions.add(new Payment("01.01.2019", "Miete", -500, 0.5, 0.5));

            List<Transaction> transactionsAlt = new ArrayList<>();
            transactionsAlt.add(new Transfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Hans"));
            transactionsAlt.add(new Transfer("01.01.2019", "Einkauf", 200, "Hans", "Rewe"));
            transactionsAlt.add(new Payment("01.01.2019", "Miete", -500, 0.1, 0.2));

            bank.createAccount("Hans", transactions);
            bankAlt.createAccount("Hans", transactionsAlt);

            //Test Contains and add/remove Transaction
            try {
                System.out.println("-----------------");
                System.out.println("Test Contains and add/remove Transaction");
                IncomingTransfer t = new IncomingTransfer("02.01.2019", "Test", 111, "Herr Mustermann", "Hans");
                bank.addTransaction("Hans", t);
                System.out.println(bank.containsTransaction("Hans", t));
                bank.removeTransaction("Hans", t);
                System.out.println(!bank.containsTransaction("Hans", t));
                System.out.println("-----------------");
            } catch (Exception e) {
                System.out.println(e);
            }


            // Test amount Exception
            try {
                Transfer t = new Transfer("01.01.2019", "Gehalt", -2000, "Herr Mustermann", "Hans");
            } catch (NumericValueInvalidException e) {
                System.out.println("-----------------");
                System.out.println("Test amount Exception");
                System.out.println(e);
                System.out.println("-----------------");

            }
            // Test incomingException Exception
            try {
                PrivateBank b = new PrivateBank("Meine Bank", 0.3, 0.3);
            } catch (NumericValueInvalidException e) {
                System.out.println("-----------------");
                System.out.println("Test incomingException Exception");
                System.out.println(e);
                System.out.println("-----------------");
            }

            PrivateBank b = new PrivateBank("Meine Bank", 0.1, 0.2);
            PrivateBankAlt bAlt = new PrivateBankAlt("Meine Bank", 0.1, 0.2);

            PrivateBank bankCopy = new PrivateBank(b);
            PrivateBankAlt bankAltCopy = new PrivateBankAlt(bAlt);

            System.out.println("-----------------");
            System.out.println("Account Balance Test");
            System.out.println(bank.getAccountBalance("Hans"));
            System.out.println(bankAlt.getAccountBalance("Hans"));
            System.out.println("-----------------");

            System.out.println("Equals/Copy Test");
            System.out.println(bankCopy.equals(b));
            System.out.println(bankAltCopy.equals(bAlt));
            bankAlt.setOutgoingInterest(0.3);
            System.out.println(bankAltCopy.equals(bAlt));

            System.out.println("-----------------");
            System.out.println("Print Test");
            System.out.println("-----------------");

            System.out.println(bank);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(e.getMessage());
        }

        try {

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("GSON TEST");
            IncomingTransfer t = new IncomingTransfer("02.01.2019", "Test", 111, "Herr Mustermann", "Hans");

            JsonSerializerImpl serializer = new JsonSerializerImpl();

            //System.out.println(serializer.serialize(t));

            JsonDeserializerImpl<IncomingTransfer> deserializer = new JsonDeserializerImpl<IncomingTransfer>();

            IncomingTransfer iT = deserializer.deserialize(serializer.serialize(t));

            PrivateBank bank = new PrivateBank("Meine Bank", 0.2, 0.2);

            /*
            List<Transaction> transactions = new ArrayList<>();
            transactions.add(new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Hans"));
            transactions.add(new OutgoingTransfer("01.01.2019", "Einkauf", 200, "Hans", "Rewe"));
            transactions.add(new Payment("01.01.2019", "Miete", -500, 0.5, 0.5));
            bank.createAccount("Hans", transactions);

            List<Transaction> transactions2 = new ArrayList<>();
            transactions2.add(new IncomingTransfer("01.01.2019", "Gehalt", 2000, "Herr Mustermann", "Hans"));
            transactions2.add(new OutgoingTransfer("01.01.2019", "Einkauf", 200, "Hans", "Rewe"));
            transactions2.add(new Payment("01.01.2019", "Miete", -500, 0.5, 0.5));
            bank.createAccount("Anna", transactions2);
*/

            bank.setDirectoryName("test22");
            bank.readAccounts();
            bank.setDirectoryName("test_12012022");

            bank.writeAccount("Hans");
            bank.writeAccount("Anna");


        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
