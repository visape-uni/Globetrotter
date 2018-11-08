package upc.fib.victor.globetrotter.Controllers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.Domain.Publication;

import static android.support.constraint.Constraints.TAG;

public class FirebaseDatabaseController {

    private static FirebaseDatabaseController instance;

    private FirebaseFirestore db;

    private FirebaseDatabaseController() {
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseDatabaseController getInstance() {
        if (instance == null) instance = new FirebaseDatabaseController();
        return instance;
    }

    public void storePublication(final Publication publication, final StorePublicationResponse storePublicationResponse) {
        final CollectionReference refUserFollowers = db.collection("perfiles").document(publication.getUidUser()).collection("seguidores");

        final DocumentReference refPublication = db.collection("publicaciones").document();

        final String id = refPublication.getId();
        publication.setId(id);


        refUserFollowers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    refPublication.set(publication).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            storePublicationResponse.success();
                            //TODO: HACER ASYNCRON
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                setRelationPublication(document.getId(), publication.getId(), publication.getUidUser());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("DEBUG: ", e.toString());
                            storePublicationResponse.error();
                        }
                    });
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void setRelationPublication(String uidFollower, String idPublication, String idOwner) {
        Map<String,String> mapPublicacion = new HashMap<>();
        mapPublicacion.put("ID Dueño", idOwner);
        db.collection("perfiles")
                .document(uidFollower)
                .collection("publicacionesSiguiendo")
                .document(idPublication)
                .set(mapPublicacion);
    }

    public void setRelationFollower (String uid, String idFollower) {
        Map<String,String> map = new HashMap<>();
        map.put("ID Dueño", idFollower);
        db.collection("perfiles")
                .document(uid)
                .collection("seguidores")
                .document(idFollower)
                .set(map);
    }

    public void setRelationFollowing (String uid, String idFollowing) {
        Map<String,String> map = new HashMap<>();
        map.put("ID Dueño", idFollowing);
        db.collection("perfiles")
                .document(uid)
                .collection("siguiendo")
                .document(idFollowing)
                .set(map);
    }

    public void editProfile() {
        //TODO: IMPLEMENTAR
    }

    public void storeProfile (final Profile profile, final StoreUserResponse storeUserResponse) {
        db.collection("perfiles")
                .document(profile.getUid())
                .set(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        storeUserResponse.success();
                        setRelationFollower(profile.getUid(), profile.getUid());
                        setRelationFollowing(profile.getUid(), profile.getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        storeUserResponse.error();
                    }
                });
    }

    public void getProfile(String uid, final GetProfileResponse getProfileResponse) {
        DocumentReference docRef = db.collection("perfiles").document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        getProfileResponse.success(document.toObject(Profile.class));
                    } else {
                        getProfileResponse.notFound();
                    }
                } else {
                    getProfileResponse.error(task.getException().getMessage());
                }
            }
        });
    }

    public interface StoreUserResponse {
        void success();
        void error();
    }

    public interface StorePublicationResponse {
        void success();
        void error();
    }

    public interface GetProfileResponse {
        void success(Profile profile);
        void notFound();
        void error(String message);
    }
}
