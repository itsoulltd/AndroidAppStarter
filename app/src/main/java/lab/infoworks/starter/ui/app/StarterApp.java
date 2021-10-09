package lab.infoworks.starter.ui.app;

import android.app.Application;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lab.infoworks.libshared.domain.remote.DownloadTracker;
import lab.infoworks.starter.BuildConfig;

public class StarterApp extends Application {

    public static final String SECRET_ALIAS = StarterApp.class.toString();
    public static final Executor executor = Executors.newFixedThreadPool(4);

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing GeoTracker
        Log.i("StarterApp", "API Gateway: " + BuildConfig.api_gateway);

        //Generate Device UUID:
        //UUID uuid = new DeviceUuid(getApplicationContext()).getUuid();
        //Save the device uuid into KeyStore:
        //SecretKeyStore.init(this).storeSecret(SECRET_ALIAS, uuid.toString(), false);
        //Retrieve the saved uuid from KeyStore:
        //String secret = SecretKeyStore.getInstance().getStoredSecret(SECRET_ALIAS);

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
