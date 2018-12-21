package upc.fib.victor.globetrotter.Presentation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import upc.fib.victor.globetrotter.Presentation.Fragments.MyInterestPointsFragment;
import upc.fib.victor.globetrotter.R;

public class RecommendationsActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    private Fragment fragment;
    protected FragmentManager fragmentManager;

    private String uid;

    private String currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fragmentManager = getSupportFragmentManager();
        findViews();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        loadFragmentRecommendations();
                        break;

                    case 1:
                        loadFragmentMyInterestPoints();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        loadFragmentMyInterestPoints();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
            profileIntent.putExtra("uidOwner", uid);
            startActivity(profileIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
        profileIntent.putExtra("uidOwner", uid);
        startActivity(profileIntent);
        finish();
    }

    private void loadFragmentRecommendations() {
        currentFragment = "recommendations";

    }

    private void loadFragmentMyInterestPoints() {
        currentFragment = "myPoints";
        fragment = MyInterestPointsFragment.newInstance(uid);
        displayFragment(R.id.frame_layout, fragment, "myPoints");
    }

    private void findViews() {
        tabLayout = findViewById(R.id.tabLayout);
    }

    // adds the given fragment to the front of the fragment stack
    protected void addFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .remove(fragmentManager.findFragmentById(R.id.frame_layout))
                .add(contentResId, fragment, tag)
                .addToBackStack(tag)
                .commit();
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
