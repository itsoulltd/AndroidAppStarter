package lab.infoworks.libshared.util.crypto;

import android.app.Application;
import android.content.Context;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.concurrent.locks.ReentrantLock;

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
    private AppStorage appStorage;
    private Cryptor cryptor;

    public SecretKeyStore(Context context, CryptoAlgorithm keyAlgorithm, Cryptor cryptor) {
        this.cryptor = cryptor;
        this.appStorage = new AppStorage(context);
        this.keyStore = new AndroidKeyStore(context, keyAlgorithm);
    }

    public SecretKeyStore(Context context, CryptoAlgorithm keyAlgorithm){
        this(context, keyAlgorithm, Cryptor.create());
    }

    public SecretKeyStore(Context context){
        this(context, CryptoAlgorithm.RSA);
    }

    private AndroidKeyStore getKeyStore(){
        return keyStore;
    }

    private AppStorage getAppStorage() {
        return appStorage;
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
            getKeyStore().createKey(alias);
            String encrypted = getKeyStore().encrypt(alias, secret);
            getAppStorage().put(alias, encrypted);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getStoredSecret(String alias) throws RuntimeException {
        try {
            String encrypted = getAppStorage().stringValue(alias);
            return getKeyStore().decrypt(alias, encrypted);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
