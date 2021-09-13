package lab.infoworks.libui.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.it.soul.lab.sql.query.models.Property;

import java.util.ArrayList;
import java.util.List;

public class NavStack {

    public static NavStack create(AppCompatActivity activity, int containerId){
        return new NavStack(containerId, activity.getSupportFragmentManager(), activity.getSupportActionBar());
    }

    public static Bundle createBundle(Property...properties){
        Bundle args = new Bundle();
        if (properties.length > 0){
            for (Property prop : properties) {
                if (prop.getValue() == null) continue;
                args.putString(prop.getKey(), prop.getValue().toString());
            }
        }
        return args;
    }

    public static Fragment bindArgs(Fragment fragment, Property...properties){
        fragment.setArguments(createBundle(properties));
        return fragment;
    }

    private FragmentManager manager;
    private ActionBar actionBar;
    private int fragContainerId;

    private NavStack(int containerId, FragmentManager manager, ActionBar actionBar) {
        this.fragContainerId = containerId;
        this.manager = manager;
        this.actionBar = actionBar;
    }

    public FragmentManager getSupportFragmentManager() {
        return manager;
    }

    private List<Fragment> navStack = new ArrayList<>();
    private List<String> tagStack = new ArrayList<>();

    public void initStackWithFragment(String tag){
        Fragment defaultFragment = getSupportFragmentManager().findFragmentByTag(tag);
        pushNavStack(defaultFragment, tag);
    }

    public void initStackWithFragment(int id){
        Fragment defaultFragment = getSupportFragmentManager().findFragmentById(id);
        pushNavStack(defaultFragment, id + "");
    }

    public void pushNavStack(Fragment fragment, String tag) {
        if (tag == null || tag.isEmpty()) return;
        if (fragment == null) return;
        navStack.add(0, fragment);
        tagStack.add(0, tag);
        boolean isAdded = fragment.isAdded();
        if (!isAdded){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(fragContainerId, fragment, tag)
                    .commit();
            //Handle back-arrow
            if (actionBar != null){
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public void popNavStack(Property...properties){
        if (isOnTop()) return;
        Fragment fragment = navStack.remove(0);
        tagStack.remove(0);
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
        //pop-next and push to fragment-maager
        fragment = navStack.remove(0);
        String tag = tagStack.remove(0);
        //
        fragment.setArguments(createBundle(properties));
        pushNavStack(fragment, tag);
        //handle back-arrow
        if (isOnTop()){
            if (actionBar != null){
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    public void close() {
        manager = null;
        actionBar = null;
        navStack.clear();
        tagStack.clear();
    }

    public boolean isOnTop(){
        return (navStack.size() == 1) ? true : false;
    }
}
