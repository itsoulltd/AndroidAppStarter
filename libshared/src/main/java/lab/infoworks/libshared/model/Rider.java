package lab.infoworks.libshared.model;

import com.infoworks.lab.rest.models.Response;

public class Rider extends Response {
    private String name;
    private String geoHash;
    private int age;
    private String gender;
    private String email;

    public Rider() {}

    public Rider(String name, String geoHash) {
        this.name = name;
        this.geoHash = geoHash;
    }

    public String getName() {
        return name;
    }

    public Rider setName(String name) {
        this.name = name;
        return this;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public Rider setGeoHash(String geoHash) {
        this.geoHash = geoHash;
        return this;
    }

    public int getAge() {
        return age;
    }

    public Rider setAge(int age) {
        this.age = age;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public Rider setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Rider setEmail(String email) {
        this.email = email;
        return this;
    }
}
