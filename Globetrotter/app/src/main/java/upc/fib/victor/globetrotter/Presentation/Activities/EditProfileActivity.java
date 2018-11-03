package upc.fib.victor.globetrotter.Presentation.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.R;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imagenPerfil;

    private EditText nombreTxt;
    private EditText apellidosTxt;
    private EditText descripcionTxt;

    private EditText dayTxt;
    private EditText monthTxt;
    private EditText yearTxt;

    private Button cancelarBtn;
    private Button aceptarBtn;

    private FirebaseDatabaseController firebaseDatabaseController;

    private Profile activityProfile;

    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        findViews();

        uid = getIntent().getStringExtra("uid");
        getDatabaseController(uid);
    }

    private void getDatabaseController(String uid) {
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();

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

                //TODO: CARGAR IMAGEN DE PERFIL

                cancelarBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
            if (descripcion.length() > 255) {
                Toast.makeText(EditProfileActivity.this, "La descripción no puede tener más de 255 carácteres",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (day < 1 || day > 31 || month < 1 || month > 21 || year < 1900 || year > 2019) {
                    Toast.makeText(EditProfileActivity.this, "Ingrese una fecha válida por favor",
                            Toast.LENGTH_SHORT).show();
                } else {

                    final Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day);

                    firebaseDatabaseController.getProfile(uid, new FirebaseDatabaseController.GetProfileResponse() {
                        @Override
                        public void success(Profile profileAnterior) {
                            Profile profile = new Profile(uid, nombre, apellidos, cal.getTime(), profileAnterior.getCorreo(), profileAnterior.getNumSeguidores(),
                                                            profileAnterior.getNumSeguidos(), profileAnterior.getNumPaises(), descripcion);
                            firebaseDatabaseController.storeProfile(profile, new FirebaseDatabaseController.StoreUserResponse() {
                                @Override
                                public void success() {
                                    Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                    profileIntent.putExtra("uid", uid);
                                    startActivity(profileIntent);
                                    finish();
                                }

                                @Override
                                public void error() {
                                    Toast.makeText(EditProfileActivity.this, "Error: Perfil no editado, pruebe más tarde",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void notFound() {
                            Toast.makeText(EditProfileActivity.this, "Error interno, vuelve a probar",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void error(String message) {
                            Toast.makeText(EditProfileActivity.this, "Error: " + message,
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
    }
}
