package lab.itsoul.com.deliman.rider.startup.activities;

import android.content.Intent;
import android.os.Bundle;

import lab.itsoul.com.deliman.rider.startup.R;

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = new Intent(getApplicationContext(),
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
