package lab.infoworks.libui.activities;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public abstract class KeepScreenOnActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public void onBottomSheetButtonClick(int refCode) {
        //TODO:
    }
}
