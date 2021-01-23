package lab.infoworks.libshared.domain.datasource;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.it.soul.lab.data.base.DataStorage;
import com.it.soul.lab.data.simple.SimpleDataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.datasource.db.AppDB;
import lab.infoworks.libshared.domain.datasource.db.dao.RiderDAO;
import lab.infoworks.libshared.domain.model.Rider;

public class RiderDataSource extends SimpleDataSource<Integer, Rider> implements DataStorage {

    private AppDB db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public RiderDataSource(Context context){
        this.db = AppDB.getInstance(context);
        retrieve();
    }

    @Override @RequiresApi(Build.VERSION_CODES.N)
    public void readAsync(int offset, int pageSize, Consumer<Rider[]> consumer) {
        if (consumer != null) {
            //TODO:
            List<Rider> items = Arrays.asList(readSync(offset, pageSize));
            consumer.accept(items.toArray(new Rider[0]));
        }
    }

    @Override
    public void save(boolean async) {
        //TODO: Save Data using Preferred Persistence Technology:
        if (async){
            executor.submit(() -> {
                RiderDAO dao = db.riderDao();
                dao.insert(new ArrayList<>(getInMemoryStorage().values()));
            });
        }
    }

    @Override
    public boolean retrieve() {
        //TODO: Retrieve Data using Preferred Persistence Technology:
        executor.submit(() -> {
            int size = db.riderDao().rowCount();
            List<Rider> results = db.riderDao().read(size, 0);
            for (Rider rider: results) {
                put(rider.getId(), rider);
            }
        });
        return true;
    }

    @Override
    public boolean delete() {
        //TODO: Delete Data using Preferred Persistence Technology:
        executor.submit(() -> {
            db.riderDao().deleteAll();
        });
        return true;
    }

}
