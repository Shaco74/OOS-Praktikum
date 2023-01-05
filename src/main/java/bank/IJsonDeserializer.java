package bank;

public interface IJsonDeserializer<G> {

    public G deserialize(String pJSON);
}
