package lab.infoworks.libshared.domain.shared;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.sql.entity.Entity;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lab.infoworks.libshared.domain.datasource.CMDataSource;

public class CacheManager<E extends Entity> {

    private String keyPrefix;
    private final int maxSize;

    public CacheManager(String keyPrefix) {
        this(keyPrefix, 100);
    }

    public CacheManager(String keyPrefix, int maxSize) {
        this.keyPrefix = keyPrefix;
        this.maxSize = maxSize;
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

    /////////////////////////////////////////////////////////////////////////////

    private CacheDataSource<E> cacheSources;

    private CacheDataSource<E> getCache() {
        if (cacheSources == null){
            cacheSources = new CacheDataSource<>(maxSize);
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
     * @param <K, V>
     */
    private static class LRUCache<K, V>  extends LinkedHashMap<K, V> {

        private static final long serialVersionUID = 1L;
        private final int lruSize;

        public LRUCache(int initSize, int maxSize) {
            super((initSize > maxSize ? Math.round(maxSize/2) : initSize)
                    , 0.75f
                    , true);
            this.lruSize = maxSize;
        }

        public LRUCache(int maxSize) {
            this(Math.round(maxSize/2), maxSize);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > lruSize;
        }

    }

    /**
     * Eviction Policy: Least Recently Used.
     * Concurrency: Multiple Thread Should Perform operation on the container.
     * Following has no impact on eviction or LRU-Policy based Key-Sorting:
     * -fetch(...) OR -readSync(...) OR -readAsync(...)
     * Cache eviction and LRU-Policy based Key-Sorting applied on following:
     * -add(...) OR -put(...)
     * -read(...)
     * -replace(...) AND -remove(...) OR -delete(...)
     * @param <E>
     */
    private static class CacheDataSource<E> extends CMDataSource<String, E> {

        private final LRUCache<String, E> cacheStorage;

        public CacheDataSource(int maxSize) {
            this.cacheStorage = new LRUCache<>(maxSize);
        }

        @Override
        protected Map<String, E> getInMemoryStorage() {
            return cacheStorage;
        }

        @Override
        public void clear(){
            if (size() > 0){
                cacheStorage.clear();
            }
        }

        @Override
        public void put(String key, E e) {
            synchronized (cacheStorage) {
                cacheStorage.put(key, e);
            }
        }

        @Override
        public E remove(String key) {
            synchronized (cacheStorage) {
                return cacheStorage.remove(key);
            }
        }

        @Override
        public void add(E e) {
            put(String.valueOf(e.hashCode()), e);
        }

        public DataSource<String, E> add(E...items){
            for (E dh: items) add(dh);
            return this;
        }

        @Override
        public void delete(E e) {
            remove(String.valueOf(e.hashCode()));
        }

        public DataSource<String, E> delete(E...items){
            for (E dh: items) delete(dh);
            return this;
        }

        @Override
        public boolean contains(E e) {
            return containsKey(String.valueOf(e.hashCode()));
        }

    }

}
