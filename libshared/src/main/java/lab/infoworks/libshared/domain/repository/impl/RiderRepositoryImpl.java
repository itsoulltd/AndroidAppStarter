package lab.infoworks.libshared.domain.repository.impl;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.data.base.DataStorage;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.datasource.RiderDataSource;
import lab.infoworks.libshared.domain.datasource.SampleData;
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.domain.repository.definition.RiderRepository;

public class RiderRepositoryImpl implements RiderRepository {

    private final DataSource<Integer, Rider> dataSource;
    private SoftReference<Context> contextRef;

    public RiderRepositoryImpl(Context context) {
        this.dataSource = new RiderDataSource(context);
        this.contextRef = new SoftReference<>(context);
    }

    @Override @RequiresApi(Build.VERSION_CODES.N)
    public void findRiders(Consumer<List<Rider>> consumer) {
        if (consumer == null) return;
        final int maxItem = dataSource.size();
        //TODO: For Simulating Network: Delay-1 sec
        new Handler().postDelayed(() -> {
                    //Calling API:
                    dataSource.readAsync(0, maxItem, (riders) ->
                            consumer.accept(Arrays.asList(riders))
                    );
                }
                , 1000);
    }

    @Override
    public void addSampleData() {
        for (Rider rider : SampleData.getRidersFrom(contextRef.get())) {
            dataSource.add(rider);
        }
        ((DataStorage)dataSource).save(true);
    }

    @Override
    public boolean isEmpty() {
        return dataSource.size() <= 0;
    }
}
