package lab.itsoul.com.deliman.libshared.repository.definition;

import lab.itsoul.com.deliman.libshared.util.DefaultScheduler;
import lab.itsoul.com.deliman.libshared.util.Scheduler;

public interface Repository {
    Scheduler scheduler = DefaultScheduler.INSTANCE;
}
