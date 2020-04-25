package lab.infoworks.libshared.datasource;

import android.os.Handler;

import com.it.soul.lab.data.base.DataStorage;
import com.it.soul.lab.data.simple.SimpleDataSource;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.model.Rider;

public class RiderDataSource extends SimpleDataSource<Integer, Rider> implements DataStorage {

    public RiderDataSource(){
        retrieve();
    }

    @Override
    public void readAsynch(int offset, int pageSize, Consumer<Rider[]> consumer) {
        if (consumer != null) {
            new Handler().postDelayed(() -> {
                        //TODO:
                        List<Rider> items = Arrays.asList(readSynch(offset, pageSize));
                        consumer.accept(items.toArray(new Rider[0]));
                    }
                    , 1000);
        }
    }

    @Override
    public void save(boolean asynch) {
        //TODO: Save Data using Preferred Persistence Technology:
    }

    @Override
    public boolean retrieve() {
        //TODO: Retrieve Data using Preferred Persistence Technology:
        if (getInMemoryStorage().size() <= 0){
            int index = 0;
            for (Rider rider : getDummyData()) {
                getInMemoryStorage().put(index++, rider);
            }
        }
        //
        return true;
    }

    private Rider[] getDummyData(){
        return new Rider[]{
                new Rider("Rider-1", "#geo-hash-001")
                , new Rider("Rider-2", "#geo-hash-002")
        };
    }

    @Override
    public boolean delete() {
        //TODO: Delete Data using Preferred Persistence Technology:
        return false;
    }

}
