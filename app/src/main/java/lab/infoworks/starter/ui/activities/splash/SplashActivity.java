package lab.infoworks.starter.ui.activities.splash;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;

import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.app.AppActivity;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startHandler();
    }

    private void startHandler() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, AppActivity.class);
            startActivity(intent);
            finish();
        },1000);
    }

}
