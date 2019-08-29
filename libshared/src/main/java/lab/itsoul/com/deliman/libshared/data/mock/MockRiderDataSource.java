package lab.itsoul.com.deliman.libshared.data.mock;

import android.app.Application;

import com.itsoul.lab.domain.models.auth.UserInfo;

import java.util.List;

import lab.itsoul.com.deliman.libshared.data.rider.RiderDataSource;
import lab.itsoul.com.deliman.libshared.model.Rider;
import lab.itsoul.com.deliman.libshared.model.VerificationResult;

public class MockRiderDataSource implements RiderDataSource {
    private MockDataProvider mockDataProvider;

    public MockRiderDataSource(Application application) {
        mockDataProvider = new MockDataProvider(application);
    }

    @Override
    public VerificationResult verify(UserInfo userInfo) {
        return mockDataProvider.verifyRider();
    }

    @Override
    public List<Rider> findRiders() {
        return mockDataProvider.findRiders();
    }
}
