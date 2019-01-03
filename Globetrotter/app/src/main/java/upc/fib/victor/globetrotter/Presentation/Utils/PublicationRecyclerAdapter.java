package upc.fib.victor.globetrotter.Presentation.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Controllers.FirebaseStorageController;
import upc.fib.victor.globetrotter.Controllers.GlideApp;
import upc.fib.victor.globetrotter.Domain.Publication;
import upc.fib.victor.globetrotter.Presentation.Activities.ProfileActivity;
import upc.fib.victor.globetrotter.R;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class PublicationRecyclerAdapter extends RecyclerView.Adapter<PublicationRecyclerAdapter.PublicationViewHolder> {

    private Context context;
    private ProfileActivity profileActivity;
    private String uid;
    private ArrayList<String> publicationIds;
    private FirebaseStorageController firebaseStorageController;
    private FirebaseDatabaseController firebaseDatabaseController;


    public static class PublicationViewHolder extends RecyclerView.ViewHolder {

        public Publication publication;
        public ImageView deleteIcon;
        public ImageView likeImg;
        public LinearLayout likeLayout;
        public TextView errorTxt;
        public ProgressBar progressBar;
        public ImageView userImg;
        public TextView userNameTxt;
        public TextView publicationTxt;
        public TextView dateTxt;
        public TextView likesTxt;
        public TextView commentsTxt;
        public ImageView publicationImg;


        public RecyclerView commentsRecycledView;
        public CommentAdapter commentAdapter;
        private RecyclerView.LayoutManager mLayoutManager;

        public TextInputEditText commentInput;
        public FloatingActionButton sendButton;
        public TextInputLayout textInputLayout;

        public PublicationViewHolder(View itemView, Context context) {
            super(itemView);
            deleteIcon = itemView.findViewById(R.id.ic_delete_publication);
            likeLayout = itemView.findViewById(R.id.likes);
            likeImg = itemView.findViewById(R.id.likesImg);
            errorTxt = itemView.findViewById(R.id.error);
            progressBar = itemView.findViewById(R.id.publicationProgressBar);
            userNameTxt = itemView.findViewById(R.id.userNameTxt);
            userImg = itemView.findViewById(R.id.userImage);
            publicationTxt = itemView.findViewById(R.id.publicationTxt);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            likesTxt = itemView.findViewById(R.id.likesTxt);
            commentsTxt = itemView.findViewById(R.id.comentariosTxt);
            publicationImg = itemView.findViewById(R.id.publicationImg);
            publication = new Publication();

            deleteIcon.setClickable(true);
            likeLayout.setClickable(true);
            commentsTxt.setClickable(true);

            textInputLayout = itemView.findViewById(R.id.textInputLayout);
            commentsRecycledView = itemView.findViewById(R.id.comments_recycler_view);
            commentInput = itemView.findViewById(R.id.comment_input);
            sendButton = itemView.findViewById(R.id.send_btn);


            mLayoutManager = new LinearLayoutManager(context);
            commentsRecycledView.setLayoutManager(mLayoutManager);
        }
    }

    public PublicationRecyclerAdapter(Context context, ArrayList<String> publicationIds, String uid) {
        this.context = context;
        this.profileActivity = (ProfileActivity) context;
        this.publicationIds = publicationIds;
        firebaseStorageController = FirebaseStorageController.getInstance(context);
        firebaseDatabaseController = FirebaseDatabaseController.getInstance(context);
        this.uid = uid;
    }

    @NonNull
    @Override
    public PublicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.publication, parent, false);

        PublicationViewHolder holder = new PublicationViewHolder(v, context);

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

        holder.textInputLayout.setVisibility(View.GONE);
        holder.commentsRecycledView.setVisibility(View.GONE);
        holder.sendButton.setVisibility(View.GONE);
        holder.commentInput.setVisibility(View.GONE);

        holder.progressBar.setVisibility(View.VISIBLE);
        holder.progressBar.setIndeterminate(true);

        firebaseDatabaseController.getPublication(publicationIds.get(position), new FirebaseDatabaseController.GetPublicationResponse() {
            @Override
            public void success(final Publication publication) {
                if (publication != null) {
                    holder.publication = publication;

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
                            }
                        }

                        @Override
                        public void error() {
                            Toast.makeText(context, "Error cargando imagen", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (holder.publication.getHasImg() != null && holder.publication.getHasImg()) {
                        firebaseStorageController.loadImageToView("publication/" + holder.publication.getId() + ".jpg", new FirebaseStorageController.GetImageResponse() {
                            @Override
                            public void load(StorageReference ref) {
                                GlideApp.with(context)
                                        .load(ref)
                                        .signature(new ObjectKey(ref.getMetadata()))
                                        .into(holder.publicationImg);
                            }
                        });
                    } else {
                        holder.publicationImg.setImageDrawable(null);
                    }

                    holder.userNameTxt.setText(holder.publication.getUserName());
                    holder.publicationTxt.setText(holder.publication.getMessage());

                    DateFormat dataFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                    String date = dataFormat.format(holder.publication.getDate());
                    holder.dateTxt.setText(date);
                    if (holder.publication.getUidLikes().contains(uid)) {
                        holder.likeImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                    } else {
                        holder.likeImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_no_like));
                    }
                    holder.likesTxt.setText(String.format("%d Me gusta", holder.publication.getUidLikes().size()));
                    holder.commentsTxt.setText(String.format("%d Comentarios", holder.publication.getAnswers().size()));

                    //Like On Click Listener
                    holder.likesTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Inserta o elimina like
                            firebaseDatabaseController.likePublication(holder.publication.getId(), uid);

                            //Holder publication no tiene el like añadido o quitado aun
                            if (holder.publication.getUidLikes().contains(uid)) {
                                holder.likeImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_no_like));
                                holder.publication.removeLike(uid);
                            } else {
                                holder.likeImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                                holder.publication.addLike(uid);
                            }
                            holder.likesTxt.setText(String.format("%d Me gusta", holder.publication.getUidLikes().size()));
                        }
                    });

                    //Comments On Click Listener
                    holder.commentsTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.commentAdapter = new CommentAdapter(context, holder.publication.getAnswers(), uid);
                            holder.commentsRecycledView.setAdapter(holder.commentAdapter);

                            holder.sendButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    firebaseDatabaseController.getUserName(uid, new FirebaseDatabaseController.GetUserNameResponse() {
                                        @Override
                                        public void success(String userName) {
                                            Publication comment = new Publication(uid, userName, holder.commentInput.getText().toString(), Calendar.getInstance().getTime());
                                            firebaseDatabaseController.commentPublication(holder.publication.getId(), comment, new FirebaseDatabaseController.CommentPublicationResponse() {
                                                @Override
                                                public void success(String publicationId) {

                                                    holder.publication.addComment(publicationId);
                                                    holder.commentAdapter.notifyDataSetChanged();
                                                    holder.commentsTxt.setText(String.format("%d Comentarios", holder.publication.getAnswers().size()));

                                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                                                    imm.hideSoftInputFromWindow(holder.commentInput.getWindowToken(), 0);
                                                    holder.commentInput.setText("");
                                                }
                                            });
                                        }

                                        @Override
                                        public void error() {
                                            //Error
                                            Log.d("PublicationRecyclerAptr", "error getting userName");
                                        }
                                    });
                                }
                            });

                            holder.textInputLayout.setVisibility(View.VISIBLE);
                            holder.commentsRecycledView.setVisibility(View.VISIBLE);
                            holder.sendButton.setVisibility(View.VISIBLE);
                            holder.commentInput.setVisibility(View.VISIBLE);
                        }
                    });

                    holder.userImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            profileActivity.reload(holder.publication.getUidUser());
                        }
                    });

                    holder.userNameTxt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            profileActivity.reload(holder.publication.getUidUser());
                        }
                    });

                    //Delete On Click Listener
                    if (holder.publication.getUidUser().equals(uid)) {

                        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final ProgressDialog progressDialog = new ProgressDialog(context);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Eliminando publicación...");
                                progressDialog.show();

                                if (holder.publication.getHasImg() != null && holder.publication.getHasImg()) {
                                    firebaseStorageController.deletePublicationImage(holder.publication.getId(), new FirebaseStorageController.DeleteImageResponse() {
                                        @Override
                                        public void success() {
                                            firebaseDatabaseController.deletePublication(holder.publication.getId(), holder.publication.getUidUser(), new FirebaseDatabaseController.DeletePublicationResponse() {
                                                @Override
                                                public void success() {
                                                    holder.publicationImg.setImageDrawable(null);
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

                                        @Override
                                        public void error(String message) {
                                            progressDialog.dismiss();
                                            Toast.makeText(context, "Error borrando publicación, prueba más tarde", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    firebaseDatabaseController.deletePublication(holder.publication.getId(), holder.publication.getUidUser(), new FirebaseDatabaseController.DeletePublicationResponse() {
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


                    holder.progressBar.setVisibility(View.GONE);
                    holder.userNameTxt.setVisibility(View.VISIBLE);
                    holder.publicationTxt.setVisibility(View.VISIBLE);
                    holder.dateTxt.setVisibility(View.VISIBLE);
                    holder.likesTxt.setVisibility(View.VISIBLE);
                    holder.commentsTxt.setVisibility(View.VISIBLE);
                }
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
