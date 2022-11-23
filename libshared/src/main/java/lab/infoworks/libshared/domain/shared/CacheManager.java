package lab.infoworks.libshared.domain.shared;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.sql.entity.Entity;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lab.infoworks.libshared.domain.datasource.CMDataSource;

public class CacheManager<E extends Entity> {

    private String keyPrefix;

    public CacheManager(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public void restore(AppStorage manager, TypeReference type){
        if (manager == null) return;
        String dhs = manager.stringValue(keyPrefix);
        if (Message.isValidJson(dhs)){
            try {
                List<E> houses = (List<E>) Message.unmarshal(type, dhs);
                clear().add(houses);
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

    private CacheDataSource<E> cacheSources;

    private CacheDataSource<E> getCache() {
        if (cacheSources == null){
            cacheSources = new CacheDataSource<>();
        }
        return cacheSources;
    }

    public List<E> fetch(int offset, int page) {
        if (page > getCache().size() || page <= 0) page = getCache().size();
        return getCache().fetch(offset, page);
    }

    public CacheManager clear(){
        if (getCache().size() > 0){
            getCache().clear();
        }
        return this;
    }

    public CacheManager add(List<E> eList){
        for (E dh: eList) {
            getCache().add(dh);
        }
        return this;
    }

    /////////////////////////////////

    /**
     * Eviction Policy: Least Frequently Used.
     * Concurrency: Multiple Thread Should Perform operation on the container.
     * @param <E>
     */
    private static class CacheDataSource<E> extends CMDataSource<Integer, E> {

        private final LinkedHashMap<Integer, E> inMem = new LinkedHashMap(10, 0.75f, true);

        @Override
        protected Map<Integer, E> getInMemoryStorage() {
            return inMem;
        }

        //TODO:
    }

}
