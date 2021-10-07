package lab.infoworks.libshared.util.crypto;

public interface iSecretKeyStore {
    Cryptor getCryptor();
    String encrypt(String alias, String text) throws RuntimeException;
    String decrypt(String alias, String text) throws RuntimeException;
    void storeSecret(String alias, String secret, boolean replace) throws RuntimeException;
    String getStoredSecret(String alias) throws RuntimeException;
}
