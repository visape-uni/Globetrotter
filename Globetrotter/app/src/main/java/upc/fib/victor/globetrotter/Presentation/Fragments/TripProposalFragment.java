package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Controllers.FirebaseStorageController;
import upc.fib.victor.globetrotter.Controllers.GlideApp;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.Domain.TripProposal;
import upc.fib.victor.globetrotter.Presentation.Utils.ProfileRecyclerAdapter;
import upc.fib.victor.globetrotter.R;


public class TripProposalFragment extends Fragment {

    private String uid;
    private String idPorposal;

    private TripProposal tripProposal;
    private ArrayList<Profile> profiles;

    private ImageView userImage;

    private Menu menu;

    private TextView countryLbl;
    private TextView fechaInicioLbl;
    private TextView fechaRegresoLbl;
    private TextView presupuestoLbl;

    private TextView userNameTxt;
    private TextView countryTxt;
    private TextView fechaInicioTxt;
    private TextView fechaRegresoTxt;
    private TextView presupuestoTxt;

    private ProgressBar progressBar;
    private ProgressBar progressBarMembers;

    private TextView noMembersTxt;

    private TextView errorTxt;
    private TextView comentarioLbl;
    private TextView comentarioTxt;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProfileRecyclerAdapter adapter;

    private FirebaseDatabaseController firebaseDatabaseController;
    private FirebaseStorageController firebaseStorageController;

    public TripProposalFragment() {
        // Required empty public constructor
    }

    public static TripProposalFragment newInstance(String idProposal, String idUser) {
        TripProposalFragment fragment = new TripProposalFragment();
        Bundle args = new Bundle();
        args.putString("uid", idUser);
        args.putString("id", idProposal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            idPorposal = getArguments().getString("id");
        }
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
        firebaseStorageController = FirebaseStorageController.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_proposal, container, false);
        setHasOptionsMenu(true);

        userImage = view.findViewById(R.id.userImage);
        userNameTxt = view.findViewById(R.id.userNameTxt);
        countryTxt = view.findViewById(R.id.countryTxt);
        fechaInicioTxt = view.findViewById(R.id.fechaInicioTxt);
        fechaRegresoTxt = view.findViewById(R.id.fechaRegresoTxt);
        presupuestoTxt = view.findViewById(R.id.presupuestoTxt);
        progressBar = view.findViewById(R.id.progressBar);
        countryLbl = view.findViewById(R.id.countryLbl);
        fechaInicioLbl = view.findViewById(R.id.fechaInicioLbl);
        fechaRegresoLbl = view.findViewById(R.id.fechaRegresoLbl);
        presupuestoLbl = view.findViewById(R.id.presupuestoLbl);
        errorTxt = view.findViewById(R.id.errorTxt);
        comentarioLbl = view.findViewById(R.id.comentarioLbl);
        comentarioTxt = view.findViewById(R.id.comentarioTxt);
        progressBarMembers = view.findViewById(R.id.progressBarMembers);
        noMembersTxt = view.findViewById(R.id.noMembersTxt);

        recyclerView = view.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        profiles = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        userNameTxt.setVisibility(View.GONE);
        countryTxt.setVisibility(View.GONE);
        fechaInicioTxt.setVisibility(View.GONE);
        fechaRegresoTxt.setVisibility(View.GONE);
        presupuestoTxt.setVisibility(View.GONE);
        countryLbl.setVisibility(View.GONE);
        fechaInicioLbl.setVisibility(View.GONE);
        fechaRegresoLbl.setVisibility(View.GONE);
        presupuestoLbl.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);
        errorTxt.setVisibility(View.GONE);
        comentarioTxt.setVisibility(View.GONE);
        comentarioLbl.setVisibility(View.GONE);

        noMembersTxt.setVisibility(View.GONE);
        progressBarMembers.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        firebaseDatabaseController.getTripProposal(idPorposal, new FirebaseDatabaseController.GetTripProposalResponse() {
            @Override
            public void success(final TripProposal proposal) {

                tripProposal = proposal;

                firebaseStorageController.loadImageToView("profiles/" + tripProposal.getUidUser() + ".jpg", new FirebaseStorageController.GetImageResponse() {
                    @Override
                    public void load(StorageReference ref) {
                        GlideApp.with(getContext())
                                .load(ref)
                                .placeholder(getContext().getResources().getDrawable(R.drawable.silueta))
                                .into(userImage);
                    }
                });

                userNameTxt.setText(tripProposal.getUserName());
                countryTxt.setText(tripProposal.getCountry());

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                fechaInicioTxt.setText(dateFormat.format(tripProposal.getIniDate()));
                fechaRegresoTxt.setText(dateFormat.format(tripProposal.getEndDate()));

                if(tripProposal.getBudget() == -1) {
                     presupuestoTxt.setText("No calculado...");
                } else {
                    presupuestoTxt.setText(String.valueOf(tripProposal.getBudget()));
                }

                if(tripProposal.getMessage().isEmpty()) {
                    comentarioTxt.setText("El usuario no ha comentado nada.");
                } else {
                    comentarioTxt.setText(tripProposal.getMessage());
                }

                progressBar.setVisibility(View.GONE);
                errorTxt.setVisibility(View.GONE);
                userNameTxt.setVisibility(View.VISIBLE);
                countryTxt.setVisibility(View.VISIBLE);
                fechaInicioTxt.setVisibility(View.VISIBLE);
                fechaRegresoTxt.setVisibility(View.VISIBLE);
                presupuestoTxt.setVisibility(View.VISIBLE);
                countryLbl.setVisibility(View.VISIBLE);
                fechaInicioLbl.setVisibility(View.VISIBLE);
                fechaRegresoLbl.setVisibility(View.VISIBLE);
                presupuestoLbl.setVisibility(View.VISIBLE);
                userImage.setVisibility(View.VISIBLE);
                comentarioTxt.setVisibility(View.VISIBLE);
                comentarioLbl.setVisibility(View.VISIBLE);


                progressBarMembers.setVisibility(View.VISIBLE);
                firebaseDatabaseController.getMembersOfTrip(proposal.getId(), new FirebaseDatabaseController.GetMembersOfTripResponse() {
                    @Override
                    public void success(ArrayList<Profile> members) {

                        profiles = members;
                        adapter = new ProfileRecyclerAdapter(getContext(), profiles);
                        recyclerView.setAdapter(adapter);

                        recyclerView.setVisibility(View.VISIBLE);
                        noMembersTxt.setVisibility(View.GONE);
                        progressBarMembers.setVisibility(View.GONE);
                    }

                    @Override
                    public void noUsers() {

                        profiles = new ArrayList<>();
                        adapter = new ProfileRecyclerAdapter(getContext(), profiles);
                        recyclerView.setAdapter(adapter);

                        noMembersTxt.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        progressBarMembers.setVisibility(View.GONE);
                    }

                    @Override
                    public void error() {

                        profiles = new ArrayList<>();
                        adapter = new ProfileRecyclerAdapter(getContext(), profiles);
                        recyclerView.setAdapter(adapter);

                        noMembersTxt.setText("Error obteniendo datos");

                        recyclerView.setVisibility(View.GONE);
                        noMembersTxt.setVisibility(View.VISIBLE);
                        progressBarMembers.setVisibility(View.GONE);

                        Toast.makeText(getContext(), "Error obteniendo datos, vuelve a intentarlo...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void error(String message) {
                errorTxt.setText("Error obteninedo datos, intentelo de nuevo");

                errorTxt.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });



        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_trip, menu);
        this.menu = menu;
        Drawable drawable = menu.findItem(R.id.action_join_trip).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        }
        drawable = menu.findItem(R.id.action_delete_from_trip).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        }

        showOptions();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_join_trip:
                firebaseDatabaseController.joinTrip(tripProposal.getId(), uid, new FirebaseDatabaseController.JoinTripResponse() {
                    @Override
                    public void success() {
                        firebaseDatabaseController.getProfile(uid, new FirebaseDatabaseController.GetProfileResponse() {
                            @Override
                            public void success(Profile profile) {
                                int insertIndex = profiles.size();
                                profiles.add(insertIndex, profile);
                                adapter.notifyItemInserted(insertIndex);

                                recyclerView.setVisibility(View.VISIBLE);
                                noMembersTxt.setVisibility(View.GONE);

                                Toast.makeText(getContext(), "Te has apuntado al viaje", Toast.LENGTH_LONG).show();
                                menu.findItem(R.id.action_join_trip).setVisible(false);
                                menu.findItem(R.id.action_delete_from_trip).setVisible(true);
                            }

                            @Override
                            public void notFound() {
                                Toast.makeText(getContext(), "Error obteniendo usuarios apuntados", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void error(String message) {
                                Toast.makeText(getContext(), "Error obteniendo usuarios apuntados", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void error() {
                        Toast.makeText(getContext(), "Ha habido un error y no te has apuntado al viaje", Toast.LENGTH_LONG).show();
                    }
                });
                break;

            case R.id.action_delete_from_trip:
                firebaseDatabaseController.unjoinTrip(tripProposal.getId(), uid, new FirebaseDatabaseController.JoinTripResponse() {
                    @Override
                    public void success() {

                        firebaseDatabaseController.getProfile(uid, new FirebaseDatabaseController.GetProfileResponse() {
                            @Override
                            public void success(Profile profile) {
                                int removeIndex = profiles.indexOf(profile);
                                if(removeIndex != -1) {
                                    profiles.remove(removeIndex);
                                    adapter.notifyItemRemoved(removeIndex);
                                }

                                if (profiles.size() == 0) {
                                    recyclerView.setVisibility(View.GONE);
                                    noMembersTxt.setVisibility(View.VISIBLE);
                                }

                                Toast.makeText(getContext(), "Te has desapuntado del viaje", Toast.LENGTH_LONG).show();
                                menu.findItem(R.id.action_join_trip).setVisible(true);
                                menu.findItem(R.id.action_delete_from_trip).setVisible(false);
                            }

                            @Override
                            public void notFound() {
                                Toast.makeText(getContext(), "Error obteniendo usuarios apuntados", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void error(String message) {
                                Toast.makeText(getContext(), "Error obteniendo usuarios apuntados", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void error() {
                        Toast.makeText(getContext(), "Ha habido un error y no te has desapuntado del viaje", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showOptions() {

        firebaseDatabaseController.getMembersOfTrip(idPorposal, new FirebaseDatabaseController.GetMembersOfTripResponse() {
            @Override
            public void success(ArrayList<Profile> members) {
                int i = 0;
                boolean find = false;
                while (i < members.size() && !find) {
                    if (members.get(i).getUid().equals(uid)) find = true;
                    ++i;
                }
                if (find) {
                    menu.findItem(R.id.action_join_trip).setVisible(false);
                    menu.findItem(R.id.action_delete_from_trip).setVisible(true);
                } else {
                    menu.findItem(R.id.action_join_trip).setVisible(true);
                    menu.findItem(R.id.action_delete_from_trip).setVisible(false);
                }
            }

            @Override
            public void noUsers() {
                menu.findItem(R.id.action_join_trip).setVisible(true);
                menu.findItem(R.id.action_delete_from_trip).setVisible(false);
            }

            @Override
            public void error() {
                menu.findItem(R.id.action_join_trip).setVisible(true);
                menu.findItem(R.id.action_delete_from_trip).setVisible(false);
            }
        });
    }
}
