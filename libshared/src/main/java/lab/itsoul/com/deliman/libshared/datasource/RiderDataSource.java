package lab.itsoul.com.deliman.libshared.datasource;

import android.os.Handler;

import com.itsoul.lab.android.data.base.DataSource;

import java.util.function.Consumer;

import lab.itsoul.com.deliman.libshared.model.Rider;

public class RiderDataSource implements DataSource<Integer, Rider> {

    private Rider[] getDummyData(){
        return new Rider[]{
                new Rider("Rider-1", "sdfaf")
                , new Rider("Rider-2", "sdfafdf")
        };
    }

    @Override
    public void readAsynch(int offset, int pageSize, Consumer<Rider[]> consumer) {
        if (consumer != null) {
            new Handler().postDelayed(() -> consumer.accept(getDummyData()), 1000);
        }
    }
}
