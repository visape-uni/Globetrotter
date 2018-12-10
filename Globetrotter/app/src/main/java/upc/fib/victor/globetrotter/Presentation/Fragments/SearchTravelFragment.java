package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.content.Context;
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
import upc.fib.victor.globetrotter.Presentation.Utils.TravelProposalRecyclerAdapter;
import upc.fib.victor.globetrotter.R;

public class SearchTravelFragment extends Fragment {

    private String uid;

    private SearchView searchView;
    private TextView noResultsTxt;
    private ProgressBar progressBar;

    private ArrayList<TripProposal> tripProposals;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TravelProposalRecyclerAdapter adapter;

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

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        tripProposals = new ArrayList<>();

        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Escribe el país");

        //TODO: firebaseDatabase get proposals

        //TODO: setOnQueryTextListener get proposals searched

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
