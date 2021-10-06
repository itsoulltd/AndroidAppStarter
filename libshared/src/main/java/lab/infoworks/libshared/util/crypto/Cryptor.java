package lab.infoworks.libshared.util.crypto;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

public interface Cryptor {

    static Cryptor create(){return new AESCryptor();}

    SecretKey getKeySpace(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    String encrypt(String secret, String strToEncrypt);
    String decrypt(String secret, String strToDecrypt);
}
