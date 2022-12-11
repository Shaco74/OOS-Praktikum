package bank;

import org.junit.jupiter.api.BeforeEach;

public class PrivateBankTest {

    private PrivateBank bank;

    @BeforeEach
    void init() {
        try{
            bank = new PrivateBank("Meine Bank", 0.2, 0.2);
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
