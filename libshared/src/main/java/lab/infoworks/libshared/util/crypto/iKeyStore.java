package lab.infoworks.libshared.util.crypto;

import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;

import javax.crypto.SecretKey;

public interface iKeyStore {

    Key createKey(String alias) throws RuntimeException;
    Key encryptKey(String alias) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException;
    Key decryptKey(String alias) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException;

    String decryptUsingRsaPrivateKey(PrivateKey key, String encrypted) throws RuntimeException;
    String decryptUsingAesSecretKey(SecretKey key, String encrypted) throws RuntimeException;

    String encryptUsingRsaPublicKey(PublicKey pbKey, String secret) throws RuntimeException;
    String encryptUsingAesSecretKey(SecretKey pbKey, String secret) throws RuntimeException;
}
