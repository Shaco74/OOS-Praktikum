package bank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

public class JsonSerializerImpl<Transaction> implements IJsonSerializer<Transaction> {

    public void JsonSerializer(){};

    @Override
    public String serialize(Transaction pObj) {
        HashMap<String,Object> map = new HashMap();
        map.put("INSTANCE", pObj);

        if(pObj instanceof IncomingTransfer){
            map.put("CLASSNAME", "IncomingTransfer");
        }else if(pObj instanceof OutgoingTransfer){
            map.put("CLASSNAME", "OutgoingTransfer");
        }else if(pObj instanceof Transfer){
            map.put("CLASSNAME", "Transfer");
        }else if(pObj instanceof Payment){
            map.put("CLASSNAME", "Payment");
        }else {
            map.put("CLASSNAME", "undefined");
        }

        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        return gson.toJson(map);
    }
}
