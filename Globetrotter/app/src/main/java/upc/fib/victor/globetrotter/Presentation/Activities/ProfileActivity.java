package upc.fib.victor.globetrotter.Presentation.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.R;

public class ProfileActivity extends AppCompatActivity {

    private Profile currentProfile;

    private TextView nameTxt;
    private TextView bornTxt;

    private ImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String uid = getIntent().getStringExtra("uid");
        getProfileAndDisplay(uid);

    }

    private void getProfileAndDisplay(String uid) {
        FirebaseDatabaseController firebaseDatabaseController = new FirebaseDatabaseController();
        firebaseDatabaseController.getProfile(uid, new FirebaseDatabaseController.GetProfileResponse() {
            @Override
            public void success(Profile profile) {
                currentProfile = profile;
                nameTxt.setText(currentProfile.getNombreCompleto());
                bornTxt.setText(currentProfile.getNacimiento().toString());
                //TODO: CARGAR IMAGEN DE PERFIL
            }

            @Override
            public void notFound() {
                Toast.makeText(ProfileActivity.this, "Perfil de usuario no encontrado. Pruebe otra vez.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(String message) {
                Toast.makeText(ProfileActivity.this, "Error: " + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findViews() {
        nameTxt = findViewById(R.id.nameLbl);
        bornTxt = findViewById(R.id.bornLbl);
        profileImg = findViewById(R.id.profileImageView);
    }
}
