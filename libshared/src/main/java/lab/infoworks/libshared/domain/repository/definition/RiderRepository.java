package lab.infoworks.libshared.domain.repository.definition;

import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.model.Rider;

public interface RiderRepository {
    void findRiders(Consumer<List<Rider>> consumer);
    void addSampleData();
    boolean isEmpty();
}
