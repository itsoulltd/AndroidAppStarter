package lab.infoworks.libshared.domain.datasource;

import android.content.Context;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.domain.shared.AssetManager;

public class SampleData {

    public static Rider[] getRiders(){
        return new Rider[]{
                new Rider("Rider-1", "#geo-hash-001").setEmail("rider-1@gmail.com")
                , new Rider("Rider-2", "#geo-hash-002").setEmail("rider-2@gmail.com")
                , new Rider("Rider-3", "#geo-hash-003").setEmail("rider-3@gmail.com")
        };
    }

    public static Rider[] getRidersFrom(Context context){
        Map<String, Object> data = AssetManager.readJsonObject(context, "data/rider-mock-data.json").get(0);
        List<Map<String, Object>> findRiders = (List<Map<String, Object>>) data.get("findRiders");
        List<Rider> res = findRiders
                .stream()
                .map(riderVal -> {
                    Rider r = new Rider();
                    r.unmarshallingFromMap(riderVal, true);
                    return r;
                }).collect(Collectors.toList());
        return res.toArray(new Rider[0]);
    }

}
