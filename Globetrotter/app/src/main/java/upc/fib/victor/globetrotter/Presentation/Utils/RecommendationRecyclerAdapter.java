package upc.fib.victor.globetrotter.Presentation.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.List;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Controllers.FirebaseStorageController;
import upc.fib.victor.globetrotter.Controllers.GlideApp;
import upc.fib.victor.globetrotter.Domain.Recommendation;
import upc.fib.victor.globetrotter.Presentation.Activities.InterestPointActivity;
import upc.fib.victor.globetrotter.R;

public class RecommendationRecyclerAdapter extends  RecyclerView.Adapter<RecommendationRecyclerAdapter.RecommendationViewHolder> {

    public Context context;
    private String uid;
    private ArrayList<String> recommendationsIds;

    private FirebaseDatabaseController firebaseDatabaseController;
    private FirebaseStorageController firebaseStorageController;


    public static class RecommendationViewHolder extends RecyclerView.ViewHolder  {

        public Recommendation recommendation;
        public ImageView userImg;
        public TextView userNameTxt;
        public TextView commentTxt;
        public TextView dateTxt;
        public TextView placeNameTxt;
        public TextView errorTxt;
        public ProgressBar progressBar;
        public RelativeLayout recommendationLayout;

        public RecommendationViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImage);
            commentTxt = itemView.findViewById(R.id.commentTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            errorTxt = itemView.findViewById(R.id.errorTxt);
            progressBar = itemView.findViewById(R.id.recommendationProgressBar);
            recommendationLayout = itemView.findViewById(R.id.recommendationLayout);
            placeNameTxt = itemView.findViewById(R.id.placeNameTxt);
        }
    }

    public RecommendationRecyclerAdapter(Context context, ArrayList<String> recommendationsIds, String uid) {
        this.uid = uid;
        this.context = context;
        this.recommendationsIds = recommendationsIds;

        Log.d("INTEEEEREST", String.valueOf(recommendationsIds.size()));
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
        firebaseStorageController = FirebaseStorageController.getInstance();
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation, parent, false);

        RecommendationViewHolder holder = new RecommendationViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecommendationViewHolder holder, int position) {

        holder.errorTxt.setVisibility(View.GONE);
        holder.userNameTxt.setVisibility(View.GONE);
        holder.commentTxt.setVisibility(View.GONE);
        holder.dateTxt.setVisibility(View.GONE);
        holder.placeNameTxt.setVisibility(View.GONE);

        holder.progressBar.setVisibility(View.VISIBLE);

        firebaseDatabaseController.getRecommendation(recommendationsIds.get(position), new FirebaseDatabaseController.GetRecommendationResponse() {
            @Override
            public void success(Recommendation recommendation) {
                holder.recommendation = recommendation;

                firebaseStorageController.loadImageToView("profiles/" + holder.recommendation.getUid() + ".jpg", new FirebaseStorageController.GetImageResponse() {
                    @Override
                    public void load(StorageReference ref) {
                        GlideApp.with(context)
                                .load(ref)
                                .placeholder(context.getResources().getDrawable(R.drawable.silueta))
                                .into(holder.userImg);
                    }
                });

                holder.userNameTxt.setText(holder.recommendation.getUserName());
                holder.placeNameTxt.setText(holder.recommendation.getInteresPointName());
                holder.commentTxt.setText(holder.recommendation.getComment());

                DateFormat dataFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                String date = dataFormat.format(holder.recommendation.getDate());
                holder.dateTxt.setText(date);

                holder.recommendationLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent interestPointIntent = new Intent(context, InterestPointActivity.class);
                        interestPointIntent.putExtra("uid", holder.recommendation.getUid());
                        interestPointIntent.putExtra("idPlace", holder.recommendation.getIdInterestPoint());
                        context.startActivity(interestPointIntent);
                    }
                });
            }

            @Override
            public void noRecommendation() {
                holder.progressBar.setVisibility(View.GONE);
                holder.errorTxt.setVisibility(View.VISIBLE);
                holder.errorTxt.setText("Error obteniendo la recomendación");
            }

            @Override
            public void error(String message) {
                holder.progressBar.setVisibility(View.GONE);
                holder.errorTxt.setVisibility(View.VISIBLE);
                holder.errorTxt.setText("Error obteniendo la recomendación");
            }
        });


        holder.progressBar.setVisibility(View.GONE);

        holder.errorTxt.setVisibility(View.VISIBLE);
        holder.userNameTxt.setVisibility(View.VISIBLE);
        holder.commentTxt.setVisibility(View.VISIBLE);
        holder.dateTxt.setVisibility(View.VISIBLE);
        holder.placeNameTxt.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return recommendationsIds.size();
    }
}
