package upc.fib.victor.globetrotter.Presentation.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Controllers.FirebaseStorageController;
import upc.fib.victor.globetrotter.Controllers.GlideApp;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.Presentation.Activities.ProfileActivity;
import upc.fib.victor.globetrotter.R;

public class FollowUsersRecyclerAdapter extends RecyclerView.Adapter<FollowUsersRecyclerAdapter.ProfileViewHolder> {

    private ArrayList<String> idsProfiles;
    private Context context;
    private FirebaseStorageController firebaseStorageController;
    private FirebaseDatabaseController firebaseDatabaseController;

    private Activity parentActivity;

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout profileLayout;
        public ImageView userImg;
        public TextView userName;
        public Profile profile;
        public TextView errorTxt;
        public ProgressBar progressBar;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userNameTxt);
            profileLayout = itemView.findViewById(R.id.itemLayout);
            errorTxt = itemView.findViewById(R.id.errorTxt);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    public FollowUsersRecyclerAdapter(Context context, ArrayList<String> idsProfiles, Activity parentActivity) {
        this.parentActivity = parentActivity;
        firebaseStorageController = FirebaseStorageController.getInstance();
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
        this.context = context;
        this.idsProfiles = idsProfiles;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile, parent, false);

        ProfileViewHolder holder = new ProfileViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileViewHolder holder, int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.errorTxt.setVisibility(View.GONE);
        holder.userName.setVisibility(View.GONE);

        firebaseDatabaseController.getProfile(idsProfiles.get(position), new FirebaseDatabaseController.GetProfileResponse() {
            @Override
            public void success(Profile profile) {
                holder.profile = profile;
                firebaseStorageController.loadImageToView("profiles/" + holder.profile.getUid() + ".jpg", new FirebaseStorageController.GetImageResponse() {
                    @Override
                    public void load(StorageReference ref) {
                        GlideApp.with(context)
                                .load(ref)
                                .placeholder(context.getResources().getDrawable(R.drawable.silueta))
                                .into(holder.userImg);
                    }
                });
                holder.userName.setText(holder.profile.getNombreCompleto());
                holder.profileLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(context, ProfileActivity.class);
                        profileIntent.putExtra("uidOwner", holder.profile.getUid());
                        context.startActivity(profileIntent);
                        parentActivity.finish();
                    }
                });
                holder.progressBar.setVisibility(View.GONE);
                holder.errorTxt.setVisibility(View.GONE);
                holder.userName.setVisibility(View.VISIBLE);
            }

            @Override
            public void notFound() {
                holder.errorTxt.setText("Usuario no encontrado.");

                holder.progressBar.setVisibility(View.GONE);
                holder.errorTxt.setVisibility(View.VISIBLE);
                holder.userName.setVisibility(View.GONE);
                holder.userImg.setVisibility(View.GONE);
            }

            @Override
            public void error(String message) {
                holder.errorTxt.setText("Error accediendo a los datos, vuelve a intentarlo");

                holder.progressBar.setVisibility(View.GONE);
                holder.errorTxt.setVisibility(View.VISIBLE);
                holder.userName.setVisibility(View.GONE);
                holder.userImg.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return idsProfiles.size();
    }

}
