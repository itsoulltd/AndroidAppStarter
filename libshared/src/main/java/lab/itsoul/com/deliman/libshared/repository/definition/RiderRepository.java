package lab.itsoul.com.deliman.libshared.repository.definition;

import androidx.lifecycle.LiveData;

import java.util.List;

import lab.itsoul.com.deliman.libshared.model.Rider;

public interface RiderRepository extends Repository {
    LiveData<List<Rider>> findRiders();
}
