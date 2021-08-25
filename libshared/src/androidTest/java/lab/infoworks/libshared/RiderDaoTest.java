package lab.infoworks.libshared;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import lab.infoworks.libshared.domain.datasource.SampleData;
import lab.infoworks.libshared.domain.db.AppDB;
import lab.infoworks.libshared.domain.db.dao.RiderDAO;
import lab.infoworks.libshared.domain.model.Rider;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RiderDaoTest {

    public static String TAG = "RiderDaoTest";
    private AppDB db;
    private RiderDAO dao;

    @Before
    public void before(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(appContext, AppDB.class).build();
        dao = db.riderDao();
        Log.i(TAG, "DB Created");
    }

    @After
    public void after(){
        db.close();
        Log.i(TAG, "DB Closed");
    }

    @Test
    public void createAndRetrieve() {
        dao.insert(Arrays.asList(SampleData.getRiders()));
        int rows = dao.rowCount();
        Log.i(TAG, "Rider Counts: " + rows);
        Assert.assertEquals(SampleData.getRiders().length, rows);
    }

    @Test
    public void compareString(){
        dao.insert(Arrays.asList(SampleData.getRiders()));
        Rider fromDB = dao.searchByName("Rider").get(0);
        Rider original = SampleData.getRiders()[0];
        Assert.assertEquals(original.getName(), fromDB.getName());
        Log.i(TAG, "FromDB: " + fromDB.toString());
    }

}
