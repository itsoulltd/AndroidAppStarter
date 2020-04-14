package lab.itsoul.com.deliman.starter.ui.app;

import android.app.Application;

import com.itsoul.lab.android.client.GeoTrackerDroidKit;
import com.itsoul.lab.android.models.AppVersion;
import com.itsoul.lab.client.GeoTracker;

import lab.itsoul.com.deliman.starter.startup.BuildConfig;

public class StarterApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing GeoTracker
        AppVersion version = new AppVersion();
        version.setAppType("android");
        version.setVersion(lab.itsoul.com.deliman.starter.startup.BuildConfig.VERSION_NAME);
        version.setBuild(lab.itsoul.com.deliman.starter.startup.BuildConfig.VERSION_CODE + "");
        version.setCloudDNS("10.2.2.2");
        //FIXME: Create New Tenant For This APP:
        GeoTrackerDroidKit.shared().initialize("--APP-ID--"
                , "--SECRET--"
                , version);
        GeoTracker.shared().activateCircuitBreaker(true);
        GeoTracker.shared().setDebuggingOn(BuildConfig.DEBUG);
        //
    }
}
