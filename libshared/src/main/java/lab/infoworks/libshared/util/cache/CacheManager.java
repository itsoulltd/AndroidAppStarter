package lab.infoworks.libshared.util.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.sql.entity.Entity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import lab.infoworks.libshared.domain.datasource.CMDataSource;
import lab.infoworks.libshared.util.AppStorage;

public class CacheManager<E extends Entity> {

    private String keyPrefix;

    public CacheManager(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    protected boolean isValidJson(String json){
        if (json == null || json.isEmpty()) return false;
        if (json.startsWith("[") || json.startsWith("{")) return true;
        return false;
    }

    public void restore(AppStorage manager, TypeReference type){
        if (manager == null) return;
        String dhs = manager.stringValue(keyPrefix);
        if (isValidJson(dhs)){
            try {
                List<E> houses = (List<E>) Message.unmarshal(type, dhs);
                clear().save(houses);
            } catch (IOException e) {}
        }
    }

    public CacheManager save(AppStorage manager){
        if (manager == null) return this;
        List<E> dhs = fetch(0, 0);
        if (dhs != null && dhs.size() > 0){
            try {
                String val = Message.marshal(dhs);
                manager.put(keyPrefix, val);
            } catch (IOException e) {}
        }
        return this;
    }

    public CacheManager clearStorage(AppStorage manager){
        if (manager == null) return this;
        manager.put(keyPrefix, "[]");
        return this;
    }

    ////////////////////////////////

    private CMDataSource<Integer, E> cacheSources;

    private CMDataSource<Integer, E> getCache() {
        if (cacheSources == null){
            cacheSources = new CMDataSource<>();
        }
        return cacheSources;
    }

    public List<E> fetch(int offset, int page) {
        if (page > getCache().size() || page <= 0) page = getCache().size();
        return Arrays.asList(getCache().readSync(offset, page));
    }

    public CacheManager clear(){
        if (getCache().size() > 0){
            getCache().clear();
        }
        return this;
    }

    public CacheManager save(List<E> eList){
        for (E dh: eList) {
            getCache().add(dh);
        }
        return this;
    }

    /////////////////////////////////

}
