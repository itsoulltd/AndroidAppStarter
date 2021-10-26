package lab.infoworks.libui.activities.decorator;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.Nullable;

import lab.infoworks.libshared.controllers.NetworkActivityController;
import lab.infoworks.libui.activities.ActivityDecorator;
import lab.infoworks.libui.activities.BaseActivity;

public class NetworkDecorator extends ActivityDecorator implements NetworkActivityController.NetworkMonitoring{

    public static final int RC_NETWORK = 902;

    public NetworkDecorator(BaseActivity ref) {
        super(ref);
    }

    @Override
    public void onStart() {
        getMonitoringController().monitorNetwork(getActivity(), true);
        getMonitoringController().checkNetworkAvailability();
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getMonitoringController().disableNetworkConnectivity();
        }
    }

    private NetworkActivityController monitoringController;

    public NetworkActivityController getMonitoringController() {
        if (monitoringController == null) {
            monitoringController = new NetworkActivityController();
            monitoringController.setNetworkMonitoring(this);
        }
        return monitoringController;
    }

    @Override
    public void onBottomSheetButtonClick(int refCode) {
        if(refCode == RC_NETWORK) getActivity().startActivityForResult(new Intent(Settings.ACTION_SETTINGS), RC_NETWORK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RC_NETWORK) {
            getMonitoringController().checkNetworkAvailability();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //TODO
    }

    @Override
    public void openNetworkErrorBottomSheet(String message) {
        getActivity().showBottomSheet("network-bottom-fragment", RC_NETWORK
                , "Internet Connection not available. Please go to settings and choose a active network!");
    }

    @Override
    public void closeNetworkErrorBottomSheet() {
        getActivity().closeBottomSheet("network-bottom-fragment");
    }
}
