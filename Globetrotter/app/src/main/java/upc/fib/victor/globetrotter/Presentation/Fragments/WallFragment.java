package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Presentation.Utils.MyRecyclerScroll;
import upc.fib.victor.globetrotter.Presentation.Utils.PublicationRecyclerAdapter;
import upc.fib.victor.globetrotter.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WallFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WallFragment extends Fragment {

    private ProgressBar progressBar;

    private FirebaseDatabaseController firebaseDatabaseController;

    private ArrayList<String> publicationIds;

    private String idUserWall;
    private String uid;

    private TextView errorTxt;
    private RecyclerView mRecyclerView;
    private PublicationRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private MyRecyclerScroll myRecyclerScroll;

    private OnFragmentInteractionListener mListener;

    public WallFragment() {
        // Required empty public constructor
    }

    public static WallFragment newInstance(String uid, String idUserWall) {
        WallFragment fragment = new WallFragment();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putString("idUserWall", idUserWall);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idUserWall = getArguments().getString("idUserWall");
            uid = getArguments().getString("uid");
        }
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_wall, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        errorTxt = view.findViewById(R.id.error);
        errorTxt.setVisibility(View.GONE);

        mRecyclerView = view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        publicationIds = new ArrayList<>();

        mAdapter = new PublicationRecyclerAdapter(getContext(), publicationIds, uid);
        mRecyclerView.setAdapter(mAdapter);

        firebaseDatabaseController.getIdsPublications(idUserWall, 20, "", new FirebaseDatabaseController.GetIdsPublicationsResponse() {
            @Override
            public void success(ArrayList<String> idsPublications) {
                publicationIds = idsPublications;
                mAdapter = new PublicationRecyclerAdapter(getContext(), publicationIds, uid);
                mRecyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void noPublications() {
                progressBar.setVisibility(View.GONE);
                mAdapter = new PublicationRecyclerAdapter(getContext(), publicationIds, uid);
                mRecyclerView.setAdapter(mAdapter);
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("No hay publicaciones.");
                errorTxt.setTextColor(Color.GRAY);
            }

            @Override
            public void error() {
                progressBar.setVisibility(View.GONE);
                mAdapter = new PublicationRecyclerAdapter(getContext(), publicationIds, uid);
                mRecyclerView.setAdapter(mAdapter);
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("Error cargando las publicaciones...");
                errorTxt.setTextColor(Color.parseColor("#ff0000"));
            }
        });

        myRecyclerScroll = new MyRecyclerScroll() {
            @Override
            public void hide() {
                mListener.hideInteraction();
            }
        };

        mRecyclerView.addOnScrollListener(myRecyclerScroll);

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

    public void showInteraction() {
        myRecyclerScroll.show();
    }

    public interface OnFragmentInteractionListener {
        void hideInteraction();
    }
}
