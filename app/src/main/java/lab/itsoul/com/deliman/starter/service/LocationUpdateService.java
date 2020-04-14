package lab.itsoul.com.deliman.starter.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.itsoul.lab.android.client.AppStorage;
import com.itsoul.lab.android.interactor.ws.LocationProducer;
import com.itsoul.lab.android.models.HourGlass;
import com.itsoul.lab.client.APIContext;
import com.itsoul.lab.client.GeoTracker;
import com.itsoul.lab.domain.models.pipeline.GeoTrackerInfo;
import com.itsoul.lab.interactor.exceptions.UnauthorizedAccess;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class LocationUpdateService extends Service implements LocationProducer.LocationUpdateProducer{

    private static final String TAG = LocationUpdateService.class.getName();

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    public static final int LOCATION_UPDATE_INTERVAL = 2500;
    public static final int LOCATION_FASTEST_INTERVAL = 1000;
    public static final int SMALLEST_DISPLACEMENT = 0;
    public static final boolean ENABLE_TRACKING = true;

    private String trackerID;
    private String userID;

    private LocationProducer _producer;
    public LocationProducer getLocationProducer(){
        if (_producer == null) _producer = new LocationProducer(this, 1);
        return _producer;
    }

    private long timeOfSharing;
    private long sharingStartingTime;

    private HourGlass hourGlass;
    protected HourGlass getHourGlass() {
        return hourGlass;
    }

    private void createHourGlass(long startTimestamp, long duration){
        hourGlass = new HourGlass(new Date(startTimestamp), ((Long)duration).intValue());
    }

    public LocationUpdateService() {
        /**/
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService( intent.getStringExtra("tracker-id"),
                            intent.getStringExtra("user-id"),
                            intent.getLongExtra("time-of-sharing", 0),
                            intent.getLongExtra("sharing-starting-time", 0));
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService(false);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /* Used to build and start foreground service. */
    private void startForegroundService(String trackerID, String userID, long timeOfSharing, long sharingStartingTIme) {
        //if (GeoTracker.shared().isDebuggingOn()) Log.d(TAG, "Start foreground service.");
        // Start foreground service.
        this.trackerID = trackerID;
        this.userID = userID;
        //
        this.timeOfSharing = timeOfSharing;
        this.sharingStartingTime = sharingStartingTIme;
        createHourGlass(this.sharingStartingTime, this.timeOfSharing);
        //
        startForeground(1, buildNotification());
        startLocationUpdates();
    }

    private void stopForegroundService(boolean shouldSendStop) {
        //if (GeoTracker.shared().isDebuggingOn()) Log.d(TAG, "Stop foreground service.");
        stopLocationUpdate(shouldSendStop);
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }

    private void checkIfSharingTimeExpired() {
        Long currentTimeStamp = System.currentTimeMillis();
        if((currentTimeStamp - sharingStartingTime) >= timeOfSharing) {
            stopForegroundService(true);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Notification buildNotification() {
        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create notification builder.
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) builder = new NotificationCompat.Builder(this, createNotificationChannel("com.itsoul.trackme", "location update service"));
        else builder = new NotificationCompat.Builder(this, "");

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Location update implemented by foreground service.");
        bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());

        // Make the notification max priority.
        builder.setPriority(Notification.PRIORITY_MAX);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);

        // Build the notification.
        return builder.build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        return channelId;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        GeoTrackerInfo trackerInfo = new GeoTrackerInfo();
        trackerInfo.setAccessToken(AppStorage.getCurrent(getApplication()).stringValue("access-token"));
        trackerInfo.setTenantID(APIContext.APPID.value());
        trackerInfo.setTrackID(this.trackerID);
        trackerInfo.setUserID(this.userID);
        try {
            getLocationProducer().start(trackerInfo, getHourGlass(), this);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnauthorizedAccess unauthorizedAccess) {
            unauthorizedAccess.printStackTrace();
        }
    }

    @Override
    public void handleProducedLocationUpdate(Location location) {
        if (location != null) {
            if (GeoTracker.shared().isDebuggingOn()) Log.d(TAG, String.format("handleProducedLocationUpdate: (%s) lat# %s, long# %s", location.getProvider(), location.getLatitude(), location.getLongitude()));
            checkIfSharingTimeExpired();
        }
        if (GeoTracker.shared().isDebuggingOn()) Log.d(TAG, "LocationUpdateService:(handleProducedLocationUpdate) " + ((Looper.myLooper() == Looper.getMainLooper()) ? "MainThread" : "BackgroundThread"));
    }

    @Override
    public void handleLocationSettingException(Exception e) {
        if (e instanceof ResolvableApiException){
            // Location settings are not satisfied, but this can be fixed
            // by showing the user a dialog.
            /*try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(this, LocationStreamProperties.REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                // Ignore the error.
            }*/
            if (GeoTracker.shared().isDebuggingOn()) Log.d(TAG, "handleLocationSettingException: " + e.getMessage());
        }
    }

    @Override
    public void handleProducerConnectionError(Throwable throwable) {
        Log.d(TAG, "handleProducerConnectionError: " + throwable.getMessage());
    }

    private void stopLocationUpdate(boolean shouldSendStop) {
        getLocationProducer().stop(shouldSendStop);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}
