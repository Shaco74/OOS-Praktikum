package bank;

import com.google.gson.JsonElement;

public interface IJsonDeserializer<G> {

    public G deserialize(String pJSON);
}
