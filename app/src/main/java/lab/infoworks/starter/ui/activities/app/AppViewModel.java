package lab.infoworks.starter.ui.activities.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.domain.model.VerificationResult;
import lab.infoworks.libshared.domain.repository.definition.RiderRepository;
import lab.infoworks.libshared.domain.repository.impl.RiderRepositoryImpl;

public class AppViewModel extends AndroidViewModel {

    private MutableLiveData<VerificationResult> userStatusLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Rider>> riderLiveData = new MutableLiveData<>();

    //TODO: make this happen via dependency injection based on debug/release
    private RiderRepository riderRepository = new RiderRepositoryImpl(getApplication().getApplicationContext());

    public AppViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<VerificationResult> getUserStatusObservable() {
        return userStatusLiveData;
    }

    public void verifyUser() {
        if(riderRepository.isEmpty()) riderRepository.addSampleData(getApplication());
        userStatusLiveData.postValue(new VerificationResult(true));
    }

    public LiveData<List<Rider>> getRiderObservable() {
        return riderLiveData;
    }

    public void findRiders() {
        riderRepository.findRiders((riders) -> riderLiveData.postValue(riders));
    }
}
