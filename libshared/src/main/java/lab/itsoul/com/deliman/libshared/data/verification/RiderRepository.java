package lab.itsoul.com.deliman.libshared.data.verification;

import androidx.lifecycle.MutableLiveData;

import com.itsoul.lab.domain.models.auth.UserInfo;

import java.util.List;

import kotlin.Unit;
import lab.itsoul.com.deliman.libshared.data.Repository;
import lab.itsoul.com.deliman.libshared.model.Rider;
import lab.itsoul.com.deliman.libshared.model.VerificationResult;

public class RiderRepository implements Repository {

    private RiderDataSource riderDataSource;

    public RiderRepository(RiderDataSource riderDataSource) {
        this.riderDataSource = riderDataSource;
    }

    public void verifyUser(UserInfo userInfo, MutableLiveData<VerificationResult> result) {
        scheduler.execute(() -> {
            VerificationResult verificationResult = riderDataSource.verify(userInfo);
            result.postValue(verificationResult);
            return Unit.INSTANCE;
        });
    }

    public void findRiders(MutableLiveData<List<Rider>> result) {
        scheduler.execute(() -> {
            List<Rider> riders = riderDataSource.findRiders();
            result.postValue(riders);
            return Unit.INSTANCE;
        });
    }

}
