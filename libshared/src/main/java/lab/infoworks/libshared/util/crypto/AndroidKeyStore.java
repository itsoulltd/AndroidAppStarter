package lab.infoworks.libshared.util.crypto;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import lab.infoworks.libshared.BuildConfig;

public class AndroidKeyStore implements iKeyStore{

    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    public static final String TAG = AndroidKeyStore.class.getSimpleName();

    private KeyStore keyStore;
    private WeakReference<Context> weakContext;
    private final String keyAlgorithm;
    private final boolean isDebugMode;
    private Calendar end;
    private Cryptor aesCryptor;
    private Cryptor rsaCryptor;

    public AndroidKeyStore(Context context, CryptoAlgorithm keyAlgorithm) {
        this.weakContext = new WeakReference<>(context);
        this.keyAlgorithm = convertAlgorithm(keyAlgorithm);
        this.isDebugMode = BuildConfig.DEBUG;
        this.end = Calendar.getInstance();
        this.end.add(Calendar.YEAR, 1);
    }

    public AndroidKeyStore(Context context, CryptoAlgorithm keyAlgorithm, Calendar end){
        this(context, keyAlgorithm);
        this.end = end;
    }

    private Cryptor getAesCryptor(){
        if (aesCryptor == null){
            aesCryptor = new DroidAESCryptor(getKeyStore());
        }
        return aesCryptor;
    }

    private Cryptor getRsaCryptor(){
        if (rsaCryptor == null){
            rsaCryptor = new DroidRSACryptor(getKeyStore());
        }
        return rsaCryptor;
    }

    private String convertAlgorithm(CryptoAlgorithm algorithm){
        String _keyAlgorithm = "";
        switch (algorithm){
            case AES:
                _keyAlgorithm = KeyProperties.KEY_ALGORITHM_AES;
                break;
            case DESede:
            case TripleDES:
            case DES:
                _keyAlgorithm = KeyProperties.KEY_ALGORITHM_3DES;
                break;
            default:
                _keyAlgorithm = KeyProperties.KEY_ALGORITHM_RSA;
        }
        //If the android os version is lower than API-Level-23, then key-algorithm must be RSA:
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){
            _keyAlgorithm = KeyProperties.KEY_ALGORITHM_RSA;
        }
        return _keyAlgorithm;
    }

    private KeyStore getKeyStore() throws RuntimeException {
        if (keyStore == null){
            try {
                keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
                keyStore.load(null);
            } catch (KeyStoreException | NoSuchAlgorithmException
                    | IOException | CertificateException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return keyStore;
    }

    private Context getContext() {
        return weakContext.get();
    }

    @Override
    public Key createKey(String alias) throws RuntimeException {
        //Pre-Check if already created:
        try {
            if (getKeyStore().containsAlias(alias)) return null;
            //
            if (keyAlgorithm.equalsIgnoreCase(KeyProperties.KEY_ALGORITHM_AES)
                    && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                //
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
                KeyGenParameterSpec.Builder builder =
                        new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
                builder.setKeySize(256);
                builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC);
                builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
                keyGenerator.init(builder.build());
                // This key will work with a CipherObject ...
                SecretKey key = keyGenerator.generateKey();
                return key;
            }
            //
            if(keyAlgorithm.equalsIgnoreCase(KeyProperties.KEY_ALGORITHM_RSA)) {
                //
                Calendar start = Calendar.getInstance();
                KeyPairGenerator generator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(getContext())
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                generator.initialize(spec);
                // This key will work with a CipherObject ...
                KeyPair pair = generator.generateKeyPair();
                return pair.getPublic();
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException
                | InvalidAlgorithmParameterException | KeyStoreException e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    @Override
    public String encrypt(String alias, String text) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        if (keyAlgorithm == KeyProperties.KEY_ALGORITHM_RSA){
            return getRsaCryptor().encrypt(alias, text);
        } else if (keyAlgorithm == KeyProperties.KEY_ALGORITHM_AES){
            return getAesCryptor().encrypt(alias, text);
        }
        return null;
    }

    @Override
    public String decrypt(String alias, String encrypted) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
        if (keyAlgorithm == KeyProperties.KEY_ALGORITHM_RSA){
            return getRsaCryptor().decrypt(alias, encrypted);
        } else if (keyAlgorithm == KeyProperties.KEY_ALGORITHM_AES){
            return getAesCryptor().decrypt(alias, encrypted);
        }
        return null;
    }

}
