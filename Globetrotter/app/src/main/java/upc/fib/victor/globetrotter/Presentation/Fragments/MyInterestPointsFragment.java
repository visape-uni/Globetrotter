package upc.fib.victor.globetrotter.Presentation.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.R;


public class MyInterestPointsFragment extends Fragment {


    private String uid;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private adapter;

    private FirebaseDatabaseController firebaseDatabaseController;

    public MyInterestPointsFragment() {
        // Required empty public constructor
    }

    public static MyInterestPointsFragment newInstance(String idUser) {
        MyInterestPointsFragment fragment = new MyInterestPointsFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_interest_points, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        return view;
    }

}
