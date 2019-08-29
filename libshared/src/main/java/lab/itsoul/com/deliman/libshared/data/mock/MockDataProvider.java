package lab.itsoul.com.deliman.libshared.data.mock;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;
import com.it.soul.lab.sql.entity.Entity;
import com.itsoul.lab.android.interactor.eadge.services.HttpMockilate;
import com.itsoul.lab.android.interactor.eadge.services.Mockitor;
import com.itsoul.lab.android.interactor.rest.HttpTemplate;
import com.itsoul.lab.domain.base.Consume;
import com.itsoul.lab.domain.base.ProduceCollection;
import com.itsoul.lab.domain.models.auth.UserInfo;
import com.itsoul.lab.interactor.exceptions.HttpInvocationException;
import com.itsoul.lab.interactor.interfaces.Interactor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lab.itsoul.com.deliman.libshared.data.Constants;
import lab.itsoul.com.deliman.libshared.model.Rider;
import lab.itsoul.com.deliman.libshared.model.VerificationResult;


public class MockDataProvider {

    private static final String TAG = MockDataProvider.class.getName();
    private Application application;
    private HttpMockilate<VerificationResult, Consume> template;
    private HttpMockilate<ProduceCollection<Rider>, Consume> template2;
    private URI uri = URI.create(Constants.BASE_URL);

    MockDataProvider(Application application) {
        this.application = application;
        mockData();
    }

    private void mockData() {
        try {
            this.template = Interactor.create(HttpMockilate.class, uri);
            this.template2 = Interactor.create(HttpMockilate.class, uri);
            initMocking();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void initMocking() {
        mockVerification();
        mockRidersFind();
    }

    private void mockVerification() {
        template.registerPathToMockitor(Constants.Rider.RIDER_VERIFY, new Mockitor<VerificationResult>() {
            @Override
            public VerificationResult accept() {
                return new VerificationResult(true);
            }
        });
    }

    private void mockRidersFind() {
        template2.registerPathToMockitor(Constants.Rider.RIDER_FIND, new Mockitor<ProduceCollection<Rider>>() {
            @Override
            public ProduceCollection<Rider> accept() {
                ProduceCollection<Rider> riders = new ProduceCollection<>();
                Rider rider = new Rider("Rider-1", "sdfaf");
                Rider rider2 = new Rider("Rider-2", "sdfafdf");
                riders.setCollections(Arrays.asList(rider, rider2));
                return riders;
            }
        });
    }

    //TODO: intelligent json mapping
    private Object initMockData() {

        try {
            InputStream stream = application.getResources().getAssets().open("data/rider-mock-data.json");
            Log.d("===>", "stream is " + stream);
            try {
                int size = stream.available();
                byte[] buffer = new byte[size];
                stream.read(buffer);
                stream.close();
                String json = new String(buffer, "UTF-8");
                Map map = new Gson().fromJson(json, Map.class);
                Log.d("===>", "map data " + map.get("verify"));

                return map;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new JSONArray();
    }

    public VerificationResult verifyRider() {
        String[] paths = Constants.Rider.RIDER_VERIFY.split("/");
        try {
            return template.post(null, paths);
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }

        return new VerificationResult(false);
    }

    public List<Rider> findRiders() {
        String[] paths = Constants.Rider.RIDER_FIND.split("/");
        try {
            return template2.post(null, paths).getCollections();
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


}
