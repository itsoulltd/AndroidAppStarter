package lab.itsoul.com.deliman.libshared.repository.definition;

import java.util.List;
import java.util.function.Consumer;

import lab.itsoul.com.deliman.libshared.model.Rider;

public interface RiderRepository extends Repository {
    void findRiders(Consumer<List<Rider>> consumer);
}
