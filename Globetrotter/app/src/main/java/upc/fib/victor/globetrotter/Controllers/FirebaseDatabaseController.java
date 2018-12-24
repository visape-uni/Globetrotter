package upc.fib.victor.globetrotter.Controllers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import upc.fib.victor.globetrotter.Domain.DiaryPage;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.Domain.Publication;
import upc.fib.victor.globetrotter.Domain.Recommendation;
import upc.fib.victor.globetrotter.Domain.TripProposal;
import upc.fib.victor.globetrotter.Presentation.Activities.FollowersActivity;

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

    public void joinTrip (final String id, String uid, final JoinTripResponse joinTripResponse) {
        final DocumentReference refProfileTrip = db.collection("perfiles").document(uid).collection("viajesApuntado").document(id);
        DocumentReference refTrip = db.collection("propuestasViajes").document(id).collection("apuntados").document(uid);
        Map<String, String> map = new HashMap<>();
        map.put("ID Dueño", uid);
        refTrip.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Map<String,String> map1 = new HashMap<>();
                map1.put("ID viaje", id);
                refProfileTrip.set(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        joinTripResponse.success();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        joinTripResponse.error();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                joinTripResponse.error();
            }
        });
    }

    public void unjoinTrip (String id, String uid, final JoinTripResponse joinTripResponse) {
        final DocumentReference refProfileTrip = db.collection("perfiles").document(uid).collection("viajesApuntado").document(id);
        DocumentReference refTrip = db.collection("propuestasViajes").document(id).collection("apuntados").document(uid);

        refTrip.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                refProfileTrip.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        joinTripResponse.success();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        joinTripResponse.error();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                joinTripResponse.error();
            }
        });
    }

    public void getMembersOfTrip (String id, final GetMembersOfTripResponse getMembersOfTripResponse) {
        CollectionReference refTrip = db.collection("propuestasViajes").document(id).collection("apuntados");

        refTrip.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) getMembersOfTripResponse.noUsers();
                    else {
                        final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        String first = documents.get(0).getId();
                        String last = documents.get(documents.size()-1).getId();

                        final ArrayList<String> ids = new ArrayList<>();
                        for (DocumentSnapshot doc : documents) {
                            ids.add(doc.getId());
                        }

                        CollectionReference refProfiles = db.collection("perfiles");
                        refProfiles.whereGreaterThanOrEqualTo("uid", first).whereLessThanOrEqualTo("uid", last).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                ArrayList<Profile> users = new ArrayList<>();
                                for (QueryDocumentSnapshot documentAux : task.getResult()) {
                                    if (ids.contains(documentAux.getId())) {
                                        users.add(documentAux.toObject(Profile.class));
                                    }
                                }
                                getMembersOfTripResponse.success(users);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                getMembersOfTripResponse.error();
                            }
                        });
                    }
                } else {
                    getMembersOfTripResponse.error();
                }
            }
        });
    }

    public void searchTrip(String countryName, final SearchTripProposalResponse searchTripProposalResponse) {
        CollectionReference refTrips = db.collection("propuestasViajes");

        countryName = countryName.toUpperCase();

        refTrips.whereGreaterThanOrEqualTo("paisCapital", countryName).whereLessThanOrEqualTo("paisCapital", countryName+'Z').get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> idsProposals = new ArrayList<>();
                    if(task.getResult().isEmpty()) searchTripProposalResponse.noTrips();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        idsProposals.add(document.getId());
                    }
                    searchTripProposalResponse.success(idsProposals);
                } else {
                    searchTripProposalResponse.error();
                }
            }
        });
    }

    public void storeTripProposal(TripProposal tripProposal, final StorePublicationResponse storePublicationResponse) {
        DocumentReference refTripProposalAux = db.collection("propuestasViajes").document();

        String id = tripProposal.getDate().getTime() + refTripProposalAux.getId();
        tripProposal.setId(id);

        DocumentReference refTripProposal = db.collection("propuestasViajes").document(id);

        DocumentReference refProposals = db.collection("perfiles").document(tripProposal.getUidUser()).collection("propuestas").document(id);
        Map<String, String> map = new HashMap<>();
        map.put("ID Dueño", tripProposal.getUidUser());
        refProposals.set(map);

        refTripProposal.set(tripProposal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                storePublicationResponse.success();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                storePublicationResponse.error();
            }
        });

    }

    public void getTripProposal (String idTripProposal, final GetTripProposalResponse getTripProposalResponse) {
        DocumentReference refTrip = db.collection("propuestasViajes").document(idTripProposal);
        refTrip.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                getTripProposalResponse.success(documentSnapshot.toObject(TripProposal.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getTripProposalResponse.error(e.getMessage());
            }
        });

    }

    public void deleteTripProposal (String idPublication, String uidOwner, final DeletePublicationResponse deletePublicationResponse) {
        DocumentReference refProposal = db.collection("propuestasViajes").document(idPublication);
        final DocumentReference refMyProposal = db.collection("perfiles").document(uidOwner).collection("propuestas").document(idPublication);
        refProposal.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                refMyProposal.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deletePublicationResponse.success();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deletePublicationResponse.error();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deletePublicationResponse.error();
            }
        });
    }

    public void getIdsRecommendations (String uid, int limit, final String idRecomStart, final GetIdsRecommendationsResponse getIdsRecommendationsResponse) {
        CollectionReference refRecommendations = db.collection("perfiles").document(uid).collection("recomendaciones");
        if (idRecomStart.isEmpty()) {
            refRecommendations.orderBy("Date", Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<String> idsRecommendations = new ArrayList<>();
                        if (task.getResult().isEmpty()) getIdsRecommendationsResponse.noRecommendations();
                        else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idsRecommendations.add(document.getId());
                            }
                            getIdsRecommendationsResponse.success(idsRecommendations);
                        }
                    } else {
                        getIdsRecommendationsResponse.error();
                    }
                }
            });
        } else {
            refRecommendations.orderBy("Date", Query.Direction.DESCENDING).startAt(idRecomStart).limit(limit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<String> idsRecommendations = new ArrayList<>();
                        if (task.getResult().isEmpty()) getIdsRecommendationsResponse.noRecommendations();
                        else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idsRecommendations.add(document.getId());
                            }
                            getIdsRecommendationsResponse.success(idsRecommendations);
                        }
                    } else {
                        getIdsRecommendationsResponse.error();
                    }
                }
            });
        }
    }

    public void getRecommendation (String idRecommendation, final GetRecommendationResponse getRecommendationResponse) {
        DocumentReference docRef = db.collection("recomendaciones").document(idRecommendation);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                getRecommendationResponse.success(documentSnapshot.toObject(Recommendation.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getRecommendationResponse.error(e.getMessage());
            }
        });
    }

    public void getIdsMyInterestPoints (String uid, final GetIdsRecommendationsResponse getIdsRecommendationsResponse) {
        CollectionReference refInterest = db.collection("perfiles").document(uid).collection("misPuntosDeInteres");
        refInterest.orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> idsPoints = new ArrayList<>();
                    if (task.getResult().isEmpty()) getIdsRecommendationsResponse.noRecommendations();
                    else {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            idsPoints.add(document.getId());
                        }
                        getIdsRecommendationsResponse.success(idsPoints);
                    }
                } else {
                    getIdsRecommendationsResponse.error();
                }
            }
        });
    }

    public void storeRecommendation (final Recommendation recommendation, final StoreRecommendationResponse storeRecommendationResponse) {


        CollectionReference refUserFollowers = db.collection("perfiles").document(recommendation.getUid()).collection("seguidores");

        final String id = recommendation.getUid().concat(recommendation.getIdInterestPoint());

        final DocumentReference refInterestPoint = db.collection("perfiles").document(recommendation.getUid()).collection("misPuntosDeInteres").document(id);
        final DocumentReference refRecommendation = db.collection("recomendaciones").document(id);

        refUserFollowers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    refRecommendation.set(recommendation).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            refInterestPoint.set(recommendation).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    storeRecommendationResponse.success();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                setRelationRecommendation(document.getId(), id, recommendation.getDate().getTime());
                                            }
                                        }
                                    }).start();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    storeRecommendationResponse.error();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            storeRecommendationResponse.error();
                        }
                    });
                }
            }
        });
    }

    public void deleteRecommendation (final String idPlace, String uidOwner, final DeleteRecommendationResponse deleteRecommendationResponse) {
        CollectionReference refUsersFollowers = db.collection("perfiles").document(uidOwner).collection("seguidores");
        final String idRecommendation = idPlace + uidOwner;
        final DocumentReference refRecommendation = db.collection("recomendaciones").document(idRecommendation);
        final DocumentReference refInterestPoint = db.collection("perfiles").document(uidOwner).collection("misPuntosDeInteres").document(idRecommendation);

        refUsersFollowers.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                refRecommendation.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        refInterestPoint.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deleteRecommendationResponse.success();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            deleteRelationRecommendation(document.getId(), idRecommendation);
                                        }
                                    }
                                }).start();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                deleteRecommendationResponse.error();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deleteRecommendationResponse.error();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteRecommendationResponse.error();
            }
        });
    }

    public void getIdsProposals (int limit, String idPropStart, final GetIdsPublicationsResponse getIdsPublicationsResponse) {
        CollectionReference refProposals = db.collection("propuestasViajes");
        if (idPropStart.isEmpty()) {
            refProposals.orderBy("id", Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<String> idsProposals = new ArrayList<>();
                        if (task.getResult().isEmpty()) getIdsPublicationsResponse.noPublications();
                        else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idsProposals.add(document.getId());
                            }
                            getIdsPublicationsResponse.success(idsProposals);
                        }
                    } else {
                        getIdsPublicationsResponse.error();
                    }
                }
            });
        } else {
            refProposals.orderBy("id", Query.Direction.DESCENDING).startAt(idPropStart).limit(limit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<String> idsProposals = new ArrayList<>();
                        if (task.getResult().isEmpty()) getIdsPublicationsResponse.noPublications();
                        else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idsProposals.add(document.getId());
                            }
                            getIdsPublicationsResponse.success(idsProposals);
                        }
                    } else {
                        getIdsPublicationsResponse.error();
                    }
                }
            });
        }
    }

    public void storePublication(final Publication publication, final StorePublicationResponse storePublicationResponse) {
        final CollectionReference refUserFollowers = db.collection("perfiles").document(publication.getUidUser()).collection("seguidores");

        DocumentReference refPublicationAux = db.collection("publicaciones").document();

        final String id = publication.getDate().getTime() + refPublicationAux.getId() ;
        publication.setId(id);
        final DocumentReference refPublication = db.collection("publicaciones").document(id);

        refUserFollowers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    refPublication.set(publication).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            storePublicationResponse.success();

                            //TODO: HACER ASYNCRON
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        setRelationPublication(document.getId(), publication.getId(), publication.getUidUser());
                                    }
                                }
                            }).start();
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

    public void deletePublication (final String idPublication, String uidOwner, final DeletePublicationResponse deletePublicationResponse) {
        CollectionReference refUserFollowers = db.collection("perfiles").document(uidOwner).collection("seguidores");

        final DocumentReference refPublication = db.collection("publicaciones").document(idPublication);

        refUserFollowers.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                refPublication.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deletePublicationResponse.success();
                        //TODO: HACER ASYNCRON
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    deleteRelationPublication(document.getId(), idPublication);
                                }
                            }
                        }).start();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        deletePublicationResponse.error();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deletePublicationResponse.error();
            }
        });
    }

    private void setRelationRecommendation (String uidFollower, String idRecommendation, long date) {
        Map<String, Long> mapRecommendation = new HashMap<>();
        mapRecommendation.put("Date", date);
        db.collection("perfiles")
                .document(uidFollower)
                .collection("recomendaciones")
                .document(idRecommendation)
                .set(mapRecommendation);
    }

    private void deleteRelationRecommendation (String uidFollower, String idRecommendation) {
        db.collection("perfiles")
                .document(uidFollower)
                .collection("recomendaciones")
                .document(idRecommendation)
                .delete();
    }

    private void setRelationPublication(String uidFollower, String idPublication, String idOwner) {
        Map<String,String> mapPublicacion = new HashMap<>();
        mapPublicacion.put("id", idPublication);
        mapPublicacion.put("ID Dueño", idOwner);
        db.collection("perfiles")
                .document(uidFollower)
                .collection("publicacionesSiguiendo")
                .document(idPublication)
                .set(mapPublicacion);
    }

    private void deleteRelationPublication(String uidFollower, String idPublication) {
        db.collection("perfiles")
                .document(uidFollower)
                .collection("publicacionesSiguiendo")
                .document(idPublication)
                .delete();
    }

    public void getIdsPublications (String uid, int limit, String idPubStart, final GetIdsPublicationsResponse getIdsPublicationsResponse) {
        CollectionReference refPublicaciones = db.collection("perfiles").document(uid).collection("publicacionesSiguiendo");

        if (idPubStart.isEmpty()) {
            refPublicaciones.orderBy("id", Query.Direction.DESCENDING).limit(limit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<String> idsPublications = new ArrayList<>();
                        if (task.getResult().isEmpty()) getIdsPublicationsResponse.noPublications();
                        else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idsPublications.add(document.getId());
                            }
                            getIdsPublicationsResponse.success(idsPublications);
                        }
                    } else {
                        getIdsPublicationsResponse.error();
                    }
                }
            });
        } else {
            refPublicaciones.orderBy("id", Query.Direction.DESCENDING).startAt(idPubStart).limit(limit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<String> idsPublications = new ArrayList<>();
                        if (task.getResult().isEmpty()) getIdsPublicationsResponse.noPublications();
                        else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                idsPublications.add(document.getId());
                            }
                            getIdsPublicationsResponse.success(idsPublications);
                        }
                    } else {
                        getIdsPublicationsResponse.error();
                    }
                }
            });
        }
    }

    public void getPublication (String idPublication, final GetPublicationResponse getPublicationResponse) {
        DocumentReference refPublication = db.collection("publicaciones").document(idPublication);

        refPublication.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                getPublicationResponse.success(documentSnapshot.toObject(Publication.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getPublicationResponse.error(e.getMessage());
            }
        });
    }

    public void likePublication (String idPublication, final String uid) {
        final DocumentReference docRef = db.collection("publicaciones").document(idPublication);

        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);
                ArrayList<String> likes = (ArrayList<String>) snapshot.get("uidLikes");
                if (likes.contains(uid)) {
                    likes.remove(uid);
                } else {
                    likes.add(uid);
                }

                transaction.update(docRef, "uidLikes", likes);
                return null;
            }
        });
    }

    public void commentPublication (final String idParentPublication, final Publication publication, final CommentPublicationResponse commentPublicationResponse) {
        final DocumentReference docRef = db.collection("publicaciones").document(idParentPublication);

        DocumentReference pubRef = db.collection("publicaciones").document();
        String id = publication.getDate().getTime() + pubRef.getId() ;
        publication.setId(id);

        final DocumentReference refPublication = db.collection("publicaciones").document(id);


        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);
                ArrayList<String> comments = (ArrayList<String>) snapshot.get("answers");
                comments.add(publication.getId());

                transaction.set(refPublication, publication);
                transaction.update(docRef, "answers", comments);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                commentPublicationResponse.success(publication.getId());
            }
        });
    }

    public void setCountryVisited(String uid, final String country, String idCountry) {
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
                int newNumPaises = snapshot.getLong("numPaises").intValue() + 1;

                String userName = snapshot.getString("nombreCompleto");
                String message = userName + " ha visitado " + country + ".";
                Publication publication = new Publication(snapshot.getString("uid"), userName, message, Calendar.getInstance().getTime());

                storePublication(publication, new StorePublicationResponse() {
                    @Override
                    public void success() {
                        Log.d("DatabaseController", "Country visited");
                    }

                    @Override
                    public void error() {
                        Log.d("DatabaseController", "Country not visited");
                    }
                });

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
                int newNumPaises = snapshot.getLong("numPaises").intValue() - 1;
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

    public void getFollowingUsers(final String uid, final GetProfileIdsResponse getFollowingUsersIdsResponse) {
        CollectionReference refFollowing = db.collection("perfiles").document(uid).collection("siguiendo");

        refFollowing.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> idsFollowing = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        if(!document.getId().equals(uid)) idsFollowing.add(document.getId());
                    }
                    if(idsFollowing.isEmpty()) getFollowingUsersIdsResponse.noFollowing();
                    else getFollowingUsersIdsResponse.success(idsFollowing);
                } else {
                    getFollowingUsersIdsResponse.error();
                }
            }
        });
    }

    public void getFollowersUsers(final String uid, final GetProfileIdsResponse getFollowerUsersIdsResponse) {
        CollectionReference refFollowers = db.collection("perfiles").document(uid).collection("seguidores");

        refFollowers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String> idsFollowers = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        if(!document.getId().equals(uid)) idsFollowers.add(document.getId());
                    }
                    if(idsFollowers.isEmpty()) getFollowerUsersIdsResponse.noFollowing();
                    else getFollowerUsersIdsResponse.success(idsFollowers);
                } else {
                    getFollowerUsersIdsResponse.error();
                }
            }
        });
    }

    public void existsRecommendation(String uid, String idPlace, final ExistsRecommendationResponse existsRecommendationResponse) {
        final DocumentReference refRecommendation = db.collection("perfiles").document(uid).collection("misPuntosDeInteres").document(uid.concat(idPlace));
        refRecommendation.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    existsRecommendationResponse.exists();
                }
                else {
                    existsRecommendationResponse.doNotExists();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                existsRecommendationResponse.error();
            }
        });
    }

    public void isFollowing(String uid, String idOther, final IsFollowingResponse isFollowingResponse) {
        DocumentReference refFollowing = db.collection("perfiles").document(uid).collection("siguiendo").document(idOther);
        refFollowing.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) isFollowingResponse.itIsFollowing();
                else isFollowingResponse.itIsNotFollowing();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isFollowingResponse.error();
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
        if(!uid.equals(idFollower)) {
            final DocumentReference docRef = db.collection("perfiles").document(uid);
            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(docRef);
                    int numSeguidores = snapshot.getLong("numSeguidores").intValue() + 1;
                    transaction.update(docRef, "numSeguidores", numSeguidores);

                    return null;
                }
            });
        }
    }

    public void deleteRelationFollower (String uid, String idFollower) {
        db.collection("perfiles")
                .document(uid)
                .collection("seguidores")
                .document(idFollower).delete();
        if(!uid.equals(idFollower)) {
            final DocumentReference docRef = db.collection("perfiles").document(uid);
            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(docRef);
                    int numSeguidores = snapshot.getLong("numSeguidores").intValue() - 1;
                    transaction.update(docRef, "numSeguidores", numSeguidores);

                    return null;
                }
            });
        }
    }

    public void setRelationFollowing (String uid, String idFollowing) {
        Map<String,String> map = new HashMap<>();
        map.put("ID Dueño", idFollowing);
        db.collection("perfiles")
                .document(uid)
                .collection("siguiendo")
                .document(idFollowing)
                .set(map);

        if(!uid.equals(idFollowing)) {
            final DocumentReference docRef = db.collection("perfiles").document(uid);
            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(docRef);
                    int numSeguidos = snapshot.getLong("numSeguidos").intValue() + 1;
                    transaction.update(docRef, "numSeguidos", numSeguidos);

                    return null;
                }
            });
        }
    }

    public void deleteRelationFollowing (String uid, String idFollowing) {
        db.collection("perfiles")
                .document(uid)
                .collection("siguiendo")
                .document(idFollowing)
                .delete();
        if(!uid.equals(idFollowing)) {
            final DocumentReference docRef = db.collection("perfiles").document(uid);
            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(docRef);
                    int numSeguidos = snapshot.getLong("numSeguidos").intValue() - 1;
                    transaction.update(docRef, "numSeguidos", numSeguidos);

                    return null;
                }
            });
        }
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
                            Log.d("DatabaseController", "Profile edited");
                        }

                        @Override
                        public void error() {
                            Log.d("DatabaseController", "Profile not edited");
                        }
                    };

                    Map<String, Object> mapChanges = new HashMap<>();
                    if (!oldProfile.getNombre().equals(newProfile.getNombre())) {
                        mapChanges.put("nombre", newProfile.getNombre());
                        mapChanges.put("nombreCompleto", newProfile.getNombre() + " " + newProfile.getApellidos());
                        mapChanges.put("nombreCompletoCapital", newProfile.getNombre().toUpperCase() + " " + newProfile.getApellidos().toUpperCase());

                        String message = newProfile.getNombreCompleto() + " ha cambiado su nombre de '" + oldProfile.getNombre() + "' a '" + newProfile.getNombre() + "'.";
                        Publication publication = new Publication(newProfile.getUid(), newProfile.getNombreCompleto(), message, Calendar.getInstance().getTime());
                        storePublication(publication, response);
                    }
                    if(!oldProfile.getApellidos().equals(newProfile.getApellidos())) {
                        mapChanges.put("apellidos", newProfile.getApellidos());
                        mapChanges.put("nombreCompleto", newProfile.getNombre() + " " + newProfile.getApellidos());
                        mapChanges.put("nombreCompletoCapital", newProfile.getNombre().toUpperCase() + " " + newProfile.getApellidos().toUpperCase());

                        String message = newProfile.getNombreCompleto() + " ha cambiado su apellido de '" + oldProfile.getApellidos() + "' a '" + newProfile.getApellidos() + "'.";
                        Publication publication = new Publication(newProfile.getUid(), newProfile.getNombreCompleto(), message, Calendar.getInstance().getTime());
                        storePublication(publication, response);
                    }
                    if(!oldProfile.getDescripcion().equals(newProfile.getDescripcion())) {
                        mapChanges.put("descripcion", newProfile.getDescripcion());

                        String message = newProfile.getNombreCompleto() + " ha cambiado su descripción.";
                        Publication publication = new Publication(newProfile.getUid(), newProfile.getNombreCompleto(), message, Calendar.getInstance().getTime());
                        storePublication(publication, response);
                    }
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    if(!dateFormat.format(oldProfile.getNacimiento()).equals(dateFormat.format(newProfile.getNacimiento()))) {
                        mapChanges.put("nacimiento", newProfile.getNacimiento());

                        String message = newProfile.getNombreCompleto() + " ha cambiado su fecha de nacimiento de '" + dateFormat.format(oldProfile.getNacimiento()) + "' a '" + dateFormat.format(newProfile.getNacimiento()) + "'.";
                        Publication publication = new Publication(newProfile.getUid(), newProfile.getNombreCompleto(), message, Calendar.getInstance().getTime());
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

    public void searchUsers(String userName, final String uid, final SearchUsersResponse searchUsersResponse) {
        CollectionReference refProfiles = db.collection("perfiles");

        userName = userName.toUpperCase();

        refProfiles.whereGreaterThanOrEqualTo("nombreCompletoCapital", userName).whereLessThanOrEqualTo("nombreCompletoCapital", userName+"Z").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Profile> users = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Profile profile = document.toObject(Profile.class);
                        if(!profile.getUid().equals(uid)) users.add(profile);
                    }
                    if(users.isEmpty()) searchUsersResponse.noUsers();
                    else searchUsersResponse.success(users);
                } else {
                    searchUsersResponse.error();
                }
            }
        });
    }

    public void getUserName (String uid, final GetUserNameResponse getUserNameResponse) {
        db.collection("perfiles").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        getUserNameResponse.success(document.getString("nombreCompleto"));
                    }
                } else {
                    getUserNameResponse.error();
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

    public interface ExistsRecommendationResponse {
        void exists();
        void doNotExists();
        void error();
    }

    public interface IsFollowingResponse {
        void itIsFollowing();
        void itIsNotFollowing();
        void error();
    }

    public interface DeleteRecommendationResponse {
        void success();
        void error();
    }

    public interface DeletePublicationResponse {
        void success();
        void error();
    }

    public interface CommentPublicationResponse {
        void success(String publicationId);
    }

    public interface GetUserNameResponse {
        void success(String userName);
        void error();
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

    public interface StoreRecommendationResponse {
        void success();
        void error();
    }

    public interface GetTripProposalResponse {
        void success(TripProposal tripProposal);
        void error(String message);
    }

    public interface GetPublicationResponse {
        void success(Publication publication);
        void error(String message);
    }

    public interface GetIdsPublicationsResponse {
        void success(ArrayList<String> idsPublications);
        void noPublications();
        void error();
    }

    public interface GetIdsRecommendationsResponse {
        void success(ArrayList<String> idsRecommendations);
        void noRecommendations();
        void error();
    }

    public interface GetRecommendationResponse {
        void success(Recommendation recommendation);
        void error (String message);
    }

    public interface GetProfileIdsResponse {
        void success(ArrayList<String> idsProfiles);
        void noFollowing();
        void error();
    }

    public interface GetProfileResponse {
        void success(Profile profile);
        void notFound();
        void error(String message);
    }

    public interface SearchUsersResponse {
        void success(ArrayList<Profile> users);
        void noUsers();
        void error();
    }

    public interface GetMembersOfTripResponse {
        void success(ArrayList<Profile> members);
        void noUsers();
        void error();
    }

    public interface SearchTripProposalResponse {
        void success(ArrayList<String> idTrips);
        void noTrips();
        void error();
    }

    public interface GetCountriesResponse {
        void success(HashMap<String, String> countries);
        void error();
    }

    public interface JoinTripResponse {
        void success();
        void error();
    }
}
