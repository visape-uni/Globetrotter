package upc.fib.victor.globetrotter.Presentation.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Controllers.FirebaseStorageController;
import upc.fib.victor.globetrotter.Controllers.GlideApp;
import upc.fib.victor.globetrotter.Domain.TripProposal;
import upc.fib.victor.globetrotter.R;

public class TripProposalRecyclerAdapter extends RecyclerView.Adapter<TripProposalRecyclerAdapter.TripProposalViewHolder> {

    private ArrayList<String> tripProposalsIds;
    private Context context;
    private FirebaseStorageController firebaseStorageController;
    private FirebaseDatabaseController firebaseDatabaseController;

    public static class TripProposalViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout tripProposalLayout;
        public ProgressBar progressBar;
        public TextView errorTxt;
        public ImageView userImage;
        public TextView userName;
        public TextView countryTxt;
        public TextView durationTxt;
        public TextView iniDateTxt;
        public TextView endDateTxt;
        public TextView publicationTxt;
        public TextView presupuestoTxt;
        public TextView dateTxt;
        public TripProposal tripProposal;

        public TripProposalViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userNameTxt);
            countryTxt = itemView.findViewById(R.id.countryTxt);
            durationTxt = itemView.findViewById(R.id.durationTxt);
            iniDateTxt = itemView.findViewById(R.id.iniDateTxt);
            endDateTxt = itemView.findViewById(R.id.endDateTxt);
            publicationTxt = itemView.findViewById(R.id.publicationTxt);
            presupuestoTxt = itemView.findViewById(R.id.presupuestoTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            errorTxt = itemView.findViewById(R.id.errorTxt);
            progressBar = itemView.findViewById(R.id.publicationProgressBar);
        }
    }


    public TripProposalRecyclerAdapter (Context context, ArrayList<String> idsTripProposals) {
        firebaseStorageController = FirebaseStorageController.getInstance();
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
        this.context = context;
        this.tripProposalsIds = idsTripProposals;
    }

    @NonNull
    @Override
    public TripProposalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_proposal, parent, false);

        return new TripProposalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final TripProposalViewHolder holder, int position) {

        holder.progressBar.setVisibility(View.VISIBLE);
        holder.userName.setVisibility(View.GONE);
        holder.countryTxt.setVisibility(View.GONE);
        holder.durationTxt.setVisibility(View.GONE);
        holder.iniDateTxt.setVisibility(View.GONE);
        holder.endDateTxt.setVisibility(View.GONE);
        holder.publicationTxt.setVisibility(View.GONE);
        holder.presupuestoTxt.setVisibility(View.GONE);
        holder.dateTxt.setVisibility(View.GONE);
        holder.errorTxt.setVisibility(View.GONE);


        firebaseDatabaseController.getTripProposal(tripProposalsIds.get(position), new FirebaseDatabaseController.GetTripProposalResponse() {
            @Override
            public void success(TripProposal tripProposal) {
                holder.tripProposal = tripProposal;
                firebaseStorageController.loadImageToView("profiles/" + holder.tripProposal.getUidUser() + ".jpg", new FirebaseStorageController.GetImageResponse() {
                    @Override
                    public void load(StorageReference ref) {
                        GlideApp.with(context)
                                .load(ref)
                                .placeholder(context.getResources().getDrawable(R.drawable.silueta))
                                .into(holder.userImage);
                    }
                });
                holder.userName.setText(holder.tripProposal.getUserName());
                holder.countryTxt.setText(holder.tripProposal.getCountry());

                holder.durationTxt.setText(String.valueOf(holder.tripProposal.getDuration()) + " Dias");

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                holder.iniDateTxt.setText(dateFormat.format(holder.tripProposal.getIniDate()) + " -> ");
                holder.endDateTxt.setText(dateFormat.format(holder.tripProposal.getEndDate()));
                holder.publicationTxt.setText(holder.tripProposal.getMessage());

                if(holder.tripProposal.getBudget() == -1) {
                    holder.presupuestoTxt.setText("Presupuesto: No calculado...");
                } else {
                    holder.presupuestoTxt.setText("Presupuesto: " + String.valueOf(holder.tripProposal.getBudget()) + "€");
                }

                holder.dateTxt.setText(dateFormat.format(holder.tripProposal.getDate()));


                holder.progressBar.setVisibility(View.GONE);
                holder.userName.setVisibility(View.VISIBLE);
                holder.countryTxt.setVisibility(View.VISIBLE);
                holder.durationTxt.setVisibility(View.VISIBLE);
                holder.iniDateTxt.setVisibility(View.VISIBLE);
                holder.endDateTxt.setVisibility(View.VISIBLE);
                holder.publicationTxt.setVisibility(View.VISIBLE);
                holder.presupuestoTxt.setVisibility(View.VISIBLE);
                holder.dateTxt.setVisibility(View.VISIBLE);
                holder.errorTxt.setVisibility(View.GONE);
            }

            @Override
            public void error(String message) {
                holder.progressBar.setVisibility(View.GONE);
                holder.errorTxt.setVisibility(View.VISIBLE);
                holder.errorTxt.setText("Error obteniendo la propuesta de viaje");
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripProposalsIds.size();
    }

}
