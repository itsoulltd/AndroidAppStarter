package lab.infoworks.libshared.domain.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.infoworks.lab.rest.models.Response;

@Entity(tableName = "rider", ignoredColumns = {"status", "error", "message", "payload", "event", "classType", "_isAutoIncremented"})
public class Rider extends Response {

    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    private String geoHash;
    private Integer age;
    private String gender;
    private String email;

    @Ignore
    public Rider() {}

    @Ignore
    public Rider(String name, String geoHash) {
        this.name = name;
        this.geoHash = geoHash;
    }

    public Rider(Integer id, String name, String geoHash, Integer age, String gender, String email) {
        this.id = id;
        this.name = name;
        this.geoHash = geoHash;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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
