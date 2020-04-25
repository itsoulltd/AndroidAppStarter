package lab.infoworks.libshared.model;

import com.infoworks.lab.rest.models.Response;

public class Rider extends Response {
    private String name;
    private String geoHash;

    public Rider() {
    }

    public Rider(String name, String geoHash) {
        this.name = name;
        this.geoHash = geoHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }
}
