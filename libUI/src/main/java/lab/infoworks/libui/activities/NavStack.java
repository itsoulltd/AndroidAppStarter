package lab.infoworks.libui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class NavStack {

    public static NavStack create(AppCompatActivity activity, int containerId){
        return new NavStack(containerId, activity.getSupportFragmentManager(), activity.getSupportActionBar());
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

    public void pushNavStack(Fragment fragment, String tag) {
        navStack.add(0, fragment);
        if (tag != null && !tag.isEmpty()){
            tagStack.add(0, tag);
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


    public void popNavStack(){
        if (navStack.size() <= 1) return;
        Fragment fragment = navStack.remove(0);
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
        //pop-next and push to fragment-maager
        fragment = navStack.remove(0);
        String tag = tagStack.remove(0);
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
