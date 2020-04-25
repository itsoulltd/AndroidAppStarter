package lab.infoworks.libshared.repository.definition;

import lab.infoworks.libshared.util.DefaultScheduler;
import lab.infoworks.libshared.util.Scheduler;

public interface Repository {
    Scheduler scheduler = DefaultScheduler.INSTANCE;
}
