package lab.infoworks.libshared.util.crypto.definition;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import lab.infoworks.libshared.util.crypto.impl.AESCryptor;
import lab.infoworks.libshared.util.crypto.models.CryptoAlgorithm;
import lab.infoworks.libshared.util.crypto.models.HashKey;
import lab.infoworks.libshared.util.crypto.models.Transformation;

public interface Cryptor {

    static Cryptor create(){return new AESCryptor();}

    Key getKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    String encrypt(String secret, String strToEncrypt);
    String decrypt(String secret, String strToDecrypt);

    CryptoAlgorithm getAlgorithm();
    Transformation getTransformation();
    HashKey getHashKey();
}
