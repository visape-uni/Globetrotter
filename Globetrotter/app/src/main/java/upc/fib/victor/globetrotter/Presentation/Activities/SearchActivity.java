package upc.fib.victor.globetrotter.Presentation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import upc.fib.victor.globetrotter.Presentation.Fragments.AddTripProposalFragment;
import upc.fib.victor.globetrotter.Presentation.Fragments.SearchTravelFragment;
import upc.fib.victor.globetrotter.Presentation.Fragments.SearchUserFragment;
import upc.fib.victor.globetrotter.Presentation.Fragments.TripProposalFragment;
import upc.fib.victor.globetrotter.R;

public class SearchActivity extends AppCompatActivity implements SearchTravelFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;

    private Fragment fragment;
    protected FragmentManager fragmentManager;

    private String uid;

    private String currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setTitle("Buscador");

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
                        loadFragmentUser();
                        break;

                    case 1:
                        loadFragmentTrip();
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


        loadFragmentUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            if (currentFragment.equals("addTrip") || currentFragment.equals("tripProposal")) {
                fragmentManager.popBackStackImmediate();
                currentFragment = "searchTrip";
            } else {
                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                profileIntent.putExtra("uidOwner", uid);
                startActivity(profileIntent);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (currentFragment.equals("addTrip") || currentFragment.equals("tripProposal")) {
            fragmentManager.popBackStackImmediate();
            currentFragment = "searchTrip";
        } else {
            Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
            profileIntent.putExtra("uidOwner", uid);
            startActivity(profileIntent);
            finish();
        }
    }

    private void loadFragmentUser() {
        currentFragment = "searchUser";
        fragment = SearchUserFragment.newInstance(uid);
        displayFragment(R.id.frame_layout, fragment, "searchUser");
    }

    private void loadFragmentTrip() {
        currentFragment = "searchTrip";
        fragment = SearchTravelFragment.newInstance(uid);
        displayFragment(R.id.frame_layout, fragment, "searchTrip");
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

    public void showTrip(TripProposalFragment tripProposalFragment) {
        currentFragment = "tripProposal";
        addFragment(R.id.frame_layout, tripProposalFragment, "tripProposal");
    }

    @Override
    public void onAddClicked(AddTripProposalFragment addTripProposalFragment) {
        currentFragment = "addTrip";
        addFragment(R.id.frame_layout, addTripProposalFragment, "addTrip");
    }
}
