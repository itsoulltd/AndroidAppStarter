package lab.itsoul.com.deliman.starter.ui.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lab.itsoul.com.deliman.libshared.model.Rider;
import lab.itsoul.com.deliman.libshared.model.VerificationResult;
import lab.itsoul.com.deliman.libshared.repository.definition.RiderRepository;
import lab.itsoul.com.deliman.libshared.repository.impl.RiderRepositoryImpl;

public class AppViewModel extends AndroidViewModel {

    private MutableLiveData<VerificationResult> userStatusLiveData = new MutableLiveData<>();

    //TODO: make this happen via dependency injection based on debug/release
    private RiderRepository riderRepository = new RiderRepositoryImpl();

    public AppViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<VerificationResult> getUserStatusLiveData() {
        return userStatusLiveData;
    }

    public void verifyUser() {
        userStatusLiveData.postValue(new VerificationResult(true));
    }

    public LiveData<List<Rider>> findRiders() {
        return riderRepository.findRiders();
    }
}
