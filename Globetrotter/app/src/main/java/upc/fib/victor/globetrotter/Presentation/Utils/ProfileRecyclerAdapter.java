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

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ProfileViewHolder> {

    private ArrayList<Profile> profiles;
    private Context context;
    private FirebaseStorageController firebaseStorageController;

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout profileLayout;
        public ImageView userImg;
        public TextView userName;
        public Profile profile;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userNameTxt);
            profileLayout = itemView.findViewById(R.id.itemLayout);
        }
    }

    public ProfileRecyclerAdapter (Context context, ArrayList<Profile> profiles) {
        firebaseStorageController = FirebaseStorageController.getInstance();
        this.context = context;
        this.profiles = profiles;
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
        holder.profile = profiles.get(position);
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void addItem (Profile profile) {
        profiles.add(profile);
        notifyDataSetChanged();
    }

    public void deleteItem (Profile profile) {
        profiles.remove(profile);
        notifyDataSetChanged();
    }
}
