package lab.infoworks.libshared.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationPermissionController implements AutoCloseable{

    private LocationPermissionsObserver _observer;
    public LocationPermissionController() {}

    public interface LocationPermissionsObserver{
        void locationPermissionsDidSuccessful(Object... observant);
        void locationPermissionsDidFailed(String error);
    }

    public static final int LOCATION_ACCESS_PERMISSION_GRANTED = 0;
    private static String[] LOCATION_PERMISSION_LIST = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @RequiresApi(Build.VERSION_CODES.P)
    boolean isPermissionGranted(Activity context){
        PackageManager pm = context.getPackageManager();
        if (pm.checkPermission(LOCATION_PERMISSION_LIST[0], context.getPackageName()) == PackageManager.PERMISSION_GRANTED
        || pm.checkPermission(LOCATION_PERMISSION_LIST[1], context.getPackageName()) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    public boolean isPermissionGrantedAtRuntime(Activity context){
        if (ContextCompat.checkSelfPermission(context, LOCATION_PERMISSION_LIST[0]) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(context, LOCATION_PERMISSION_LIST[1]) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    public void setLocationPermissionObserver(LocationPermissionsObserver observer){
        _observer = observer;
    }

    public void askForPermissionAtRuntime(Activity context){
        if (LocationPermissionsObserver.class.isAssignableFrom(context.getClass())){
            _observer = (LocationPermissionsObserver) context;
        }
        if (!isPermissionGrantedAtRuntime(context)){
            ActivityCompat.requestPermissions(context, LOCATION_PERMISSION_LIST, LOCATION_ACCESS_PERMISSION_GRANTED);
        }else {
            if (_observer != null){
                _observer.locationPermissionsDidSuccessful();
            }
        }
    }

    public void handleRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults){
        switch (requestCode) {
            case LOCATION_ACCESS_PERMISSION_GRANTED: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (_observer != null){
                        _observer.locationPermissionsDidSuccessful();
                    }
                } else {
                    if (_observer != null){
                        _observer.locationPermissionsDidFailed("Location Access Permission Denied!");
                    }
                }
            }
        }

    }

    public interface LocationProviderObserver{
        void observeLocationProviderStatus(boolean active, String[] providers);
    }

    private LocationProviderObserver _providerObserver;
    private ExecutorService exe;
    private ExecutorService getExe() {
        if (exe == null){
            exe = Executors.newSingleThreadExecutor();
        }
        return exe;
    }

    public void setLocationProviderObserver(LocationProviderObserver observer){
        _providerObserver = observer;
    }

    public void checkLocationProviderAvailability(Activity context){

        if (LocationProviderObserver.class.isAssignableFrom(context.getClass()))
            _providerObserver = (LocationProviderObserver) context;

        getExe().submit(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    if (_providerObserver != null) {
                        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        //
                        String[] providers = new String[0];
                        try {
                            providers = manager.getProviders(true).toArray(new String[0]);
                        } catch (Exception e) {}
                        //
                        final String[] resultProviders = providers;
                        boolean isTrue = manager.isLocationEnabled();
                        Log.d("LocationProvider(28)", "Available " + isTrue);
                        //
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                _providerObserver.observeLocationProviderStatus(isTrue, resultProviders);
                            }
                        });
                    }
                }else{
                    if (_providerObserver != null){
                        int locationMode = 0;
                        boolean isAvailable = false;
                        //
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                            try {
                                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                            } catch (Settings.SettingNotFoundException e) {
                                e.printStackTrace();
                            }

                            isAvailable = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
                        } else {
                            try {
                                String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                                isAvailable = !TextUtils.isEmpty(locationProviders);
                            } catch (Exception e) {
                                Log.d("LocationProvider", "run: " + e.getMessage());
                            }
                        }
                        //
                        boolean permissionCheck = isPermissionGrantedAtRuntime(context);
                        boolean isTrue = isAvailable && permissionCheck;
                        Log.d("LocationProvider(<28)", "Available " + isTrue);
                        //
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                _providerObserver.observeLocationProviderStatus(isTrue, new String[0]);
                            }
                        });
                    }
                }
            }
        });
    }

    private void stopExecution() throws Exception{
        if (exe != null && exe.isShutdown() == false){
            exe.shutdownNow();
            exe = null;
        }
    }

    @Override
    public void close() throws Exception {
        stopExecution();
        _providerObserver = null;
        _observer = null;
    }
}
