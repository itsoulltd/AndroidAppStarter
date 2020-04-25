package lab.infoworks.libshared.repository.definition;

import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.model.Rider;

public interface RiderRepository extends Repository {
    void findRiders(Consumer<List<Rider>> consumer);
}
