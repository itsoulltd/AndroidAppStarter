package lab.itsoul.com.deliman.libshared.data;

import lab.itsoul.com.deliman.libshared.util.DefaultScheduler;
import lab.itsoul.com.deliman.libshared.util.Scheduler;

public interface Repository {
    Scheduler scheduler = DefaultScheduler.INSTANCE;
}
