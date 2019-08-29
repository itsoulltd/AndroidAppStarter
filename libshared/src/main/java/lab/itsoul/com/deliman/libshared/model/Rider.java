package lab.itsoul.com.deliman.libshared.model;

import com.it.soul.lab.sql.entity.Entity;
import com.itsoul.lab.domain.base.Produce;

public class Rider extends Produce {
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
