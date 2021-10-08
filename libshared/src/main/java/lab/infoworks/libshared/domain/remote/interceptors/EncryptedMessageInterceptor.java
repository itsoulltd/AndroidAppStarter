package lab.infoworks.libshared.domain.remote.interceptors;

import lab.infoworks.libshared.domain.remote.interceptors.definition.CryptoInterceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EncryptedMessageInterceptor implements CryptoInterceptor {

    private final EncryptedRequestInterceptor encryptInterceptor;
    private final DecryptedResponseInterceptor decryptedInterceptor;

    public EncryptedMessageInterceptor(String keyAlias) {
        encryptInterceptor = new EncryptedRequestInterceptor(keyAlias);
        decryptedInterceptor = new DecryptedResponseInterceptor(keyAlias);
    }

    @Override
    public ResponseBody decrypt(Response original) {
        return decryptedInterceptor.decrypt(original);
    }

    @Override
    public RequestBody encrypt(Request original) {
        return encryptInterceptor.encrypt(original);
    }
}
