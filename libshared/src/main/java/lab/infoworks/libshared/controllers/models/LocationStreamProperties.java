package lab.infoworks.libshared.controllers.models;

import com.google.android.gms.location.LocationRequest;

public class LocationStreamProperties {

    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    private int updateInterval = 2500;
    private int fastestInterval = 1000;
    private int smallestDisplacement = 0;
    private int requestPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getFastestInterval() {
        return fastestInterval;
    }

    public void setFastestInterval(int fastestInterval) {
        this.fastestInterval = fastestInterval;
    }

    public int getSmallestDisplacement() {
        return smallestDisplacement;
    }

    public void setSmallestDisplacement(int smallestDisplacement) {
        this.smallestDisplacement = smallestDisplacement;
    }

    public int getRequestPriority() {
        return requestPriority;
    }

    public void setRequestPriority(int requestPriority) {
        this.requestPriority = requestPriority;
    }

    public static class Builder{

        private int updateInterval = 2500;
        private int fastestInterval = 1000;
        private int smallestDisplacement = 0;
        private int requestPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;

        public Builder() {}

        public LocationStreamProperties build(){
            LocationStreamProperties properties = new LocationStreamProperties();
            properties.setFastestInterval(fastestInterval);
            properties.setUpdateInterval(updateInterval);
            properties.setSmallestDisplacement(smallestDisplacement);
            properties.setRequestPriority(requestPriority);
            return properties;
        }

        public Builder addUpdateInterval(int updateInterval){
            this.updateInterval = updateInterval;
            return this;
        }

        public Builder addFastestInterval(int fastestInterval){
            this.fastestInterval = fastestInterval;
            return this;
        }

        public Builder addRequestPriority(int priority){
            this.requestPriority = priority;
            return this;
        }

        public Builder addSmallestDisplacement(int displacement){
            this.smallestDisplacement = displacement;
            return this;
        }

    }
}
