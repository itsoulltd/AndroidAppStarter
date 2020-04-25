package lab.infoworks.libshared.datasource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import lab.infoworks.libshared.model.Rider;

public class RiderDataSourceTest {

    RiderDataSource dataSource;

    @Before
    public void setUp() throws Exception {

        dataSource = new RiderDataSource();
        int index = dataSource.size() - 1;
        dataSource.put(index++, new Rider()
                .setName("John")
                .setEmail("john@gmail.com")
                .setAge(36)
                .setGender("male"));

        dataSource.put(index++, new Rider()
                .setName("Eve")
                .setEmail("eve@gmail.com")
                .setAge(21)
                .setGender("female"));

        dataSource.put(index++, new Rider()
                .setName("Mosses")
                .setEmail("mosses@gmail.com")
                .setAge(31)
                .setGender("male"));

        dataSource.put(index++, new Rider()
                .setName("Abraham")
                .setEmail("abraham@gmail.com")
                .setAge(31)
                .setGender("male"));

        dataSource.put(index++, new Rider()
                .setName("Ahmed")
                .setEmail("ahmed@gmail.com")
                .setAge(31)
                .setGender("male"));

        dataSource.put(index++, new Rider()
                .setName("Adam")
                .setEmail("adam@gmail.com")
                .setAge(31)
                .setGender("male"));
    }

    @After
    public void tearDown() throws Exception {
        dataSource = null;
    }

    @Test
    public void readTest(){

        System.out.println("===========================0-(datasource.size())======================");
        int maxItem = Long.valueOf(dataSource.size()).intValue();
        List<Rider> readAll = Arrays.asList(dataSource.readSynch(0, maxItem));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================1-2==========================");
        readAll = Arrays.asList(dataSource.readSynch(1, 2));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================2-3=========================");
        readAll = Arrays.asList(dataSource.readSynch(2, 3));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================0-3=========================");
        readAll = Arrays.asList(dataSource.readSynch(0, 3));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("===========================0-2========================");
        readAll = Arrays.asList(dataSource.readSynch(0, 2));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================100-10=======================");
        readAll = Arrays.asList(dataSource.readSynch(100, 10));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================0-0=======================");
        readAll = Arrays.asList(dataSource.readSynch(0, 0));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================1-0=======================");
        readAll = Arrays.asList(dataSource.readSynch(1, 0));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================4-1=======================");
        readAll = Arrays.asList(dataSource.readSynch(4, 1));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================4-2=======================");
        readAll = Arrays.asList(dataSource.readSynch(4, 2));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
        System.out.println("==========================4-3=======================");
        readAll = Arrays.asList(dataSource.readSynch(4, 3));
        for (Rider p : readAll) {
            System.out.println(p.getName() + ":" + p.getEmail());
        }
    }

}