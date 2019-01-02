package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Presentation.Utils.TripProposalRecyclerAdapter;
import upc.fib.victor.globetrotter.R;

public class SearchTravelFragment extends Fragment {

    private String uid;

    private SearchView searchView;
    private TextView noResultsTxt;
    private ProgressBar progressBar;

    private FloatingActionButton addBtn;

    private ArrayList<String> idsTripProposals;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TripProposalRecyclerAdapter adapter;

    private FirebaseDatabaseController firebaseDatabaseController;

    private OnFragmentInteractionListener mListener;

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
        addBtn = view.findViewById(R.id.addBtn);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        idsTripProposals = new ArrayList<>();

        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Escribe el país");

        firebaseDatabaseController.getIdsProposals(20, "", new FirebaseDatabaseController.GetIdsPublicationsResponse() {
            @Override
            public void success(ArrayList<String> idsPublications) {
                idsTripProposals = idsPublications;
                adapter = new TripProposalRecyclerAdapter(getContext(), idsTripProposals, uid);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                noResultsTxt.setVisibility(View.GONE);
            }

            @Override
            public void noPublications() {
                idsTripProposals = new ArrayList<>();
                adapter = new TripProposalRecyclerAdapter(getContext(), idsTripProposals, uid);
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
                noResultsTxt.setVisibility(View.VISIBLE);
                noResultsTxt.setText("No se han encontrado resultados.");
                noResultsTxt.setTextColor(Color.GRAY);
            }

            @Override
            public void error() {
                idsTripProposals = new ArrayList<>();
                adapter = new TripProposalRecyclerAdapter(getContext(), idsTripProposals, uid);
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
                noResultsTxt.setVisibility(View.VISIBLE);
                noResultsTxt.setText("Error cargando las propuestas de viaje...");
                noResultsTxt.setTextColor(Color.GRAY);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTripProposalFragment addTripFragment = AddTripProposalFragment.newInstance(uid);
                mListener.onAddClicked(addTripFragment);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                noResultsTxt.setVisibility(View.GONE);

                firebaseDatabaseController.searchTrip(query, new FirebaseDatabaseController.SearchTripProposalResponse() {
                    @Override
                    public void success(ArrayList<String> idTrips) {
                        idsTripProposals = idTrips;
                        adapter = new TripProposalRecyclerAdapter(getContext(), idsTripProposals, uid);
                        recyclerView.setAdapter(adapter);

                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        noResultsTxt.setVisibility(View.GONE);
                    }

                    @Override
                    public void noTrips() {
                        idsTripProposals = new ArrayList<>();
                        adapter = new TripProposalRecyclerAdapter(getContext(), idsTripProposals, uid);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.GONE);

                        progressBar.setVisibility(View.GONE);
                        noResultsTxt.setVisibility(View.VISIBLE);
                        noResultsTxt.setText("No se han encontrado resultados.");
                        Toast.makeText(getContext(), "No se han encontrado resultados", Toast.LENGTH_LONG).show();
                        noResultsTxt.setTextColor(Color.GRAY);
                    }

                    @Override
                    public void error() {
                        idsTripProposals = new ArrayList<>();

                        adapter = new TripProposalRecyclerAdapter(getContext(), idsTripProposals, uid);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        noResultsTxt.setVisibility(View.VISIBLE);
                        noResultsTxt.setText("Error cargando las propuestas de viaje...");
                        noResultsTxt.setTextColor(Color.GRAY);
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



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onAddClicked(AddTripProposalFragment addTripProposalFragment);
    }
}
