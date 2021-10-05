package lab.infoworks.starter.ui.activities.app;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lab.infoworks.libshared.domain.remote.DownloadTracker;
import lab.infoworks.libshared.domain.shared.AssetManager;
import lab.infoworks.libshared.notifications.SystemNotificationTray;
import lab.infoworks.libui.activities.BaseNetworkActivity;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.app.StarterApp;


public class AppActivity extends BaseNetworkActivity {

    private static final String TAG = AppActivity.class.getName();

    @BindView(R.id.statusTextView)
    TextView statusTextView;

    @BindView(R.id.statusButton)
    TextView verifyButton;

    private AppViewModel appViewModel;
    private SystemNotificationTray notificationTray;
    private long dRef = 0l;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider);
        ButterKnife.bind(this);

        notificationTray = new SystemNotificationTray(this);

        appViewModel = new AppViewModel(getApplication());
        appViewModel.getUserStatusObservable().observe(this, verificationResult -> {
            Log.d(TAG, "===> result: " + verificationResult.isVerified());
            statusTextView.setText("Rider is verified.... :) ");
            verifyButton.setEnabled(false);
        });
        appViewModel.getRiderObservable().observe(this, riders -> {
            Log.d(TAG, "===> number of riders found: " + riders.size());
            statusTextView.setText("number of riders found: " + riders.size());
            verifyButton.setEnabled(true);
            notifyTray();
        });
    }

    private void notifyTray(){
        //Notification:
        String title = getString(R.string.hello_title);
        String message = getString(R.string.hello_message);
        String ticker = getString(R.string.app_name);
        int icon = R.mipmap.ic_launcher_round;
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //display msg on notificationTry
        notificationTray.notify(1, title, message, ticker, icon, sound);
    }

    @OnClick(R.id.statusButton)
    public void verifyRider() {
        appViewModel.verifyUser();
    }

    @OnClick(R.id.moveToRidersButton)
    public void findRiders() {
        appViewModel.findRiders();
    }

    @OnClick(R.id.startDownloadButton)
    public void startDownload(){
        //DownloadTracker test
        String link = "https://upload.wikimedia.org/wikipedia/commons/c/c6/A_modern_Cricket_bat_%28back_view%29.jpg";
        dRef = new DownloadTracker.Builder(getApplicationContext(), link)
                .setDestinationInExternalFilesDir("myImg.jpg", Environment.DIRECTORY_DOWNLOADS)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setTitle("myImg download")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .enqueue((ios) -> {
                    //Now do whatever you want:
                    try {
                        Bitmap img = AssetManager.readAsImage(ios, 0);
                        System.out.println("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("");
                });
        //
    }

    @OnClick(R.id.stopDownloadButton)
    public void stopDownload(){
        String status = new DownloadTracker.Builder(this).cancel(dRef).getStatus();
        statusTextView.setText(status);
    }

    @OnClick(R.id.statusDownloadButton)
    public void updateStatusDownload(){
        /*new DownloadTracker
                .Builder(this)
                .checkStatus(dRef, StarterApp.executor, (status) -> {
                    runOnUiThread(() -> statusTextView.setText(status.getStatus()));
                });*/
        //Other sample:
        DownloadTracker.TrackItemStatus status = new DownloadTracker.Builder(this).checkStatus(dRef);
        statusTextView.setText(status.getStatus());
    }

    @OnClick(R.id.showDownloadsButton)
    public void showDownloads(){
        DownloadTracker.viewOnGoingDownloads(this);
    }
}
