package lab.infoworks.libshared.util.crypto.definition;

public interface IVectorIO {
    boolean isSaved(String alias);
    byte[] read(String alias);
    void write(String alias, byte[] bytes);
}
