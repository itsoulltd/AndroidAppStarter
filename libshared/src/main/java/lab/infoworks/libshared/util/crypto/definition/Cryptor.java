package lab.infoworks.libshared.util.crypto;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public interface Cryptor {

    static Cryptor create(){return new AESCryptor();}

    Key getKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    String encrypt(String secret, String strToEncrypt);
    String decrypt(String secret, String strToDecrypt);

    CryptoAlgorithm getAlgorithm();
    Transformation getTransformation();
    HashKey getHashKey();
}
