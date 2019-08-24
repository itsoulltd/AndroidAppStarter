package lab.itsoul.com.deliman.rider.startup.controllers;

import android.app.Application;

import com.itsoul.lab.android.client.GeoTrackerDroidKit;
import com.itsoul.lab.android.models.AppVersion;
import com.itsoul.lab.client.GeoTracker;

import lab.itsoul.com.deliman.app.startup.BuildConfig;

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //FIXME:
        AppVersion version = new AppVersion();
        version.setBuild(BuildConfig.VERSION_NAME);
        version.setVersion(String.valueOf(BuildConfig.VERSION_CODE));
        version.setAppType("android");
        version.setCloudDNS("dailygoods.com");
        //FIXME: Must be given:
        GeoTrackerDroidKit.shared().initialize("!@#$", "!@#$%^&", version);
        GeoTracker.shared().activateCircuitBreaker(false);
        GeoTracker.shared().setDebuggingOn(true);
        //
    }
}
