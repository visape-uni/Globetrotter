package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.Presentation.Utils.ProfileRecyclerAdapter;
import upc.fib.victor.globetrotter.R;

public class SearchUserFragment extends Fragment {

    private String uid;

    private SearchView searchView;
    private TextView noResultsTxt;
    private ProgressBar progressBar;

    private ArrayList<Profile> profiles;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProfileRecyclerAdapter adapter;

    private FirebaseDatabaseController firebaseDatabaseController;

    public SearchUserFragment() {
        // Required empty public constructor
    }

    public static SearchUserFragment newInstance(String idUser) {
        SearchUserFragment fragment = new SearchUserFragment();
        Bundle args = new Bundle();
        args.putString("uid", idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
        }
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_user, container, false);

        searchView = view.findViewById(R.id.search_user_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progressBar);
        noResultsTxt = view.findViewById(R.id.noResultsTxt);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        profiles = new ArrayList<>();

        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                noResultsTxt.setVisibility(View.GONE);

                firebaseDatabaseController.searchUsers(query, uid, new FirebaseDatabaseController.SearchUsersResponse() {
                    @Override
                    public void success(ArrayList<Profile> users) {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        noResultsTxt.setVisibility(View.GONE);

                        profiles = users;
                        adapter = new ProfileRecyclerAdapter(getContext(), profiles);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void noUsers() {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noResultsTxt.setVisibility(View.VISIBLE);

                        adapter = new ProfileRecyclerAdapter(getContext(), profiles);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void error() {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noResultsTxt.setVisibility(View.VISIBLE);

                        adapter = new ProfileRecyclerAdapter(getContext(), profiles);
                        recyclerView.setAdapter(adapter);

                        Toast.makeText(getContext(), "Error obteniendo datos, vuelve a intentarlo...", Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

}
