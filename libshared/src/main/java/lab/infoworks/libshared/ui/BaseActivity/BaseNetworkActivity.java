package lab.infoworks.libshared.ui.BaseActivity;

import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.Nullable;

import lab.infoworks.libshared.controllers.NetworkActivityController;

public abstract class BaseNetworkActivity extends BaseActivity
        implements NetworkActivityController.NetworkMonitoring {

    protected static final int RC_NETWORK = 902;
    protected static final int RC_LOCATION = 903;

    @Override
    protected void onStart() {
        super.onStart();
        getMonitoringController().monitorNetwork(this, true);
        getMonitoringController().checkNetworkAvailability();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getMonitoringController().disableNetworkConnectivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //
        if(requestCode == RC_NETWORK) {
            getMonitoringController().checkNetworkAvailability();
        }
    }

    private NetworkActivityController monitoringController;

    public NetworkActivityController getMonitoringController() {
        if (monitoringController == null) monitoringController = new NetworkActivityController();
        return monitoringController;
    }

    @Override
    public void onBottomSheetButtonClick(int code) {
        if(code == RC_NETWORK) startActivityForResult(new Intent(Settings.ACTION_SETTINGS), RC_NETWORK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void openNetworkErrorBottomSheet(String message) {
        showBottomSheet("network-bottom-fragment", RC_NETWORK
                , "Internet Connection not available. Please go to settings and choose a active network!");
    }

    @Override
    public void closeNetworkErrorBottomSheet() {
        closeBottomSheet("network-bottom-fragment");
    }

}
