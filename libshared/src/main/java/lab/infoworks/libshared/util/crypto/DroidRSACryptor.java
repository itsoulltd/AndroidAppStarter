package lab.infoworks.libshared.util.crypto;

import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import lab.infoworks.libshared.BuildConfig;

public class DroidRSACryptor implements Cryptor{

    public static final String TAG = DroidRSACryptor.class.getSimpleName();
    private Cipher cipher;
    private Cipher decipher;
    private KeyStore keyStore;
    private Key pbKey;
    private final boolean isDebugMode;

    private final Transformation transformation;
    private final CryptoAlgorithm cryptoAlgorithm;

    public DroidRSACryptor(KeyStore keyStore) {
        this.keyStore = keyStore;
        this.transformation = Transformation.RSA_ECB_PKCS1Padding;
        this.cryptoAlgorithm = CryptoAlgorithm.RSA;
        this.isDebugMode = BuildConfig.DEBUG;
    }

    public CryptoAlgorithm getAlgorithm() {return cryptoAlgorithm;}
    public Transformation getTransformation() {return transformation;}
    private KeyStore getKeyStore(){return keyStore;}

    private Cipher cipherForRSA() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) { // below android m
            // error in android 6: InvalidKeyException: Need RSA private or public key
            return Cipher.getInstance(transformation.value(), "AndroidOpenSSL");
        }
        else { // android m and above
            // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
            return Cipher.getInstance(transformation.value(), "AndroidKeyStoreBCWorkaround");
        }
    }

    @Override
    public Key getKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (pbKey == null) throw new NoSuchAlgorithmException("Public/Private Key Didn't Initiated!");
        return pbKey;
    }

    private Key getPbKey(String alias, boolean forEncrypt) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        //
        KeyStore.Entry entry = getKeyStore().getEntry(alias, null);
        if ((entry instanceof KeyStore.PrivateKeyEntry) == false){
            throw new KeyStoreException("KeyStore didn't generated public/private key!");
        }
        //We use public-key for encrypt data and private-key for decrypt data.
        if (forEncrypt){
            pbKey = ((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey();
        }else {
            pbKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        }
        return pbKey;
    }

    @Override
    public String encrypt(String alias, String strToEncrypt) {
        try {
            if (cipher == null){
                Cipher input = cipherForRSA();
                input.init(Cipher.ENCRYPT_MODE, getPbKey(alias, true));
                cipher = input;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CipherOutputStream cios = new CipherOutputStream(baos, cipher);
            cios.write(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            cios.close();
            //
            byte [] encryptedBytes = baos.toByteArray();
            String encrypted = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            if(isDebugMode) Log.d(TAG, "encryptUsingRsaPublicKey: " + encrypted);
            return encrypted;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException
                | IOException | InvalidKeyException
                | UnrecoverableEntryException | KeyStoreException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String decrypt(String alias, String encrypted) {
        try {
            if (decipher == null){
                Cipher cipher = cipherForRSA();
                cipher.init(Cipher.DECRYPT_MODE, getPbKey(alias, false));
                decipher = cipher;
            }
            if(isDebugMode) Log.d(TAG, "decryptUsingRsaPrivateKey: " + encrypted);
            byte[] encryptedBytes = Base64.decode(encrypted, Base64.DEFAULT);
            CipherInputStream cis = new CipherInputStream(new ByteArrayInputStream(encryptedBytes), decipher);
            byte[] readBytes = IOUtils.readInputStreamFully(cis);
            cis.close();
            //
            String decrypted = new String(readBytes, 0, readBytes.length, StandardCharsets.UTF_8);
            return decrypted;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | NoSuchProviderException | InvalidKeyException
                | IOException | KeyStoreException | UnrecoverableEntryException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public HashKey getHashKey() {
        return null;
    }
}
