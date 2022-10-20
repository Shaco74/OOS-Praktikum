import bank.Payment;
import bank.Transfer;

public class Main {
    public static void main(String[] args) {
        Payment p1 = new Payment("2022-01-01", "P1 description", 100, 0.1, 0.12);
        Payment p2 = new Payment("2022-01-02", "P2 description", 200);
        System.out.println(p2.getIncomingInterest());
        p2.setIncomingInterest(0.05);
        p2.setOutgoingInterest(0.12);
        Payment p3 = new Payment(p2);
        p3.setDescription("P3 description");
        p3.setDate("2022-01-03");


        Transfer t1 = new Transfer("2022-01-01", "T1 description", 100);
        t1.setSender("T1 sender");
        t1.setRecipient("T1 recipient");
        Transfer t2 = new Transfer("2022-01-02", "T2 description", 200, "T2 sender", "T2 recipient");
        Transfer t3 = new Transfer(t2);
        t3.setDescription("T3 description");
        t3.setDate("2022-01-03");

        System.out.println("-----------------");
        System.out.println(t3.toString());
        System.out.println("-----------------");
        System.out.println(p3.toString());
        System.out.println("-----------------");

        System.out.println(p3.equals(p2));
        p2.setAmount(p3.getAmount());
        p2.setDate(p3.getDate());
        p2.setDescription(p3.getDescription());
        System.out.println(p3.equals(p2));

    }
}
