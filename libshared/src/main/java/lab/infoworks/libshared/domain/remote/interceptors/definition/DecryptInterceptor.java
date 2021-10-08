package lab.infoworks.libshared.domain.remote.interceptors.definition;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public interface DecryptInterceptor extends Interceptor {

    ResponseBody decrypt(Response original);

    @NonNull @Override
    default Response intercept(@NonNull Chain chain) throws IOException {
        Response original = chain.proceed(chain.request());
        if (original.body() == null) {
            return original;
        }
        //
        Response decryptedResponse = original.newBuilder()
                .body(decrypt(original))
                .build();
        return decryptedResponse;
    }

    default String readResponseBody(final ResponseBody response){
        try {
            final ResponseBody copy = response;
            final Buffer buffer = new Buffer();
            if(copy != null)
                buffer.write(copy.bytes());
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }

}
