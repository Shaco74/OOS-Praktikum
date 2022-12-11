package bank;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonDeserializerImpl<Transaction> implements IJsonDeserializer<Transaction> {

    public JsonDeserializerImpl(){};

    public Transaction deserialize(String pJSON) {
        JsonElement json = JsonParser.parseString(pJSON);
        JsonObject result = json.getAsJsonObject();

        String classname = result.get("CLASSNAME").getAsString();
        JsonObject instance = result.get("INSTANCE").getAsJsonObject();

        if(classname.equals("IncomingTransfer")){
            IncomingTransfer resultObject = new Gson().fromJson(instance, IncomingTransfer.class);
            return (Transaction) resultObject;
        }else if(classname.equals("OutgoingTransfer")){
            OutgoingTransfer resultObject = new Gson().fromJson(instance, OutgoingTransfer.class);
            return (Transaction) resultObject;
        }else if(classname.equals("Transfer")){
            Transfer resultObject = new Gson().fromJson(instance, Transfer.class);
            return (Transaction) resultObject;
        }else if(classname.equals("Payment")){
            Payment resultObject = new Gson().fromJson(instance, Payment.class);
            return (Transaction) resultObject;
        }

        return null;
    }
}
