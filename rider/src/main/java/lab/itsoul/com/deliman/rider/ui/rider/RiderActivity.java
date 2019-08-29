package lab.itsoul.com.deliman.rider.ui.rider;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lab.itsoul.com.deliman.rider.startup.R;


public class RiderActivity extends AppCompatActivity {

    private static final String TAG = RiderActivity.class.getName();
    @BindView(R.id.verificationStatusTextView)
    TextView verificationStatusTextView;

    @BindView(R.id.verifyButton)
    TextView verifyButton;

    private RiderViewModel riderViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider);
        ButterKnife.bind(this);

        riderViewModel = new RiderViewModel(getApplication());
        riderViewModel.getUserStatusLiveData().observe(this, verificationResult -> {
            Log.d(TAG, "===> result: " + verificationResult.isVerified());
            verificationStatusTextView.setText("Rider is verified.... :) ");
            verifyButton.setEnabled(false);
        });
        riderViewModel.getRidersLiverData().observe(this, riders -> {
            Log.d(TAG, "===> number of riders found: " + riders.size());
        });
    }


    @OnClick(R.id.verifyButton)
    public void verifyRider() {
        riderViewModel.verifyUser();
    }

    @OnClick(R.id.findRidersButton)
    public void findRiders() {
        riderViewModel.findRiders();
    }
}
