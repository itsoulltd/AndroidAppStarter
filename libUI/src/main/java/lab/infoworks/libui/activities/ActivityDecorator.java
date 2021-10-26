package lab.infoworks.libui.activities;

import android.content.Intent;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import lab.infoworks.libui.alert.AlertSheetFragment;

public abstract class ActivityDecorator implements AlertSheetFragment.OnFragmentInteractionListener {

    private WeakReference<BaseActivity> _ref;

    public ActivityDecorator(BaseActivity ref) {
        this._ref = new WeakReference<>(ref);
        ref.setDecorator(this);
    }

    public BaseActivity getActivity() {return _ref.get();}
    public Class<? extends BaseActivity> getActivityClass() {return _ref.get().getClass();}

    public abstract void onStart();

    public abstract void onResume();

    public abstract void onPause();

    public abstract void onDestroy();

    public abstract void onRestart();

    public abstract void onStop();

    public abstract void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    public abstract void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);

    public void startActivity(Class<?> type) {
        Intent intent = new Intent(getActivity(), type);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    public static void startActivity(ActivityDecorator decorator, Class<?> type) {
        Intent intent = new Intent(decorator.getActivity(), type);
        decorator.getActivity().startActivity(intent);
        decorator.getActivity().finish();
    }
}
