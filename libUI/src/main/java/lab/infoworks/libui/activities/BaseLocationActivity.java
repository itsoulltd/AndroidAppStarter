package lab.infoworks.libui.activities;

import android.content.Intent;

import androidx.annotation.Nullable;

import lab.infoworks.libshared.controllers.LocationStreamController;
import lab.infoworks.libshared.controllers.models.LocationStreamProperties;
import lab.infoworks.libui.activities.decorator.LocationDetector;

public abstract class BaseLocationActivity extends BaseActivity {

    @Override
    protected void onStart() {
        super.onStart();
        new LocationDetector(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
