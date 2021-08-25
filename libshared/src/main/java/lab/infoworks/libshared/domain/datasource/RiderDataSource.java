package lab.infoworks.libshared.domain.datasource;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.it.soul.lab.data.base.DataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.db.AppDB;
import lab.infoworks.libshared.domain.db.dao.RiderDAO;
import lab.infoworks.libshared.domain.model.Rider;

public class RiderDataSource extends CMDataSource<Integer, Rider> implements DataStorage, AutoCloseable {

    private AppDB db;
    private ExecutorService executor;

    private ExecutorService getExecutor() {
        if (executor == null){
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    public RiderDataSource(Context context){
        this.db = AppDB.getInstance(context);
        retrieve();
    }

    @Override @RequiresApi(Build.VERSION_CODES.N)
    public void readAsync(int offset, int pageSize, Consumer<Rider[]> consumer) {
        if (consumer != null) {
            //TODO:
            List<Rider> items = readSyncAsList(offset, pageSize);
            consumer.accept(items.toArray(new Rider[0]));
        }
    }

    @Override
    public void save(boolean async) {
        //TODO: Save Data using Preferred Persistence Technology:
        if (async){
            getExecutor().submit(() -> {
                RiderDAO dao = db.riderDao();
                dao.insert(new ArrayList<>(getInMemoryStorage().values()));
            });
        }
    }

    @Override
    public boolean retrieve() {
        //TODO: Retrieve Data using Preferred Persistence Technology:
        getExecutor().submit(() -> {
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
        getExecutor().submit(() -> {
            db.riderDao().deleteAll();
        });
        return true;
    }

    @Override
    public void close() throws Exception {
        if (executor != null && !executor.isShutdown()){
            executor.shutdown();
            executor = null;
        }
    }
}
