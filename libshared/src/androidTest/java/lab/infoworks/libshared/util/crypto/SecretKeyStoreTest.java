package lab.infoworks.libshared.util.crypto;

import android.app.Application;
import android.util.Log;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import lab.infoworks.libshared.util.crypto.models.CryptoAlgorithm;
import lab.infoworks.libshared.util.crypto.shared.SecretKeyStore;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SecretKeyStoreTest {

    Application appContext;

    @Before
    public void setUp() throws Exception {
        appContext = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
    }

    @Test
    public void secretRSAKeyStoreInitTest(){
        //Generate Device UUID:
        UUID uuid = UUID.randomUUID();
        String uuidStrA = uuid.toString();
        Log.d("StarterApp", "onCreate: stored uuid: " + uuidStrA);

        //Save the device uuid into KeyStore:
        SecretKeyStore.init(appContext).storeSecret("SECRET_ALIAS_RSA", uuidStrA, true);

        //Retrieve the saved uuid from KeyStore:
        String uuidStrB = SecretKeyStore.getInstance().getStoredSecret("SECRET_ALIAS_RSA");
        Log.d("StarterApp", "onCreate: retrieved uuid: " + uuidStrB);

        Assert.assertTrue(uuidStrA.length() == uuidStrB.length());
        Log.d("StarterApp", "onCreate: uuid Length is " + (uuidStrB.length() == uuidStrA.length() ? "equal" : "not-equal"));

        Assert.assertTrue(uuidStrA.equalsIgnoreCase(uuidStrB));
        Log.d("StarterApp", "onCreate: uuid Text is " + (uuidStrA.equalsIgnoreCase(uuidStrB) ? "equal" : "not-equal"));
    }

    @Test
    public void secretAESKeyStoreInitTest(){
        //Generate Device UUID:
        UUID uuid = UUID.randomUUID();
        String uuidStrA = uuid.toString();
        Log.d("StarterApp", "onCreate: stored uuid: " + uuidStrA);

        //Save the device uuid into KeyStore:
        SecretKeyStore.init(appContext, CryptoAlgorithm.AES).storeSecret("SECRET_ALIAS_AES", uuidStrA, true);

        //Retrieve the saved uuid from KeyStore:
        String uuidStrB = SecretKeyStore.getInstance().getStoredSecret("SECRET_ALIAS_AES");
        Log.d("StarterApp", "onCreate: retrieved uuid: " + uuidStrB);

        Assert.assertTrue(uuidStrA.length() == uuidStrB.length());
        Log.d("StarterApp", "onCreate: uuid Length is " + (uuidStrB.length() == uuidStrA.length() ? "equal" : "not-equal"));

        Assert.assertTrue(uuidStrA.equalsIgnoreCase(uuidStrB));
        Log.d("StarterApp", "onCreate: uuid Text is " + (uuidStrA.equalsIgnoreCase(uuidStrB) ? "equal" : "not-equal"));
    }
}