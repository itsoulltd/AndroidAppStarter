package lab.infoworks.libshared.domain.remote.interceptors;

import com.infoworks.lab.rest.models.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lab.infoworks.libshared.domain.remote.interceptors.definition.DecryptInterceptor;
import lab.infoworks.libshared.util.crypto.SecretKeyStore;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DecryptedResponseInterceptor implements DecryptInterceptor {

    private final String keyAlias;

    public DecryptedResponseInterceptor(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public ResponseBody decrypt(Response original) {
        //
        MediaType contentType = original.body().contentType();
        ResponseBody body = original.body();
        //
        String subtype = original.body().contentType().subtype();
        if (subtype.contains("text")){
            String strRes = readResponseBody(original.body());
            String decrypted = SecretKeyStore.getInstance()
                    .decrypt(keyAlias, strRes);
            body = ResponseBody.create(decrypted.getBytes(StandardCharsets.UTF_8), contentType);
        } else if(subtype.contains("json")) {
            String json = readResponseBody(original.body());
            if (Message.isValidJson(json)){
                try {
                    Message msg = Message.unmarshal(com.infoworks.lab.rest.models.Response.class, json);
                    String decrypted = SecretKeyStore.getInstance()
                            .decrypt(keyAlias, msg.getPayload());
                    msg.setPayload(decrypted);
                    body = ResponseBody.create(msg.toString().getBytes(StandardCharsets.UTF_8), contentType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //
        return body;
    }
}
