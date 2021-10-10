package lab.infoworks.libshared.util.crypto;

import android.app.Application;
import android.content.Context;
import android.security.keystore.KeyProperties;

import java.lang.ref.WeakReference;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.util.concurrent.locks.ReentrantLock;

import javax.crypto.SecretKey;

import lab.infoworks.libshared.BuildConfig;
import lab.infoworks.libshared.domain.shared.AppStorage;

public class SecretKeyStore implements iSecretKeyStore{

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    public static final String TAG = SecretKeyStore.class.getSimpleName();
    private static volatile SecretKeyStore instance;
    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();

    public static SecretKeyStore init(Application context, CryptoAlgorithm keyAlgorithm, Cryptor cryptor){
        if (instance == null){
            REENTRANT_LOCK.lock();
            try {
                if (instance == null){
                    keyAlgorithm = (keyAlgorithm != null)
                            ? keyAlgorithm
                            : CryptoAlgorithm.RSA;
                    instance = new SecretKeyStore(context, keyAlgorithm, cryptor);
                }
            } catch (Exception e){ }
            finally {
                REENTRANT_LOCK.unlock();
            }
        }
        return instance;
    }

    public static SecretKeyStore init(Application context, Cryptor cryptor){
        return init(context, CryptoAlgorithm.RSA, cryptor);
    }

    public static SecretKeyStore init(Application context, CryptoAlgorithm keyAlgorithm){
        return init(context, keyAlgorithm, Cryptor.create());
    }

    public static SecretKeyStore init(Application context){
        return init(context, CryptoAlgorithm.RSA, Cryptor.create());
    }

    public static SecretKeyStore getInstance() throws RuntimeException{
        if (instance == null) throw new RuntimeException("Not instantiated!");
        return instance;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private AndroidKeyStore keyStore;
    private WeakReference<Context> weakContext;
    private AppStorage appStorage;
    private Cryptor cryptor;
    private final String keyAlgorithm;
    private final boolean isDebugMode;

    public SecretKeyStore(Context context, CryptoAlgorithm keyAlgorithm, Cryptor cryptor) {
        this.cryptor = cryptor;
        this.weakContext = new WeakReference<>(context);
        this.appStorage = new AppStorage(context);
        this.keyAlgorithm = convertAlgorithm(keyAlgorithm);
        this.isDebugMode = BuildConfig.DEBUG;
        this.keyStore = new AndroidKeyStore(context, keyAlgorithm);
    }

    public SecretKeyStore(Context context, CryptoAlgorithm keyAlgorithm){
        this(context, keyAlgorithm, Cryptor.create());
    }

    public SecretKeyStore(Context context){
        this(context, CryptoAlgorithm.RSA);
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

    private AndroidKeyStore getKeyStore(){
        return keyStore;
    }

    private AppStorage getAppStorage() {
        return appStorage;
    }

    private Context getContext() {
        return weakContext.get();
    }

    @Override
    public Cryptor getCryptor() {
        return cryptor;
    }

    public String encrypt(String alias, String text) throws RuntimeException{
        String secret = getStoredSecret(alias);
        return getCryptor().encrypt(secret, text);
    }

    public String decrypt(String alias, String text) throws RuntimeException{
        String secret = getStoredSecret(alias);
        return getCryptor().decrypt(secret, text);
    }

    public void storeSecret(String alias, String secret, boolean replace) throws RuntimeException{
        try {
            if (replace == false){
                String encryptedSecret = getAppStorage().stringValue(alias);
                if (encryptedSecret != null && !encryptedSecret.isEmpty()) return;
            }
            Key pbKey = getKeyStore().encryptKey(alias);
            if (pbKey instanceof PublicKey){
                String encrypted = getKeyStore().encryptUsingRsaPublicKey((PublicKey)pbKey, secret);
                getAppStorage().put(alias, encrypted);
            }else if (pbKey instanceof SecretKey){
                String encrypted = getKeyStore().encryptUsingAesSecretKey((SecretKey)pbKey, secret);
                getAppStorage().put(alias, encrypted);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getStoredSecret(String alias) throws RuntimeException {
        try {
            String encrypted = getAppStorage().stringValue(alias);
            Key pbKey = getKeyStore().decryptKey(alias);
            if (pbKey instanceof PrivateKey){
                return getKeyStore().decryptUsingRsaPrivateKey((PrivateKey) pbKey, encrypted);
            } else if (pbKey instanceof SecretKey){
                return getKeyStore().decryptUsingAesSecretKey((SecretKey) pbKey, encrypted);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "";
    }

}
