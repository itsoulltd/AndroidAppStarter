package lab.infoworks.libshared.repository.impl;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.it.soul.lab.data.base.DataSource;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.datasource.RiderDataSource;
import lab.infoworks.libshared.model.Rider;
import lab.infoworks.libshared.repository.definition.RiderRepository;

public class RiderRepositoryImpl implements RiderRepository {

    private DataSource<Integer, Rider> dataSource = new RiderDataSource();

    public RiderRepositoryImpl() {}

    @Override @RequiresApi(Build.VERSION_CODES.N)
    public void findRiders(Consumer<List<Rider>> consumer) {
        if (consumer == null) return;
        int maxItem = dataSource.size();
        dataSource.readAsynch(0, maxItem, (riders) ->
                consumer.accept(Arrays.asList(riders))
        );
    }
}
