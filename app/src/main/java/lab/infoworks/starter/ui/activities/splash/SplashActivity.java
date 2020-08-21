package lab.infoworks.starter.ui.activities.splash;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import lab.infoworks.starter.R;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            start(1000, bundle.getString("landingPage"));
        } catch (ClassNotFoundException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void start(long withDelay, String landingPage) throws ClassNotFoundException {
        if (landingPage == null || landingPage.isEmpty()) throw new ClassNotFoundException();
        final Class<?> to = Class.forName(landingPage);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, to);
            startActivity(intent);
            finish();
        },withDelay);
    }

}
