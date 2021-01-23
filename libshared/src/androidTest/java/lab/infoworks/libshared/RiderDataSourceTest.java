package lab.infoworks.libshared;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import lab.infoworks.libshared.domain.datasource.RiderDataSource;
import lab.infoworks.libshared.domain.model.Rider;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RiderDataSourceTest {

    public static String TAG = "RiderDataSourceTest";
    RiderDataSource dataSource;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        dataSource = new RiderDataSource(appContext);
        //
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
    }

    @After
    public void tearDown() throws Exception {
        dataSource = null;
    }

    @Test
    public void readTest(){

        System.out.println("===========================0-(datasource.size())======================");
        int maxItem = Long.valueOf(dataSource.size()).intValue();
        List<Rider> readAll = Arrays.asList(dataSource.readSync(0, maxItem));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================1-2==========================");
        readAll = Arrays.asList(dataSource.readSync(1, 2));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================2-3=========================");
        readAll = Arrays.asList(dataSource.readSync(2, 3));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================0-3=========================");
        readAll = Arrays.asList(dataSource.readSync(0, 3));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================0-2========================");
        readAll = Arrays.asList(dataSource.readSync(0, 2));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================100-10=======================");
        readAll = Arrays.asList(dataSource.readSync(100, 10));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================0-0=======================");
        readAll = Arrays.asList(dataSource.readSync(0, 0));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================1-0=======================");
        readAll = Arrays.asList(dataSource.readSync(1, 0));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================4-1=======================");
        readAll = Arrays.asList(dataSource.readSync(4, 1));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================4-2=======================");
        readAll = Arrays.asList(dataSource.readSync(4, 2));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================4-3=======================");
        readAll = Arrays.asList(dataSource.readSync(4, 3));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
    }

}