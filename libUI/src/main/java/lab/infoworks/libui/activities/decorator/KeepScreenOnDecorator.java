package lab.infoworks.libui.activities.decorator;

import android.content.Intent;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import lab.infoworks.libui.activities.ActivityDecorator;
import lab.infoworks.libui.activities.BaseActivity;

public class KeepScreenOnDecorator extends ActivityDecorator {

    public KeepScreenOnDecorator(BaseActivity ref) {
        super(ref);
    }

    @Override
    public void onStart() {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBottomSheetButtonClick(int refCode) {
        //TODO:
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //
    }
}
