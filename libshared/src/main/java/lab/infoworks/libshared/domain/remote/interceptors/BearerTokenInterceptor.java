package lab.infoworks.libshared.domain.remote.interceptors;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BearerTokenInterceptor implements Interceptor {

    private String jwtToken;

    public BearerTokenInterceptor(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String jwt = jwtToken;
        if (jwt == null || jwt.isEmpty()){
            return chain.proceed(chain.request());
        }
        //rewrite the request to add bearer token
        Request newRequest=chain.request().newBuilder()
                .header("Authorization","Bearer "+ jwt)
                .build();
        return chain.proceed(newRequest);
    }
}
