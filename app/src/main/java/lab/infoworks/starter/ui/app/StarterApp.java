package lab.infoworks.starter.ui.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
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

    public static boolean isServiceRunning(Context context, Class<? extends Service> serviceCls){
        boolean result = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceCls.getName().equals(service.service.getClassName())) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static void bringToFront(Context context, Class<? extends Activity> activity, long delayInMillis){
        delayInMillis = (delayInMillis < 0l) ? 0l : delayInMillis;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent bringMeFront = new Intent(context.getApplicationContext(), activity);
            bringMeFront.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(bringMeFront);
        }, delayInMillis);
    }

}
