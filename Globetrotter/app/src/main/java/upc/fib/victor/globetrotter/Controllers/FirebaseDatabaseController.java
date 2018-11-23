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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import upc.fib.victor.globetrotter.Domain.DiaryPage;
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

    public void setCountryVisited(String uid, String country, String idCountry) {
        Map<String,String> map = new HashMap<>();
        map.put("ID Ciudad", idCountry);
        db.collection("perfiles")
                .document(uid)
                .collection("paisesVisitados")
                .document(country)
                .set(map);

        final DocumentReference docRef = db.collection("perfiles").document(uid);
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);
                int newNumPaises = (Integer) snapshot.get("numPaises") + 1;
                transaction.update(docRef, "numPaises", newNumPaises);

                return null;
            }
        });
    }

    public void deleteCountryVisited(String uid, String country) {
        db.collection("perfiles")
                .document(uid)
                .collection("paisesVisitados")
                .document(country)
                .delete();
        final DocumentReference docRef = db.collection("perfiles").document(uid);
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);
                int newNumPaises = (Integer) snapshot.get("numPaises") - 1;
                transaction.update(docRef, "numPaises", newNumPaises);

                return null;
            }
        });
    }

    public void getCountriesVisited(String uid, final GetCountriesResponse getCountriesResponse) {
        CollectionReference refPaisesVisitados = db.collection("perfiles")
                .document(uid)
                .collection("paisesVisitados");

        refPaisesVisitados.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap<String, String> countries = new HashMap<>();
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        countries.put(document.getId(), document.getString("ID Ciudad"));
                    }
                    getCountriesResponse.success(countries);
                } else {
                    getCountriesResponse.error();
                }
            }
        });
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

    public void editProfile(final Profile newProfile, final EditProfileResponse editProfileResponse) {
        final DocumentReference refProfile = db.collection("perfiles").document(newProfile.getUid());

        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot documentProfile = transaction.get(refProfile);

                if (documentProfile.exists()) {
                    Profile oldProfile = documentProfile.toObject(Profile.class);

                    StorePublicationResponse response = new StorePublicationResponse() {
                        @Override
                        public void success() {
                            Log.d("DatabaseController", "Published");
                        }

                        @Override
                        public void error() {
                            Log.d("DatabaseController", "Not Published");
                        }
                    };

                    Map<String, Object> mapChanges = new HashMap<>();
                    if (!oldProfile.getNombre().equals(newProfile.getNombre())) {
                        mapChanges.put("nombre", newProfile.getNombre());

                        String message = newProfile.getNombreCompleto() + " ha cambiado su nombre de '" + oldProfile.getNombre() + "' a '" + newProfile.getNombre() + "'.";
                        Publication publication = new Publication(newProfile.getUid(), message, Calendar.getInstance().getTime());
                        storePublication(publication, response);
                    }
                    if(!oldProfile.getApellidos().equals(newProfile.getApellidos())) {
                        mapChanges.put("apellidos", newProfile.getApellidos());

                        String message = newProfile.getNombreCompleto() + " ha cambiado su apellido de '" + oldProfile.getApellidos() + "' a '" + newProfile.getApellidos() + "'.";
                        Publication publication = new Publication(newProfile.getUid(), message, Calendar.getInstance().getTime());
                        storePublication(publication, response);
                    }
                    if(!oldProfile.getDescripcion().equals(newProfile.getDescripcion())) {
                        mapChanges.put("descripcion", newProfile.getDescripcion());

                        String message = newProfile.getNombreCompleto() + " ha cambiado su descripción.";
                        Publication publication = new Publication(newProfile.getUid(), message, Calendar.getInstance().getTime());
                        storePublication(publication, response);
                    }
                    if(!oldProfile.getNacimiento().equals(newProfile.getNacimiento())) {
                        mapChanges.put("nacimiento", newProfile.getNacimiento());

                        String message = newProfile.getNombreCompleto() + " ha cambiado su fecha de nacimiento de '" + oldProfile.getNacimiento() + "' a '" + newProfile.getNacimiento() + "'.";
                        Publication publication = new Publication(newProfile.getUid(), message, Calendar.getInstance().getTime());
                        storePublication(publication, response);
                    }

                    transaction.update(refProfile, mapChanges);
                } else {
                    editProfileResponse.notFound();
                }
                return null;
            }

        });
        editProfileResponse.success();
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

    public void storePage(String uid, DiaryPage diaryPage, final StorePageResponse storePageResponse) {
        db.collection("perfiles")
                .document(uid)
                .collection("diario")
                .document(String.valueOf(diaryPage.getDateModified().getTime()))
                .set(diaryPage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        storePageResponse.success();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        storePageResponse.error();
                    }
                });
    }

    public void getPage (String uid, String pageId, final GetPageResponse getPageResponse) {
        DocumentReference docRef = db.collection("perfiles").document(uid).collection("diario").document(pageId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        getPageResponse.success(document.toObject(DiaryPage.class));
                    } else {
                        getPageResponse.notFound();
                    }
                } else {
                    getPageResponse.error(task.getException().getMessage());
                }
            }
        });
    }

    public void deletePage(String uid, String pageId) {
        db.collection("perfiles")
                .document(uid)
                .collection("diario")
                .document(pageId)
                .delete();
    }

    public void getUserPages (String uid, final GetUserPagesResponse getUserPagesResponse) {
        CollectionReference refUserPages = db.collection("perfiles")
                .document(uid)
                .collection("diario");

        refUserPages.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap<String, String> pages = new HashMap<>();
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        pages.put(document.getId(), document.getString("title"));
                    }
                    getUserPagesResponse.success(pages);
                } else {
                    getUserPagesResponse.error();
                }
            }
        });
    }

    public interface GetUserPagesResponse {
        void success (HashMap<String, String> pages);
        void error();
    }

    public interface StorePageResponse {
        void success();
        void error();
    }

    public interface GetPageResponse {
        void success(DiaryPage diaryPage);
        void notFound();
        void error(String message);
    }

    public interface StoreUserResponse {
        void success();
        void error();
    }

    public interface EditProfileResponse {
        void success();
        void notFound();
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

    public interface GetCountriesResponse {
        void success(HashMap<String, String> countries);
        void error();
    }
}
