package lab.infoworks.libshared.util.cachemanager;

import com.it.soul.lab.data.simple.SimpleDataSource;

import java.util.ArrayList;
import java.util.List;

public class CMDataSource<Key, Value> extends SimpleDataSource<Key, Value> {
    @Override
    public Value[] readSync(int offset, int pageSize) {
        int size = size();
        int maxItemCount = Math.abs(offset) + Math.abs(pageSize);
        if (maxItemCount <= size) {
            List<Value> values = new ArrayList<>(getInMemoryStorage().values());
            List<Value> items = values.subList(Math.abs(offset), maxItemCount);
            return (Value[]) items.toArray();
        } else {
            return (Value[]) new Object[0];
        }
    }
}
