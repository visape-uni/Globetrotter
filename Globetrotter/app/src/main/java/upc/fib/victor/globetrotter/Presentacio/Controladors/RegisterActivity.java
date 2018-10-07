package upc.fib.victor.globetrotter.Presentacio.Controladors;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import upc.fib.victor.globetrotter.Presentacio.Fragments.RegisterNameFragment;
import upc.fib.victor.globetrotter.R;

public class RegisterActivity extends AppCompatActivity {
    private static final int NAME = 1;
    private static final int DATE = 2;
    private static final int EMAIL = 3;
    private static final int PASSWORD = 4;

    private Fragment fragment;
    protected FragmentManager fragmentManager;

    private int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fragmentManager = getSupportFragmentManager();

        fragment = RegisterNameFragment.newInstance();
        displayFragment(R.id.frame_layout, fragment, "name");
        currentFragment = NAME;
    }

    // replaces the front fragment with the given fragment
    protected void replaceFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .replace(contentResId, fragment, tag)
                .commit();
    }

    // deletes all the fragments of the stack and displays the given one
    protected void displayFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(contentResId, fragment, tag);
    }
}
