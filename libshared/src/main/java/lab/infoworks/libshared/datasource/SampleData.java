package lab.infoworks.libshared.datasource;

import lab.infoworks.libshared.model.Rider;

public class SampleData {

    public static Rider[] getRiders(){
        return new Rider[]{
                new Rider("Rider-1", "#geo-hash-001").setEmail("rider-1@gmail.com")
                , new Rider("Rider-2", "#geo-hash-002").setEmail("rider-2@gmail.com")
                , new Rider("Rider-3", "#geo-hash-003").setEmail("rider-3@gmail.com")
        };
    }

}
