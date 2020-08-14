package lab.infoworks.libshared.datasource.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import lab.infoworks.libshared.model.Rider;

@Dao
public interface RiderDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Rider rider);

    @Delete
    void delete(Rider rider);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Rider> riders);

    @Query("DELETE FROM rider")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM rider")
    int rowCount();

    @Query("SELECT * FROM rider ORDER BY id ASC LIMIT :limit OFFSET :offset")
    List<Rider> read(int limit, int offset);

    @Query("SELECT * FROM rider WHERE name LIKE '%' || :name || '%' ORDER BY id ASC")
    List<Rider> searchByName(String name);

}
