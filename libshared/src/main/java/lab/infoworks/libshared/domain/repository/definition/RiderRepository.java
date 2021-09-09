package lab.infoworks.libshared.domain.repository.definition;

import android.content.Context;

import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.model.Rider;

public interface RiderRepository {
    void findRiders(Consumer<List<Rider>> consumer);
    void update(Rider rider);
    boolean isEmpty();
    void addSampleData(Context context);
}
