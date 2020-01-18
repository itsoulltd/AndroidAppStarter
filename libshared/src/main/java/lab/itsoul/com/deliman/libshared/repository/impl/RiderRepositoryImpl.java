package lab.itsoul.com.deliman.libshared.repository.impl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.itsoul.lab.android.data.base.DataSource;

import java.util.Arrays;
import java.util.List;

import lab.itsoul.com.deliman.libshared.datasource.RiderDataSource;
import lab.itsoul.com.deliman.libshared.model.Rider;
import lab.itsoul.com.deliman.libshared.repository.definition.RiderRepository;

public class RiderRepositoryImpl implements RiderRepository {

    private DataSource<Integer, Rider> dataSource = new RiderDataSource();

    public RiderRepositoryImpl() {}

    @Override
    public LiveData<List<Rider>> findRiders() {
        MutableLiveData<List<Rider>> liveData = new MutableLiveData<>();
        int maxItem = Long.valueOf(dataSource.size()).intValue();
        dataSource.readAsynch(0, maxItem, (riders) ->
                liveData.postValue(Arrays.asList(riders))
        );
        return liveData;
    }
}
