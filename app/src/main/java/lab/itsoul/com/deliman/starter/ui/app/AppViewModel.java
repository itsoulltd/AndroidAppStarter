package lab.itsoul.com.deliman.starter.ui.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.itsoul.lab.domain.models.auth.UserInfo;

import java.util.List;

import lab.itsoul.com.deliman.libshared.data.mock.MockRiderDataSource;
import lab.itsoul.com.deliman.libshared.data.rider.RiderRepository;
import lab.itsoul.com.deliman.libshared.model.Rider;
import lab.itsoul.com.deliman.libshared.model.VerificationResult;

public class AppViewModel extends AndroidViewModel {

    private MutableLiveData<VerificationResult> userStatusLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Rider>> ridersLiverData = new MutableLiveData<>();
    //TODO: make this happen via dependency injection based on debug/release
    private RiderRepository riderRepository = new RiderRepository(new MockRiderDataSource(getApplication()));

    public AppViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<VerificationResult> getUserStatusLiveData() {
        return userStatusLiveData;
    }

    public MutableLiveData<List<Rider>> getRidersLiverData() {
        return ridersLiverData;
    }

    public void verifyUser() {
        UserInfo userInfo = new UserInfo();
        riderRepository.verifyUser(userInfo, userStatusLiveData);
    }

    public void findRiders() {
        riderRepository.findRiders(ridersLiverData);
    }
}
