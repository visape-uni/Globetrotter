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

import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Controllers.FirebaseStorageController;
import upc.fib.victor.globetrotter.Controllers.GlideApp;
import upc.fib.victor.globetrotter.Domain.Publication;
import upc.fib.victor.globetrotter.R;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.PublicationViewHolder> {

    private Context context;
    private ArrayList<Publication> publications;
    private ArrayList<String> publicationIds;
    private FirebaseStorageController firebaseStorageController;
    private FirebaseDatabaseController firebaseDatabaseController;


    public static class PublicationViewHolder extends RecyclerView.ViewHolder {
        public TextView errorTxt;
        public ProgressBar progressBar;
        public ImageView userImg;
        public TextView userNameTxt;
        public TextView publicationTxt;
        public TextView dateTxt;
        public TextView likesTxt;
        public TextView commentsTxt;

        public PublicationViewHolder(View itemView) {
            super(itemView);
            errorTxt = itemView.findViewById(R.id.error);
            progressBar = itemView.findViewById(R.id.publicationProgressBar);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            userImg = itemView.findViewById(R.id.userImage);
            publicationTxt = itemView.findViewById(R.id.publicationTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            likesTxt = itemView.findViewById(R.id.likesTxt);
            commentsTxt = itemView.findViewById(R.id.comentariosTxt);
        }
    }

    public PublicationAdapter(Context context, ArrayList<String> publicationIds) {
        this.context = context;
        this.publicationIds = publicationIds;
        firebaseStorageController = FirebaseStorageController.getInstance();
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
    }

    @NonNull
    @Override
    public PublicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.publication, parent, false);

        PublicationViewHolder holder = new PublicationViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PublicationViewHolder holder, int position) {

        holder.errorTxt.setVisibility(View.GONE);
        holder.userNameTxt.setVisibility(View.GONE);
        holder.publicationTxt.setVisibility(View.GONE);
        holder.dateTxt.setVisibility(View.GONE);
        holder.likesTxt.setVisibility(View.GONE);
        holder.commentsTxt.setVisibility(View.GONE);
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.progressBar.setIndeterminate(true);

        firebaseDatabaseController.getPublication(publicationIds.get(position), new FirebaseDatabaseController.GetPublicationResponse() {
            @Override
            public void success(Publication publication) {

                /*TODO: QUITAR ESTO!!!
                publications.add(publication);*/

                firebaseStorageController.loadImageToView("profiles/" + publication.getUidUser() + ".jpg", new FirebaseStorageController.GetImageResponse() {
                    @Override
                    public void load(StorageReference ref) {
                        GlideApp.with(context)
                                .load(ref)
                                .placeholder(context.getResources().getDrawable(R.drawable.silueta))
                                .into(holder.userImg);
                    }
                });

                holder.userNameTxt.setText(publication.getUserName());
                holder.publicationTxt.setText(publication.getMessage());

                DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
                String date = dataFormat.format(publication.getDate());
                holder.dateTxt.setText(date);
                holder.likesTxt.setText(String.format("%d Me gusta", publication.getUidLikes().size()));
                holder.commentsTxt.setText(String.format("%d Comentarios", publication.getAnswers().size()));

                holder.progressBar.setVisibility(View.GONE);
                holder.userNameTxt.setVisibility(View.VISIBLE);
                holder.publicationTxt.setVisibility(View.VISIBLE);
                holder.dateTxt.setVisibility(View.VISIBLE);
                holder.likesTxt.setVisibility(View.VISIBLE);
                holder.commentsTxt.setVisibility(View.VISIBLE);
            }

            @Override
            public void error(String message) {
                holder.progressBar.setVisibility(View.GONE);
                holder.errorTxt.setVisibility(View.VISIBLE);
                holder.errorTxt.setText("Error obteniendo la publicación: " + message);
            }
        });


    }

    @Override
    public int getItemCount() {
        return publicationIds.size();
    }
}
