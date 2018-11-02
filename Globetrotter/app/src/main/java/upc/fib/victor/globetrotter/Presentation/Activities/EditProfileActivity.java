package upc.fib.victor.globetrotter.Presentation.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    private Map<Integer, Integer> yearPosMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        findViews();

        String uid = getIntent().getStringExtra("uid");
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

            }

            @Override
            public void notFound() {
                Toast.makeText(EditProfileActivity.this, "Perfil de usuario no encontrado. Pruebe otra vez.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(String message) {
                Toast.makeText(EditProfileActivity.this, "Error: " + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
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
