package lab.infoworks.libshared.domain.shared;

import android.app.Application;
import android.content.Context;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import lab.infoworks.libshared.domain.datasource.RiderDataSource;
import lab.infoworks.libshared.domain.model.Rider;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CacheManagerTest {

    Application appContext;

    @Before
    public void setUp() throws Exception {
        appContext = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
    }

    @After
    public void tearDown() throws Exception {
        appContext = null;
    }

    @Test
    public void savingTestInSharedPref(){
        //Settings:
        AppStorage storage = AppStorage.getCurrent(appContext);
        RiderDataSource dataSource = getRiderDatasource(appContext);
        CacheManager<Rider> riderCache = new CacheManager<>("rider-cache");
        //Saving:
        riderCache.clear()
                .add(dataSource.readSyncAsList(0, dataSource.size()))
                .save(storage);
        //Read Back:
        List<Rider> readBack = riderCache.fetch(0, dataSource.size());
        Assert.assertTrue(readBack != null);
        Assert.assertTrue(readBack.size() == dataSource.size());
    }

    private RiderDataSource getRiderDatasource(Context appContext) {
        RiderDataSource dataSource = new RiderDataSource(appContext);

        dataSource.add(new Rider()
                .setName("John")
                .setEmail("john@gmail.com"));

        dataSource.add(new Rider()
                .setName("Eve")
                .setEmail("eve@gmail.com"));

        dataSource.add(new Rider()
                .setName("Mosses")
                .setEmail("mosses@gmail.com"));

        dataSource.add(new Rider()
                .setName("Abraham")
                .setEmail("abraham@gmail.com"));

        dataSource.add(new Rider()
                .setName("Ahmed")
                .setEmail("ahmed@gmail.com"));

        dataSource.add(new Rider()
                .setName("Adam")
                .setEmail("adam@gmail.com"));

        return dataSource;
    }
}