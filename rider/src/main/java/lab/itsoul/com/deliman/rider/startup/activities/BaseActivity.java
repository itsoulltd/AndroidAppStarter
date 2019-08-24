package lab.itsoul.com.deliman.rider.startup.activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.itsoul.lab.android.controllers.NetworkActivityController;
import com.itsoul.lab.domain.base.Produce;

import lab.itsoul.com.deliman.rider.startup.R;
import lab.itsoul.com.deliman.rider.startup.controllers.AppVersionController;

public class BaseActivity extends AppCompatActivity {

    //FIXME: Change the way of making link: e.g. "https://itsoultrackme.page.link?uuid=" + uuid
    //public static final String FIREBASE_DYNAMIC_LINK = "https://itsoultrackme.page.link/welcome/";

    private NetworkActivityController monitoringController;
    private AppVersionController versionController;

    public NetworkActivityController getMonitoringController() {
        if (monitoringController == null) monitoringController = new NetworkActivityController();
        return monitoringController;
    }

    public AppVersionController getVersionController() {
        if (versionController == null) versionController = new AppVersionController();
        return versionController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @NonNull
    public AlertDialog.Builder createAlertBuilder() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        return builder;
    }

    public void copyToClipBoard(String data) {
        ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(this.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied to clipboard", data);
        clipboardManager.setPrimaryClip(clipData);
    }

    public void gotoPlayStore(){
        final String appPackageName = this.getPackageName();
        try {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    protected void handleUnAuthorizedAccess(Produce produce){
        //TODO:
    }
}
