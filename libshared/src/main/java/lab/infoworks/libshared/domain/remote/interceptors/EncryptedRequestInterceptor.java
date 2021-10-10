package lab.infoworks.libshared.domain.remote.interceptors;

import com.infoworks.lab.rest.models.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lab.infoworks.libshared.domain.remote.interceptors.definition.EncryptInterceptor;
import lab.infoworks.libshared.util.crypto.shared.SecretKeyStore;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class EncryptedRequestInterceptor implements EncryptInterceptor {

    private final String keyAlias;

    public EncryptedRequestInterceptor(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public RequestBody encrypt(Request original) {
        return new RequestBody() {

            @Override public MediaType contentType() {
                return original.body().contentType();
            }

            @Override public long contentLength() throws IOException {
                return -1l;
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                String subtype = original.body().contentType().subtype();
                if(subtype.contains("json")
                    || subtype.contains("form")) {
                    //...
                    String bodyStr = readRequestBody(original.body());
                    String encoded = SecretKeyStore.getInstance()
                            .encrypt(keyAlias, bodyStr);
                    encoded = encoded.replace(System.getProperty("line.separator"), "");
                    Message msg = new Message().setPayload(encoded);
                    byte[] bytes = msg.toString().getBytes(StandardCharsets.UTF_8);
                    //writing new body-data into sink:
                    sink.write(bytes);
                } else {
                    original.body().writeTo(sink);
                }
            }
        };
    }
}
