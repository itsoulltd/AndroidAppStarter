package lab.itsoul.com.deliman.libshared;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chat {
    private final HttpUrl basUrl;

    public Chat(HttpUrl baseUrl) {
        this.basUrl = baseUrl;
    }

    public String loadMore() {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(basUrl)
                .get()
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
