package lab.itsoul.com.deliman.starter.ui.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lab.itsoul.com.deliman.starter.startup.R;


public class AppActivity extends AppCompatActivity {

    private static final String TAG = AppActivity.class.getName();
    @BindView(R.id.verificationStatusTextView)
    TextView verificationStatusTextView;

    @BindView(R.id.verifyButton)
    TextView verifyButton;

    private AppViewModel appViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider);
        ButterKnife.bind(this);

        appViewModel = new AppViewModel(getApplication());
        appViewModel.getUserStatusObservable().observe(this, verificationResult -> {
            Log.d(TAG, "===> result: " + verificationResult.isVerified());
            verificationStatusTextView.setText("Rider is verified.... :) ");
            verifyButton.setEnabled(false);
        });
        appViewModel.getRiderObservable().observe(this, riders -> {
            Log.d(TAG, "===> number of riders found: " + riders.size());
            verificationStatusTextView.setText("number of riders found: " + riders.size());
            verifyButton.setEnabled(true);
        });
    }


    @OnClick(R.id.verifyButton)
    public void verifyRider() {
        appViewModel.verifyUser();
    }

    @OnClick(R.id.findRidersButton)
    public void findRiders() {
        appViewModel.findRiders();
    }
}
