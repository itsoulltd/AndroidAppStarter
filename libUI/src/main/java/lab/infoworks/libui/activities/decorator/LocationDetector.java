package lab.infoworks.libui.activities.decorator;

import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.Nullable;

import lab.infoworks.libshared.controllers.LocationPermissionController;
import lab.infoworks.libshared.controllers.LocationStreamController;
import lab.infoworks.libshared.controllers.models.LocationStreamProperties;
import lab.infoworks.libui.activities.BaseActivity;

public class LocationDetector extends ActivityDecorator implements LocationPermissionController.LocationPermissionsObserver,
        LocationPermissionController.LocationProviderObserver {

    public static final int RC_LOCATION = 903;

    public LocationDetector(BaseActivity ref) {
        super(ref);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {
        getLocationPermissionController().checkLocationProviderAvailability(getActivity());
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
        try {
            getLocationPermissionController().close();
        } catch (Exception e) {}
    }

    @Override
    public void onBottomSheetButtonClick(int refCode) {
        if(refCode == RC_LOCATION) getActivity().startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), RC_LOCATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //TODO:
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        getLocationPermissionController().handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private LocationPermissionController locationController;

    public LocationPermissionController getLocationPermissionController() {
        if (locationController == null) {
            locationController = new LocationPermissionController();
            locationController.setLocationPermissionObserver(this);
            locationController.setLocationProviderObserver(this);
        }
        return locationController;
    }

    @Override
    public void locationPermissionsDidSuccessful(Object... observant) {
        getActivity().closeBottomSheet("location-bottom-fragment");
    }

    @Override
    public void locationPermissionsDidFailed(String error) {
        if (error == null || error.isEmpty()){
            error = "Location access permission not available. Please go to settings and update location access permission!";
        }
        getActivity().showBottomSheet("location-bottom-fragment", RC_LOCATION, error);
    }

    @Override
    public void observeLocationProviderStatus(boolean active, String[] providers) {
        if (active){
            getActivity().closeBottomSheet("location-bottom-fragment");
        }else {
            getActivity().showBottomSheet("location-bottom-fragment", RC_LOCATION,"Device Location Service is disabled! \n \n");
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
            streamController = new LocationStreamController(getActivity(), streamProperties);
        }
        return streamController;
    }
}
