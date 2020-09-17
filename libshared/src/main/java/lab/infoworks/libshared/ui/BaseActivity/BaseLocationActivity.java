package lab.infoworks.libshared.ui.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;

import lab.infoworks.libshared.controllers.LocationPermissionController;
import lab.infoworks.libshared.controllers.LocationStreamController;
import lab.infoworks.libshared.controllers.models.LocationStreamProperties;

public abstract class BaseLocationActivity extends KeepScreenOnActivity
        implements LocationPermissionController.LocationPermissionsObserver,
        LocationPermissionController.LocationProviderObserver{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getLocationController().askForPermissionAtRuntime(this);
        getLocationPermissionController().checkLocationProviderAvailability(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            getLocationPermissionController().close();
        } catch (Exception e) {}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLocationPermissionController().handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private LocationPermissionController locationController;

    public LocationPermissionController getLocationPermissionController() {
        if (locationController == null) locationController = new LocationPermissionController();
        return locationController;
    }

    /**
     *
     */

    @Override
    public void onBottomSheetButtonClick(int code) {
        super.onBottomSheetButtonClick(code);
        if(code == RC_LOCATION) startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), RC_LOCATION);
    }

    /**
     *
     *
     */

    @Override
    public void locationPermissionsDidSuccessful(Object... observant) {
        closeBottomSheet("location-bottom-fragment");
    }

    @Override
    public void locationPermissionsDidFailed(String error) {
        if (error == null || error.isEmpty()){
            error = "Location access permission not available. Please go to settings and update location access permission!";
        }
        showBottomSheet("location-bottom-fragment", RC_LOCATION, error);
    }

    @Override
    public void observeLocationProviderStatus(boolean active, String[] providers) {
        if (active){
            closeBottomSheet("location-bottom-fragment");
        }else {
            showBottomSheet("location-bottom-fragment", RC_LOCATION,"Device Location Service is disabled! \n \n");
        }
    }

    private LocationStreamProperties streamProperties;
    private LocationStreamController streamController;

    public LocationStreamController getLocationStreamController() {
        if (streamController == null){
            streamProperties = new LocationStreamProperties.Builder()
                    .addUpdateInterval(6000)
                    .addSmallestDisplacement(10)
                    .build();
            streamController = new LocationStreamController(this, streamProperties);
        }
        return streamController;
    }
}
