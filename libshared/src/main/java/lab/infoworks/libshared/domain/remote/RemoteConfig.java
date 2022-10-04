package lab.infoworks.libshared.domain.remote;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import lab.infoworks.libshared.BuildConfig;
import okhttp3.CertificatePinner;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RemoteConfig {

    public static <ApiService> ApiService getInstance(String BASE_URL, Class<ApiService> serviceClass, Interceptor...interceptors) {
        //Setup-Builder:
        OkHttpClient.Builder okHttpClientBuilder;
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .sslSocketFactory(BypassHttpsForDebug.getSSLSocketFactory());
        } else {
            okHttpClientBuilder = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);
            //Adding CertificatePinner if exist:
            if (createCertificatePinner() != null) {
                okHttpClientBuilder.certificatePinner(createCertificatePinner());
            }
        }
        //Setup Interceptors:
        for (Interceptor ceptor : interceptors) {
            okHttpClientBuilder.addInterceptor(ceptor);
        }
        //
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClientBuilder.build())
                //.addConverterFactory(GsonConverterFactory.create(getGson()))
                .addConverterFactory(JacksonConverterFactory.create(getMapper()))
                .build();
        //
        return retrofit.create(serviceClass);
    }

    private static ObjectMapper getMapper(){
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private static Gson getGson(){
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    private static CertificatePinner sslPinner;
    private static String domainName;
    private static String sslPublicKey;

    public static void activateSslCertificatePinner(String domainName, String sslPublicKey) {
        setDomainName(domainName);
        setSslPublicKey(sslPublicKey);
        sslPinner = createCertificatePinner();
    }

    private static CertificatePinner createCertificatePinner() {
        if (sslPinner != null) return sslPinner;
        if (domainName == null || sslPublicKey == null) return null;
        return new CertificatePinner.Builder()
                .add(domainName, sslPublicKey)
                .build();
    }

    public static void setSslPublicKey(String sslPublicKey) {
        RemoteConfig.sslPublicKey = sslPublicKey;
    }

    public static void setDomainName(String domainName) {
        RemoteConfig.domainName = domainName;
    }

}
