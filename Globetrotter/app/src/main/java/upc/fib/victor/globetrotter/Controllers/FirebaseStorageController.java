package upc.fib.victor.globetrotter.Controllers;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class FirebaseStorageController {

    private static FirebaseStorageController instance;

    private Context context;
    private FirebaseStorage storage;

    private StorageReference storageRef;

    private FirebaseStorageController(Context context) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        this.context = context;
    }

    public static FirebaseStorageController getInstance(Context context) {
        if (instance == null) instance = new FirebaseStorageController(context);
        return instance;
    }

    public void uploadImage(Uri uri, String uid, final UploadImageResponse uploadImageResponse) {
        if (uri != null) {
            Log.d("STORAGE: ", "1");
            String profileImagePath = "profiles/" + uid + ".jpg";
            StorageReference ref = storageRef.child(profileImagePath);
            Log.d("STORAGE: ", "2");
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("STORAGE: ", "3");
                            uploadImageResponse.success();
                            Log.d("STORAGE: ", "4");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadImageResponse.error("Error " + e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            uploadImageResponse.progress("Subiendo " + (int) progress + "%");
                        }
                    });
        }
    }

    public void uploadPublicationImage (Uri uri, String id, final UploadImageResponse uploadImageResponse) {
        if (uri != null) {
            String profileImagePath = "publication/" + id + ".jpg";
            StorageReference ref = storageRef.child(profileImagePath);

            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadImageResponse.success();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadImageResponse.error("Error " + e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            uploadImageResponse.progress("Subiendo " + (int) progress + "%");
                        }
                    });
        }
    }

    public void deletePublicationImage (String id, final DeleteImageResponse deleteImageResponse) {
        String profileImagePath = "publication/" + id + ".jpg";
        StorageReference ref = storageRef.child(profileImagePath);

        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteImageResponse.success();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteImageResponse.error("Error eliminando la imagen");
            }
        });
    }

    public void getImageLocal(String path, final GetImageLocalResponse getImageResponse) {
        StorageReference ref = storageRef.child(path);

        try {
            File localFile = File.createTempFile("images", "jpg");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    getImageResponse.success();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    getImageResponse.error("Error al cargar la imagen");
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    getImageResponse.progress("Descargando imagen " + (int) progress + "%");
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getImageMemory(String path, final GetImageResponseMemory getImageResponse) {
        StorageReference ref = storageRef.child(path);

        final long ONE_MEGABYTE = 1024 * 1024;

        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                getImageResponse.success(bytes);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getImageResponse.error("Error al cargar la imagen");
            }
        });
    }

    public void loadImageToView(String path, GetImageResponse getImageResponse) {
        StorageReference ref = storageRef.child(path);
        getImageResponse.load(ref);
    }

    public interface DeleteImageResponse {
        void success();
        void error(String message);
    }

    public interface UploadImageResponse {
        void success();
        void progress(String message);
        void error(String message);
    }

    public interface GetImageLocalResponse {
        void success();
        void progress(String message);
        void error(String message);
    }

    public interface GetImageResponseMemory {
        void success(byte[] bytes);
        void error(String message);
    }

    public interface GetImageResponse {
        void load(StorageReference ref);
    }

    public void onDestroy() {
        if (context != null) context = null;
    }
}
