package lab.itsoul.com.deliman.rider.startup.controllers;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.itsoul.lab.android.client.GeoTrackerDroidKit;
import com.itsoul.lab.android.interactor.rest.HttpTemplate;
import com.itsoul.lab.android.models.AppVersion;
import com.itsoul.lab.client.GeoTracker;
import com.itsoul.lab.domain.base.Consume;
import com.itsoul.lab.domain.base.QueryParam;
import com.itsoul.lab.interactor.exceptions.HttpInvocationException;
import com.itsoul.lab.interactor.interfaces.Interactor;

import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.validation.constraints.NotNull;

import lab.itsoul.com.deliman.rider.startup.BuildConfig;
import lab.itsoul.com.deliman.rider.startup.activities.BaseActivity;

public class AppVersionController {

    private Executor executor;

    private Executor getExecutor() {
        if (executor == null){
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    public void checkAppVersion(Handler handler){
        Message msg = new Message();
        getExecutor().execute(() -> {
            AppVersion version = getAppVersion(false);
            if (version == null) {
                msg.obj = false;
                handler.sendMessage(msg);
            }else {
                GeoTrackerDroidKit.shared().setVersion(version);
                try {
                    int versionCode = BuildConfig.VERSION_CODE;
                    //String versionName = BuildConfig.VERSION_NAME;
                    int currentCode = Double.valueOf(version.getVersion()).intValue();
                    //Handle with msg
                    msg.obj = (currentCode > versionCode && version.isShouldUpdate());
                } catch (Exception e) {}
                finally {
                    handler.sendMessage(msg);
                }
            }
        });
    }

    public AppVersion getAppVersion(boolean offline){
        if (offline){
            return GeoTrackerDroidKit.shared().getVersion();
        }else {
            return appVersion();
        }
    }

    protected AppVersion appVersion(){
        URI url = URI.create(domain() + "/api/appVersion");
        try(HttpTemplate<AppVersion, Consume> resetTemp = Interactor.create(HttpTemplate.class, url, AppVersion.class)) {
            AppVersion response = resetTemp.get(null, new QueryParam("type", "android"));
            return response;
        } catch (HttpInvocationException e) {
            if (GeoTracker.shared().isDebuggingOn()) Log.d("appVersion", e.getMessage());
        } catch (IllegalAccessException e) {
            if (GeoTracker.shared().isDebuggingOn()) Log.d("appVersion", e.getMessage());
        } catch (InstantiationException e) {
            if (GeoTracker.shared().isDebuggingOn()) Log.d("appVersion", e.getMessage());
        }
        return null;
    }

    protected @NotNull String domain() {
        //return "http://192.168.0.199:8083"; //FIXME-When local network:
        return "http://117.58.247.50:8083"; //FIXME-When out of local network:
    }

    public void fetchAsyncAppVersion(Handler handler){
        Message msg = new Message();
        getExecutor().execute(() -> {
            AppVersion version = getAppVersion(false);
            if (version == null) {
                msg.obj = "Service Unavailable. Please try again later. Thanks";
                handler.sendMessage(msg);
            }else {
                if (version.isAuthorized()){
                    GeoTrackerDroidKit.shared().setVersion(version);
                    msg.obj = version;
                }else {
                    msg.obj = version.getError();
                }
                handler.sendMessage(msg);
            }
        });
    }

    public void showAppUpdateAlertDialog(BaseActivity context, String title, String message, String cancelTitle){
        AlertDialog.Builder builder = context.createAlertBuilder();
        if (cancelTitle == null){
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Update", (dialog, which) -> {
                        //GOTO: PlayStore:
                        context.gotoPlayStore();
                    })
                    .show();
        }else {
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Update", (dialog, which) -> {
                        //GOTO: PlayStore:
                        context.gotoPlayStore();
                    })
                    .setNegativeButton(cancelTitle, null)
                    .show();
        }
    }

}
