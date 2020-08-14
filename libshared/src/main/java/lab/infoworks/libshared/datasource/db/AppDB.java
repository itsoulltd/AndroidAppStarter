package lab.infoworks.libshared.datasource.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.locks.ReentrantLock;

import lab.infoworks.libshared.datasource.db.dao.RiderDAO;
import lab.infoworks.libshared.model.Rider;

@Database(entities = {Rider.class}, version = 1)
public abstract class AppDB extends RoomDatabase {

    public static final String DATABASE_NAME = "AppDatabase.db";
    private static volatile AppDB instance;
    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();

    public static AppDB getInstance(Context context){
        if (instance == null){
            REENTRANT_LOCK.lock();
            try {
                if (instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDB.class, DATABASE_NAME).build();
                }
            }catch (Exception e){}
            finally {
                REENTRANT_LOCK.unlock();
            }
        }
        return instance;
    }

    //Declare DAO abstract methods:
    public abstract RiderDAO riderDao();

}
