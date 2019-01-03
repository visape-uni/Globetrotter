package upc.fib.victor.globetrotter.Presentation.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
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
    private String uid;
    private ArrayList<String> publicationIds;

    public static class CommentAdapterViewHolder extends RecyclerView.ViewHolder {
        public Publication publication;
        public ImageView userImg;
        public TextView userNameTxt;
        public TextView publicationTxt;
        public TextView dateTxt;
        public ImageView likeImg;
        public ImageView deleteIcon;

        public CommentAdapterViewHolder(View itemView) {
            super(itemView);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            userImg = itemView.findViewById(R.id.userImage);
            publicationTxt = itemView.findViewById(R.id.publicationTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            likeImg = itemView.findViewById(R.id.likesImg);
            deleteIcon = itemView.findViewById(R.id.ic_delete_publication);
            likeImg.setVisibility(View.GONE);

            deleteIcon.setClickable(true);
        }
    }

    public CommentAdapter(Context context, ArrayList<String> publicationIds, String uid) {
        this.context = context;
        this.publicationIds = publicationIds;
        this.uid = uid;
        firebaseStorageController = FirebaseStorageController.getInstance(context);
        firebaseDatabaseController = FirebaseDatabaseController.getInstance(context);
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
                holder.userImg.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.comment_image_height);
                holder.userImg.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.comment_image_width);

                RelativeLayout.LayoutParams layoutParamsUserName = (RelativeLayout.LayoutParams) holder.userNameTxt.getLayoutParams();
                layoutParamsUserName.topMargin = (int) context.getResources().getDimension(R.dimen.username_margin_top);
                holder.userNameTxt.setLayoutParams(layoutParamsUserName);

                RelativeLayout.LayoutParams layoutParamsComment = (RelativeLayout.LayoutParams) holder.publicationTxt.getLayoutParams();
                layoutParamsComment.topMargin = (int) context.getResources().getDimension(R.dimen.comment_margin);
                layoutParamsComment.bottomMargin = (int) context.getResources().getDimension(R.dimen.comment_margin);
                holder.publicationTxt.setLayoutParams(layoutParamsComment);

                RelativeLayout.LayoutParams layoutParamsDate = (RelativeLayout.LayoutParams) holder.dateTxt.getLayoutParams();
                layoutParamsDate.bottomMargin = 0;
                holder.dateTxt.setLayoutParams(layoutParamsDate);

                if (holder.publication.getUidUser().equals(uid)) {
                    holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (holder.publication.getParentId() != null) {

                                final ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Eliminando comentario...");
                                progressDialog.show();

                                firebaseDatabaseController.deleteComment(holder.publication.getParentId(), holder.publication.getId(), new FirebaseDatabaseController.DeletePublicationResponse() {
                                    @Override
                                    public void success() {
                                        publicationIds.remove(holder.publication.getId());
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Publicación eliminada correctamente", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void error() {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Error borrando publicación, prueba más tarde", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    holder.deleteIcon.setVisibility(View.GONE);
                }

                firebaseDatabaseController.getPictureTimestamp(holder.publication.getUidUser(), new FirebaseDatabaseController.GetPictureTimestampResponse() {
                    @Override
                    public void success(final Long time) {
                        if (time != null) {
                            firebaseStorageController.loadImageToView("profiles/" + holder.publication.getUidUser() + ".jpg", new FirebaseStorageController.GetImageResponse() {
                                @Override
                                public void load(StorageReference ref) {
                                    GlideApp.with(context)
                                            .load(ref)
                                            .signature(new ObjectKey(time))
                                            .placeholder(context.getResources().getDrawable(R.drawable.silueta))
                                            .into(holder.userImg);
                                }
                            });
                        }else {
                            holder.userImg.setImageDrawable(context.getResources().getDrawable(R.drawable.silueta));
                        }
                    }

                    @Override
                    public void error() {
                        Toast.makeText(context, "Error cargando imagen", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.userNameTxt.setText(holder.publication.getUserName());
                holder.publicationTxt.setText(holder.publication.getMessage());
                holder.publicationTxt.setGravity(Gravity.RIGHT);

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
