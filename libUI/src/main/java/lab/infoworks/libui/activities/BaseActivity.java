package lab.infoworks.libui.activities;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.libshared.notifications.NotificationType;
import lab.infoworks.libui.activities.decorator.ActivityDecorator;
import lab.infoworks.libui.alert.AlertSheetFragment;

public abstract class BaseActivity extends AppCompatActivity implements AlertSheetFragment.OnFragmentInteractionListener{

    protected String TAG = "ACTIVITY_STATE " + this.getClass().getSimpleName();

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //subscribe to notifications listener in onCreate of activity
        NotificationCenter.addObserver(this, NotificationType.FORCE_SIGN_OUT.name(), (context, intent) -> {
            //TODO:
            //Global Logout From Here!
        });
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getDecorators() != null) {
            for (ActivityDecorator decorator : getDecorators()) {
                decorator.onStart();
            }
        }
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getDecorators() != null) {
            for (ActivityDecorator decorator : getDecorators()) {
                decorator.onResume();
            }
        }
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getDecorators() != null) {
            for (ActivityDecorator decorator : getDecorators()) {
                decorator.onPause();
            }
        }
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (getDecorators() != null) {
            for (ActivityDecorator decorator : getDecorators()) {
                decorator.onRestart();
            }
        }
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getDecorators() != null) {
            for (ActivityDecorator decorator : getDecorators()) {
                decorator.onStop();
            }
        }
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unsubscribe from notifications listener
        NotificationCenter.removeObserver(this, NotificationType.FORCE_SIGN_OUT.name());
        if (getDecorators() != null) {
            for (ActivityDecorator decorator : getDecorators()) {
                decorator.onDestroy();
            }
        }
        Log.d(TAG, "onDestroy");
    }

    private Set<ActivityDecorator> _decorators;
    protected Collection<ActivityDecorator> getDecorators() {
        return _decorators;
    }

    public void setDecorator(ActivityDecorator decorator) {
        if (_decorators == null){
            _decorators = new HashSet<>();
        }
        _decorators.add(decorator);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getDecorators() != null) {
            for (ActivityDecorator decorator :
                    getDecorators()) {
                decorator.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (getDecorators() != null) {
            for (ActivityDecorator decorator :
                    getDecorators()) {
                decorator.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    ///////////////////////////////////////////////

    protected AlertDialog.Builder createAlertBuilder() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        return builder;
    }

    protected void copyToClipBoard(String data) {
        ClipboardManager clipboardManager = (ClipboardManager) this.getSystemService(this.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied to clipboard", data);
        clipboardManager.setPrimaryClip(clipData);
    }

    protected void gotoPlayStore(){
        final String appPackageName = getPackageName();
        try {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    public void showBottomSheet(String tag, int refCode, String message) {
        //Log.d(TAG, "showBottomSheet: " + tag);
        if (getSupportFragmentManager().findFragmentByTag(tag) != null)
            return;
        AlertSheetFragment bottomSheetFragment = AlertSheetFragment.newInstance(refCode, message);
        bottomSheetFragment.setCancelable(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(bottomSheetFragment, tag);
        ft.commitAllowingStateLoss();
    }

    public void closeBottomSheet(String tag) {
        //Log.d(TAG, "closeBottomSheet: " + tag);
        AlertSheetFragment bottomSheetFragment = (AlertSheetFragment) getSupportFragmentManager()
                .findFragmentByTag(tag);
        if ( bottomSheetFragment != null) {
            bottomSheetFragment.dismiss();
        }
    }

    @Override
    public void onBottomSheetButtonClick(int refCode) {
        if (getDecorators() != null) {
            for (ActivityDecorator decorator : getDecorators()) {
                decorator.onBottomSheetButtonClick(refCode);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////

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
