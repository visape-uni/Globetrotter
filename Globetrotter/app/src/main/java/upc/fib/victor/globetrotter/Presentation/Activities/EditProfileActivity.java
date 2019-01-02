package upc.fib.victor.globetrotter.Presentation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Controllers.FirebaseStorageController;
import upc.fib.victor.globetrotter.Controllers.GlideApp;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.Domain.Publication;
import upc.fib.victor.globetrotter.R;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imagenPerfil;

    private EditText nombreTxt;
    private EditText apellidosTxt;
    private EditText descripcionTxt;

    private EditText dayTxt;
    private EditText monthTxt;
    private EditText yearTxt;

    private TextView cambiarFotoTxt;

    private Button cancelarBtn;
    private Button aceptarBtn;

    private FirebaseDatabaseController firebaseDatabaseController;
    private FirebaseStorageController firebaseStorageController;

    private Profile activityProfile;

    private String uid;

    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        findViews();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        getDatabaseController(uid);
    }

    private void getDatabaseController(final String uid) {
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
        firebaseStorageController = FirebaseStorageController.getInstance();

        firebaseDatabaseController.getProfile(uid, new FirebaseDatabaseController.GetProfileResponse() {
            @Override
            public void success(Profile profile) {
                activityProfile = profile;

                nombreTxt.setText(activityProfile.getNombre());
                apellidosTxt.setText(activityProfile.getApellidos());
                descripcionTxt.setText(activityProfile.getDescripcion());


                DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
                String date = dataFormat.format(activityProfile.getNacimiento());
                int day = Integer.valueOf(date.substring(0,2));
                int month = Integer.valueOf(date.substring(3,5));
                int year = Integer.valueOf(date.substring(6,10));

                dayTxt.setText(String.valueOf(day));
                monthTxt.setText(String.valueOf(month));
                yearTxt.setText(String.valueOf(year));


                firebaseStorageController.loadImageToView("profiles/" + uid + ".jpg", new FirebaseStorageController.GetImageResponse() {
                    @Override
                    public void load(StorageReference ref) {

                        GlideApp.with(getApplicationContext())
                                .load(ref)
                                .placeholder(getResources().getDrawable(R.drawable.silueta))
                                .into(imagenPerfil);
                    }
                });

                cambiarFotoTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cambiarFoto();
                    }
                });

                cancelarBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("uidOwner", uid);
                        startActivity(intent);
                        finish();
                    }
                });

                aceptarBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editProfile();
                    }
                });
            }

            @Override
            public void notFound() {
                Toast.makeText(EditProfileActivity.this, "Perfil de usuario no encontrado. Pruebe otra vez",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(String message) {
                Toast.makeText(EditProfileActivity.this, "Error: " + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cambiarFoto() {
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Elige una foto"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST
                && data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();

                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Subiendo...");
                progressDialog.show();

                firebaseStorageController.uploadImage(imageUri, uid, new FirebaseStorageController.UploadImageResponse() {
                    @Override
                    public void success() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Foto subida", Toast.LENGTH_SHORT).show();
                        imagenPerfil.setImageBitmap(bitmap);

                        Publication publication = new Publication(uid, activityProfile.getNombreCompleto(), activityProfile.getNombreCompleto() + " " + R.string.cambio_de_foto, Calendar.getInstance().getTime());
                        firebaseDatabaseController.storePublication(publication, new FirebaseDatabaseController.StorePublicationResponse() {
                            @Override
                            public void success() {
                                Log.d("EditProfileActivity: ", "Published");
                            }

                            @Override
                            public void error() {
                                Log.d("EditProfileActivity: ", "Not published");
                            }
                        });
                    }

                    @Override
                    public void progress(String message) {
                        progressDialog.setMessage(message);
                    }

                    @Override
                    public void error(String message) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Ha sucedido un error, pruebe más tarde", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No has seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.putExtra("uidOwner", uid);
        startActivity(intent);
        finish();
    }

    private void editProfile() {
        final String nombre = nombreTxt.getText().toString().trim();
        final String apellidos = apellidosTxt.getText().toString().trim();
        int day = Integer.valueOf(dayTxt.getText().toString());
        int month = Integer.valueOf(monthTxt.getText().toString());
        int year = Integer.valueOf(yearTxt.getText().toString());
        final String descripcion = descripcionTxt.getText().toString().trim();

        if (nombre.isEmpty() || apellidos.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Debes indicar tu nombre y apellidos",
                    Toast.LENGTH_SHORT).show();
        } else {
            String nombreCapital = nombre.substring(0,1).toUpperCase() + nombre.substring(1);
            String apellidosCapital = apellidos.substring(0,1).toUpperCase() + apellidos.substring(1);

            if (descripcion.length() > 100) {
                Toast.makeText(EditProfileActivity.this, "La descripción no puede tener más de 100 carácteres",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (day < 1 || day > 31 || month < 1 || month > 21 || year < 1900 || year > 2019) {
                    Toast.makeText(EditProfileActivity.this, "Ingrese una fecha válida por favor",
                            Toast.LENGTH_SHORT).show();
                } else {

                    final Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month-1);
                    cal.set(Calendar.DAY_OF_MONTH, day);

                    final Profile profile = new Profile(uid, nombreCapital, apellidosCapital, cal.getTime(), descripcion);

                    firebaseDatabaseController.editProfile(profile, new FirebaseDatabaseController.EditProfileResponse() {
                        @Override
                        public void success() {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.putExtra("uidOwner", uid);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void notFound() {
                            Toast.makeText(EditProfileActivity.this, "Error interno, vuelve a probar más tarde",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void error() {
                            Toast.makeText(EditProfileActivity.this, "Error: Perfil no editado, pruebe más tarde",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

    }

    private void findViews() {
        imagenPerfil = findViewById(R.id.profileImageView);
        nombreTxt = findViewById(R.id.nombreTxt);
        apellidosTxt = findViewById(R.id.apellidosTxt);
        descripcionTxt = findViewById(R.id.descripcionTxt);
        dayTxt = findViewById(R.id.dayTxt);
        monthTxt = findViewById(R.id.monthTxt);
        yearTxt = findViewById(R.id.yearTxt);
        cancelarBtn = findViewById(R.id.cancelarBtn);
        aceptarBtn = findViewById(R.id.aceptarBtn);
        cambiarFotoTxt = findViewById(R.id.cambiarFotoLbl);
    }
}
