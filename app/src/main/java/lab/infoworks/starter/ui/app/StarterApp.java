package lab.infoworks.starter.ui.app;

import android.app.Application;
import android.util.Log;

import lab.infoworks.starter.BuildConfig;

public class StarterApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing GeoTracker
        Log.i("StarterApp", "API Gateway: " + BuildConfig.api_gateway);
        //
    }
}
