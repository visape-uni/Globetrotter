package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.TripProposal;
import upc.fib.victor.globetrotter.Presentation.Utils.TripProposalRecyclerAdapter;
import upc.fib.victor.globetrotter.R;

public class SearchTravelFragment extends Fragment {

    private String uid;

    private SearchView searchView;
    private TextView noResultsTxt;
    private ProgressBar progressBar;

    private ArrayList<String> idsTripProposals;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TripProposalRecyclerAdapter adapter;

    private FirebaseDatabaseController firebaseDatabaseController;

    public SearchTravelFragment() {
        // Required empty public constructor
    }

    public static SearchTravelFragment newInstance(String idUser) {
        SearchTravelFragment fragment = new SearchTravelFragment();
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
        View view =  inflater.inflate(R.layout.fragment_search_travel, container, false);

        searchView = view.findViewById(R.id.search_travel_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progressBar);
        noResultsTxt = view.findViewById(R.id.noResultsTxt);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        idsTripProposals = new ArrayList<>();

        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Escribe el país");

        //TODO: firebaseDatabase get proposals
        firebaseDatabaseController.getIdsProposals(20, "", new FirebaseDatabaseController.GetIdsPublicationsResponse() {
            @Override
            public void success(ArrayList<String> idsPublications) {
                idsTripProposals = idsPublications;
                adapter = new TripProposalRecyclerAdapter(getContext(), idsTripProposals);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void noPublications() {
                adapter = new TripProposalRecyclerAdapter(getContext(), idsTripProposals);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                noResultsTxt.setVisibility(View.VISIBLE);
                noResultsTxt.setText("No hay publicaciones.");
                noResultsTxt.setTextColor(Color.GRAY);
            }

            @Override
            public void error() {
                adapter = new TripProposalRecyclerAdapter(getContext(), idsTripProposals);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                noResultsTxt.setVisibility(View.VISIBLE);
                noResultsTxt.setText("Error cargando las propuestas de viaje...");
                noResultsTxt.setTextColor(Color.GRAY);
            }
        });

        //TODO: setOnQueryTextListener get proposals searched
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
