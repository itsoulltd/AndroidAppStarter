package lab.infoworks.libshared.domain.remote.interceptors.definition;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public interface EncryptInterceptor extends Interceptor {

    RequestBody encrypt(Request original);

    @NonNull @Override
    default Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        if (original.body() == null || original.body().contentLength() <= 0) {
            return chain.proceed(original);
        }
        //
        Request encryptedRequest = original.newBuilder()
                .method(original.method(), encrypt(original))
                .build();
        return chain.proceed(encryptedRequest);
    }

    default String readRequestBody(final RequestBody request){
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if(copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }

}
