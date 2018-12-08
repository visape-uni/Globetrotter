package upc.fib.victor.globetrotter.Presentation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.Presentation.Fragments.SearchUserFragment;
import upc.fib.victor.globetrotter.Presentation.Utils.FollowUsersRecyclerAdapter;
import upc.fib.victor.globetrotter.Presentation.Utils.ProfileRecyclerAdapter;
import upc.fib.victor.globetrotter.R;

public class FollowersActivity extends AppCompatActivity {
    private String uid;

    private ArrayList<String> profiles;

    private FirebaseDatabaseController firebaseDatabaseController;

    private TextView errorTxt;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FollowUsersRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        setTitle("Seguidores");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViews();

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        errorTxt.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        profiles = new ArrayList<>();

        firebaseDatabaseController.getFollowersUsers(uid, new FirebaseDatabaseController.GetProfileIdsResponse() {
            @Override
            public void success(ArrayList<String> idsProfiles) {
                profiles = idsProfiles;
                adapter = new FollowUsersRecyclerAdapter(getApplicationContext(), profiles);
                recyclerView.setAdapter(adapter);

                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                errorTxt.setVisibility(View.GONE);
            }

            @Override
            public void noFollowing() {
                adapter = new FollowUsersRecyclerAdapter(getApplicationContext(), profiles);
                recyclerView.setAdapter(adapter);
                errorTxt.setText("A este usuario no le sigue nadie");

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                errorTxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void error() {
                adapter = new FollowUsersRecyclerAdapter(getApplicationContext(), profiles);
                recyclerView.setAdapter(adapter);
                errorTxt.setText("Error accediendo a los datos, vuelve a intentarlo");

                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                errorTxt.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
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

    private void findViews() {
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
        recyclerView = findViewById(R.id.recycler_view);
        errorTxt = findViewById(R.id.errorTxt);
        progressBar = findViewById(R.id.progressBar);
    }
}
