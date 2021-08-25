package lab.infoworks.libshared.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

public class SystemNotificationTray extends ContextWrapper {

    private NotificationManager mManager;
    public static final String CHANNEL_ID = SystemNotificationTray.class.getName();
    public static final String CHANNEL_NAME = SystemNotificationTray.class.getSimpleName();

    public SystemNotificationTray(Context base) {
        super(base);
        createChannel();
    }

    protected void createChannel() {
        // create channel
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID
                    , CHANNEL_NAME
                    , NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            channel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            channel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            channel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(channel);
        }
    }

    protected NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public void notify(int notificationId, String title, String message){
        notify(notificationId, title, message, null, 0, null);
    }

    public void notify(int notificationId, String title, String message, String ticker, int smallIcon, Uri sound){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setLights(Color.RED, 3000, 3000)
                .setVibrate(new long[] { 1000, 1000 })
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        if (ticker != null) notificationBuilder.setTicker(ticker);
        if (smallIcon > 0) notificationBuilder.setSmallIcon(smallIcon);
        if (sound != null) notificationBuilder.setSound(sound);
        //
        Notification notification = notificationBuilder.build();
        notify(notificationId, notification);
    }

    public void notify(int notificationId, Notification notification){
        getManager().notify(notificationId, notification);
    }

}