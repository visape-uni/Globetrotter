package upc.fib.victor.globetrotter.Presentation.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Presentation.Utils.RecommendationRecyclerAdapter;
import upc.fib.victor.globetrotter.R;

public class RecommendationsFragment extends Fragment {

    private String uid;

    private TextView errorTxt;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecommendationRecyclerAdapter adapter;

    private ArrayList<String> idsRecommendations;

    private FirebaseDatabaseController firebaseDatabaseController;

    public RecommendationsFragment() {
        // Required empty public constructor
    }

    public static RecommendationsFragment newInstance(String idUser) {
        RecommendationsFragment fragment = new RecommendationsFragment();
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
        firebaseDatabaseController = FirebaseDatabaseController.getInstance(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseDatabaseController.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_recommendations, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progressBar);
        errorTxt = view.findViewById(R.id.noResultsTxt);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        idsRecommendations = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        errorTxt.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        firebaseDatabaseController.getIdsRecommendations(uid, 20, "", new FirebaseDatabaseController.GetIdsRecommendationsResponse() {
            @Override
            public void success(ArrayList<String> ids) {
                idsRecommendations = ids;
                adapter = new RecommendationRecyclerAdapter(getContext(), idsRecommendations, uid);
                recyclerView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
                errorTxt.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void noRecommendations() {
                idsRecommendations = new ArrayList<>();
                adapter = new RecommendationRecyclerAdapter(getContext(), idsRecommendations, uid);
                recyclerView.setAdapter(adapter);

                errorTxt.setText("No se han encontrado resultados.");

                progressBar.setVisibility(View.GONE);
                errorTxt.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void error() {
                idsRecommendations = new ArrayList<>();
                adapter = new RecommendationRecyclerAdapter(getContext(), idsRecommendations, uid);
                recyclerView.setAdapter(adapter);

                errorTxt.setText("Error obteniendo datos.");

                progressBar.setVisibility(View.GONE);
                errorTxt.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

                Toast.makeText(getContext(), "Error obteniendo datos, vuelve a intentarlo...", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
