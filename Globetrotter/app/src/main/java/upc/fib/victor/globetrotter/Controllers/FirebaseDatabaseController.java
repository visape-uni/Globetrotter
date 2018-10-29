package upc.fib.victor.globetrotter.Controllers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import upc.fib.victor.globetrotter.Domain.Profile;

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

    public void storeProfile (Profile profile, final StoreUserResponse storeUserResponse) {
        db.collection("profiles")
                .document(profile.getUid())
                .set(profile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        storeUserResponse.success();
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
        DocumentReference docRef = db.collection("profiles").document(uid);

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

    public interface GetProfileResponse {
        void success(Profile profile);
        void notFound();
        void error(String message);
    }
}
