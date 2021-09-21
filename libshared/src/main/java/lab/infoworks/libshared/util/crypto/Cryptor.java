package lab.infoworks.libshared.util.crypto;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;

public interface Cryptor {

    static Cryptor create(){return new AESCryptor();}

    SecretKeySpec getKeySpace(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    String encrypt(String secret, String strToEncrypt);
    String decrypt(String secret, String strToDecrypt);
}
