package lab.infoworks.starter.ui.app;

import android.app.Application;
import android.util.Log;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lab.infoworks.libshared.domain.remote.DownloadTracker;
import lab.infoworks.libshared.util.crypto.SecretKeyStore;
import lab.infoworks.starter.BuildConfig;
import lab.infoworks.starter.util.DeviceUuid;

public class StarterApp extends Application {

    public static final String SECRET_ALIAS = StarterApp.class.toString();
    public static final Executor executor = Executors.newFixedThreadPool(4);

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing GeoTracker
        Log.i("StarterApp", "API Gateway: " + BuildConfig.api_gateway);

        //Generate Device UUID:
        UUID uuid = new DeviceUuid(getApplicationContext()).getUuid();
        String uuidStrA = uuid.toString();
        Log.d("StarterApp", "onCreate: stored uuid: " + uuidStrA);

        //Save the device uuid into KeyStore:
        SecretKeyStore.init(this).storeSecret(SECRET_ALIAS, uuidStrA, false);

        //Retrieve the saved uuid from KeyStore:
        String uuidStrB = SecretKeyStore.getInstance().getStoredSecret(SECRET_ALIAS);
        Log.d("StarterApp", "onCreate: retrieved uuid: " + uuidStrB);
        Log.d("StarterApp", "onCreate: uuid Length is " + (uuidStrB.length() == uuidStrA.length() ? "equal" : "not-equal"));
        Log.d("StarterApp", "onCreate: uuid Text is " + (uuidStrA.equalsIgnoreCase(uuidStrB) ? "equal" : "not-equal"));

        //Register for Download Complete:
        DownloadTracker.registerReceiverForCompletion(this);
    }

    @Override
    public void onTerminate() {
        if (executor instanceof ExecutorService){
            if(!((ExecutorService) executor).isShutdown()){
                ((ExecutorService) executor).shutdown();
            }
        }
        super.onTerminate();
    }
}
