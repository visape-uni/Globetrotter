package upc.fib.victor.globetrotter.Presentation.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import upc.fib.victor.globetrotter.Domain.Recommendation;
import upc.fib.victor.globetrotter.R;

public class RecommendationRecyclerAdapter extends  RecyclerView.Adapter<RecommendationRecyclerAdapter.RecommendationViewHolder> {

    public Context context;
    private String uid;
    private ArrayList<Recommendation> recommendations;


    public static class RecommendationViewHolder extends RecyclerView.ViewHolder  {

        public Recommendation recommendation;
        public ImageView userImg;
        public TextView userNameTxt;
        public TextView commentTxt;
        public TextView dateTxt;
        public TextView errorTxt;
        public ProgressBar progressBar;

        public RecommendationViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImage);
            commentTxt = itemView.findViewById(R.id.commentTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            errorTxt = itemView.findViewById(R.id.errorTxt);
            progressBar = itemView.findViewById(R.id.recommendationProgressBar);
        }
    }

    public RecommendationRecyclerAdapter(Context context, ArrayList<Recommendation> recommendations, String uid) {
        this.uid = uid;
        this.context = context;
        this.recommendations = recommendations;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation, parent, false);

        RecommendationViewHolder holder = new RecommendationViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {

        holder.errorTxt.setVisibility(View.GONE);
        holder.userNameTxt.setVisibility(View.GONE);
        holder.commentTxt.setVisibility(View.GONE);
        holder.dateTxt.setVisibility(View.GONE);

        holder.progressBar.setVisibility(View.VISIBLE);




        holder.progressBar.setVisibility(View.GONE);

        holder.errorTxt.setVisibility(View.VISIBLE);
        holder.userNameTxt.setVisibility(View.VISIBLE);
        holder.commentTxt.setVisibility(View.VISIBLE);
        holder.dateTxt.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }
}
