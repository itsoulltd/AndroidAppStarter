package lab.infoworks.libshared;

import android.content.Context;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import lab.infoworks.libshared.domain.datasource.RiderDataSource;
import lab.infoworks.libshared.domain.model.Rider;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RiderDataSourceTest {

    public static String TAG = "RiderDataSourceTest";
    RiderDataSource dataSource;
    Context appContext;

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        dataSource = getRiderDatasource(appContext);
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

    @After
    public void tearDown() throws Exception {
        dataSource = null;
        appContext = null;
    }

    @Test
    public void readTest(){

        System.out.println("===========================0-MaxSize======================");
        int maxItem = Long.valueOf(dataSource.size()).intValue();
        List<Rider> readAll = dataSource.readSyncAsList(0, maxItem);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================1-2==========================");
        readAll = dataSource.readSyncAsList(1, 2);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================2-3=========================");
        readAll = dataSource.readSyncAsList(2, 3);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================0-3=========================");
        readAll = dataSource.readSyncAsList(0, 3);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================0-2========================");
        readAll = dataSource.readSyncAsList(0, 2);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================100-10=======================");
        readAll = dataSource.readSyncAsList(100, 10);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================0-0=======================");
        readAll = dataSource.readSyncAsList(0, 0);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================1-0=======================");
        readAll = dataSource.readSyncAsList(1, 0);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================4-1=======================");
        readAll = dataSource.readSyncAsList(4, 1);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================4-2=======================");
        readAll = dataSource.readSyncAsList(4, 2);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================4-3=======================");
        readAll = dataSource.readSyncAsList(4, 3);
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
    }

}