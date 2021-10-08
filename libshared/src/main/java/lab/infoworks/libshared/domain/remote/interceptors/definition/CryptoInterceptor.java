package lab.infoworks.libshared.domain.remote.interceptors.definition;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public interface CryptoInterceptor extends EncryptInterceptor, DecryptInterceptor {

    @NonNull @Override
    default Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (request.body() != null && request.body().contentLength() > 0) {
            request = request.newBuilder()
                    .method(request.method(), encrypt(request))
                    .build();
        }
        //
        Response response = chain.proceed(request);
        if (response.body() == null) {
            return response;
        } else {
            return response.newBuilder()
                    .body(decrypt(response))
                    .build();
        }
    }

}
