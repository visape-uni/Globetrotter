package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.Publication;
import upc.fib.victor.globetrotter.Presentation.Utils.MyRecyclerScroll;
import upc.fib.victor.globetrotter.Presentation.Utils.PublicationAdapter;
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

    private static String idUserWall;
    private static String uid;

    private TextView errorTxt;
    private RecyclerView mRecyclerView;
    private PublicationAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private MyRecyclerScroll myRecyclerScroll;

    private OnFragmentInteractionListener mListener;

    public WallFragment() {
        // Required empty public constructor
    }

    public static WallFragment newInstance(String idApp, String idUser) {
        WallFragment fragment = new WallFragment();
        idUserWall = idUser;
        uid = idApp;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        firebaseDatabaseController.getIdsPublications(idUserWall, 20, "", new FirebaseDatabaseController.GetIdsPublicationsResponse() {
            @Override
            public void success(ArrayList<String> idsPublications) {
                publicationIds = idsPublications;
                mAdapter = new PublicationAdapter(getContext(), publicationIds, uid);
                mRecyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void noPublications() {
                progressBar.setVisibility(View.GONE);
                mAdapter = new PublicationAdapter(getContext(), publicationIds, uid);
                mRecyclerView.setAdapter(mAdapter);
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("No hay publicaciones");
            }

            @Override
            public void error() {
                progressBar.setVisibility(View.GONE);
                mAdapter = new PublicationAdapter(getContext(), publicationIds, uid);
                mRecyclerView.setAdapter(mAdapter);
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("Error cargando las publicaciones...");
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
