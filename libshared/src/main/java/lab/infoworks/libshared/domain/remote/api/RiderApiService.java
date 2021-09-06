package lab.infoworks.libshared.domain.remote.api;

import com.infoworks.lab.rest.models.ItemCount;

import java.util.List;

import lab.infoworks.libshared.domain.model.Rider;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RiderApiService {

    @GET("/rider")
    Call<List<Rider>> fetch(@Query("limit") int limit, @Query("offset") int offset);

    @GET("/rider/rowCount")
    Call<ItemCount> rowCount();

    @POST("/rider")
    Call<Rider> create(@Body Rider rider);

    @PUT("/rider")
    Call<Rider> update(@Body Rider rider);

    @DELETE("/rider")
    Call<Response> delete(@Query("userid") int userid);

}
