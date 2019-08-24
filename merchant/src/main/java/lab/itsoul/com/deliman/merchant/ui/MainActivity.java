package lab.itsoul.com.deliman.merchant.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import lab.itsoul.com.deliman.libshared.util.ConnectionUtil;
import lab.itsoul.com.deliman.merchant.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectionUtil.isConnectedToInternet();
    }
}
