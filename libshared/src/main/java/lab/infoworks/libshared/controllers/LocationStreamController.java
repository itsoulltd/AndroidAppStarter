package lab.infoworks.libshared.controllers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.infoworks.lab.rest.models.events.EventType;

import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.NotNull;

import lab.infoworks.libshared.controllers.models.LocationStreamProperties;

public class LocationStreamController {

    public interface LocationStreamListener extends LocationPermissionController.LocationProviderObserver{
        void locationStream(Location location, EventType eventType);
    }

    private static String TAG = LocationStreamController.class.getSimpleName();

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient locationProvider;
    private SettingsClient locationSettingsClient;
    private LocationStreamProperties _properties;
    private Location lastSavedLocation = null;
    private LocationStreamListener _listener;

    public LocationStreamController(Context context, LocationStreamProperties properties) {
        //
        if (LocationStreamListener.class.isAssignableFrom(context.getClass())){
            _listener = (LocationStreamListener) context;
        }
        locationSettingsClient = LocationServices.getSettingsClient(context);
        locationProvider = LocationServices.getFusedLocationProviderClient(context);
        if(properties != null) _properties = properties;
        else _properties = new LocationStreamProperties();
    }

    @SuppressLint("MissingPermission")
    public void observeLastLocation(Activity context, Handler handler){
        if (locationProvider != null && handler != null){
            try {
                locationProvider.getLastLocation().addOnSuccessListener(context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Message msg = new Message();
                        msg.obj = location;
                        handler.handleMessage(msg);
                    }
                });
            } catch (SecurityException e) {
                Log.d(TAG, "observeLastLocation: " + e.getMessage());
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void observeLastLocation(Handler handler){
        if (locationProvider != null && handler != null){
            try {
                locationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Message msg = new Message();
                        msg.obj = location;
                        handler.handleMessage(msg);
                    }
                });
            } catch (SecurityException e) {
                Log.d(TAG, "observeLastLocation: " + e.getMessage());
            }
        }
    }

    private boolean _isStarted;
    public boolean isStarted(){return _isStarted;}

    private ReentrantLock _lock;
    protected ReentrantLock getLock(){
        if (_lock == null) _lock = new ReentrantLock();
        return _lock;
    }

    /**
     * Must Call from MainThread or MainLooper
     * @param listener
     * @param handler
     */
    public void startListening(LocationStreamListener listener, Handler handler) {
        //if already running:
        if (_isStarted) return;
        //
        getLock().lock();
        try {
            if (listener != null) _listener = listener;
            if (locationCallback == null) locationCallback = createLocationCallback();
            initLocationRequest(new Handler(Looper.myLooper()){
                @SuppressLint("MissingPermission")
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Message returnMsg = new Message();
                    if (msg.obj instanceof LocationSettingsResponse) {
                        if (_isStarted == false) {
                            // new Google API SDK v11 uses getFusedLocationProviderClient(this) and from api 11 no need of googleapiclient
                            getLock().lock();
                            try {
                                locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                                _isStarted = true;
                            } catch (SecurityException e) {
                                Log.d(TAG, "startListening: "+ e.getLocalizedMessage());
                            }finally {
                                getLock().unlock();
                            }
                        }
                        returnMsg.obj = Boolean.valueOf(_isStarted);
                    }else if (msg.obj instanceof ResolvableApiException){
                        //So we need to throw this upper layer.
                        returnMsg.obj = msg.obj;
                    }else {
                        Log.d(TAG, "onFailure: "+ msg.obj.toString());
                        returnMsg.obj = Boolean.valueOf(_isStarted);
                    }
                    if (handler != null){
                        handler.handleMessage(returnMsg);
                    }
                }
            });
        } finally {
            getLock().unlock();
        }
    }

    public void stopListening(boolean isLastUpdate){
        getLock().lock();
        try {
            if(locationCallback != null) locationProvider.removeLocationUpdates(locationCallback);
            _isStarted = false;
        } finally {
            getLock().unlock();
        }
        if (isLastUpdate && _listener != null) {
            _listener.locationStream(lastSavedLocation, EventType.STOP);
        }
        _listener = null;
        lastSavedLocation = null;
    }

    private void initLocationRequest(Handler handler) {
        if (locationRequest == null) {
            // Create the location request to start receiving updates
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(_properties.getRequestPriority());
            locationRequest.setInterval(_properties.getUpdateInterval());
            locationRequest.setFastestInterval(_properties.getRequestPriority());
            /**
             * Setting this in meters will cause app to not be notified about location change,
             * if it was smaller than the given value.
             */
            //FIXME:
            if (_properties.getSmallestDisplacement() > 0){
                locationRequest.setSmallestDisplacement(_properties.getSmallestDisplacement());
            }
            // Create LocationSettingsRequest object using location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(locationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();

            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            Task<LocationSettingsResponse> task = locationSettingsClient.checkLocationSettings(locationSettingsRequest);
            task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    if (handler != null) {
                        Message message = new Message();
                        message.obj = locationSettingsResponse;
                        handler.handleMessage(message);
                    }
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NotNull Exception e) {
                    if (handler != null){
                        Message message = new Message();
                        if (e != null && e instanceof ResolvableApiException) {
                            message.obj = e;
                        }else{
                            message.obj = (e != null) ? e.getMessage() : "Unknown error on Location Setting Response failure!";
                        }
                        handler.handleMessage(message);
                    }
                }
            });
        }
        //
    }

    private LocationCallback createLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (_listener != null) {
                    Location currentLocation = locationResult.getLastLocation();
                    Log.d(TAG, "createLocationCallback: " + currentLocation.getLongitude() + " :: " + currentLocation.getLongitude());
                    EventType eventType = (lastSavedLocation != null) ? EventType.UPDATE : EventType.START;
                    _listener.locationStream((eventType == EventType.START) ? currentLocation : lastSavedLocation
                            , eventType);
                    lastSavedLocation = currentLocation;
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (_listener != null){
                    Log.d(TAG, "onLocationAvailability: Location Service Available: " + locationAvailability.isLocationAvailable());
                    _listener.observeLocationProviderStatus(locationAvailability.isLocationAvailable(), new String[0]);
                }
            }
        };
    }

}
