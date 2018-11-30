package upc.fib.victor.globetrotter.Presentation.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentAdapterViewHolder> {

    private FirebaseStorageController firebaseStorageController;
    private FirebaseDatabaseController firebaseDatabaseController;
    private Context context;
    private ArrayList<String> publicationIds;

    public static class CommentAdapterViewHolder extends RecyclerView.ViewHolder {
        public Publication publication;
        public ImageView userImg;
        public TextView userNameTxt;
        public TextView publicationTxt;
        public TextView dateTxt;
        public ImageView likeImg;

        public CommentAdapterViewHolder(View itemView) {
            super(itemView);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            userImg = itemView.findViewById(R.id.userImage);
            publicationTxt = itemView.findViewById(R.id.publicationTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            likeImg = itemView.findViewById(R.id.likesImg);
            likeImg.setVisibility(View.GONE);
        }
    }

    public CommentAdapter(Context context, ArrayList<String> publicationIds) {
        this.context = context;
        this.publicationIds = publicationIds;
        firebaseStorageController = FirebaseStorageController.getInstance();
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
    }

    @NonNull
    @Override
    public CommentAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.publication, parent, false);

        CommentAdapterViewHolder holder = new CommentAdapterViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapterViewHolder holder, int position) {
        firebaseDatabaseController.getPublication(publicationIds.get(position), new FirebaseDatabaseController.GetPublicationResponse() {
            @Override
            public void success(Publication publication) {
                holder.publication = publication;
                firebaseStorageController.loadImageToView("profiles/" + holder.publication.getUidUser() + ".jpg", new FirebaseStorageController.GetImageResponse() {
                    @Override
                    public void load(StorageReference ref) {
                        GlideApp.with(context)
                                .load(ref)
                                .placeholder(context.getResources().getDrawable(R.drawable.silueta))
                                .into(holder.userImg);
                    }
                });

                holder.userNameTxt.setText(holder.publication.getUserName());
                holder.publicationTxt.setText(holder.publication.getMessage());

                DateFormat dataFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                String date = dataFormat.format(holder.publication.getDate());
                holder.dateTxt.setText(date);
            }

            @Override
            public void error(String message) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return publicationIds.size();
    }
}
