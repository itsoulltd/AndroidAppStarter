package lab.infoworks.libshared.ui.BaseActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.libshared.notifications.NotificationType;
import lab.infoworks.libshared.ui.alert.AlertSheetFragment;

public abstract class BaseActivity extends AppCompatActivity implements AlertSheetFragment.OnFragmentInteractionListener{

    protected String TAG = "ACTIVITY_STATE " + this.getClass().getSimpleName();

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate");
        //subscribe to notifications listener in onCreate of activity
        NotificationCenter.addObserver(this, NotificationType.FORCE_SIGN_OUT.name(), (context, intent) -> {
            //TODO:
            //Global Logout From Here!
        });
    }

    //FIXME: Refactoring to all child activity:
    /*protected abstract void initViews();
    protected abstract void loadContents();
    protected abstract void initListeners();*/

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy");
        // Don't forget to unsubscribe from notifications listener
        NotificationCenter.removeObserver(this, NotificationType.FORCE_SIGN_OUT.name());
    }

    ///////////////////////////////////////////////

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
        final String appPackageName = getPackageName();
        try {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    //////////////////////////////////////////////

    protected void showBottomSheet(String tag, int refCode, String message) {
        //Log.d(TAG, "showBottomSheet: " + tag);
        if (getSupportFragmentManager().findFragmentByTag(tag) != null)
            return;
        AlertSheetFragment bottomSheetFragment = AlertSheetFragment.newInstance(refCode
                , message);
        bottomSheetFragment.setCancelable(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(bottomSheetFragment, tag);
        ft.commitAllowingStateLoss();
    }

    protected void closeBottomSheet(String tag) {
        //Log.d(TAG, "closeBottomSheet: " + tag);
        AlertSheetFragment bottomSheetFragment = (AlertSheetFragment) getSupportFragmentManager()
                .findFragmentByTag(tag);
        if ( bottomSheetFragment != null) {
            bottomSheetFragment.dismiss();
        }
    }

    //////////////////////////////////////////////

    private static final Integer PLAY_SERVICES_RESOLUTION_REQUEST = 2404;

    protected boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

}
