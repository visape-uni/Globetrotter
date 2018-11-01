package upc.fib.victor.globetrotter.Presentation.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import upc.fib.victor.globetrotter.Controllers.FirebaseAuthenticationController;
import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.R;

public class ProfileActivity extends AppCompatActivity {

    private Profile activityProfile;

    private TextView nameTxt;
    private TextView bornTxt;
    private TextView seguidosTxt;
    private TextView seguidoresTxt;
    private TextView paisesTxt;

    private ImageView profileImg;

    private Button editBtn;
    private Button seguirBtn;
    private Button dejarSeguirBtn;

    private FrameLayout frameLayout;

    private FirebaseDatabaseController firebaseDatabaseController;
    private FirebaseAuthenticationController firebaseAuthenticationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuthenticationController = FirebaseAuthenticationController.getInstance();

        findViews();

        String uid = getIntent().getStringExtra("uid");
        getProfileAndDisplay(uid);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(editIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case DialogInterface.BUTTON_POSITIVE:

                            firebaseAuthenticationController.signOut();
                            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(loginIntent);
                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setMessage("Estás seguro de que quieres cerrar sessión?")
                    .setPositiveButton("Sí", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getProfileAndDisplay(String uid) {

        firebaseDatabaseController = FirebaseDatabaseController.getInstance();

        firebaseDatabaseController.getProfile(uid, new FirebaseDatabaseController.GetProfileResponse() {
            @Override
            public void success(Profile profile) {
                activityProfile = profile;

                if (activityProfile.equals(firebaseAuthenticationController.getCurrentUser())) {
                    seguirBtn.setVisibility(View.GONE);
                    dejarSeguirBtn.setVisibility(View.GONE);
                    editBtn.setVisibility(View.VISIBLE);
                } else {
                    editBtn.setVisibility(View.GONE);
                    if(isFollowing()) {
                        seguirBtn.setVisibility(View.INVISIBLE);
                        dejarSeguirBtn.setVisibility(View.VISIBLE);
                    } else {
                        seguirBtn.setVisibility(View.VISIBLE);
                        dejarSeguirBtn.setVisibility(View.INVISIBLE);
                    }
                }

                nameTxt.setText(activityProfile.getNombreCompleto());
                seguidoresTxt.setText(String.valueOf(activityProfile.getNumSeguidores()));
                seguidosTxt.setText(String.valueOf(activityProfile.getNumSeguidos()));
                paisesTxt.setText(String.valueOf(activityProfile.getNumPaises()));

                DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
                bornTxt.setText(dataFormat.format(activityProfile.getNacimiento()));
                //TODO: CARGAR IMAGEN DE PERFIL

            }

            @Override
            public void notFound() {
                Toast.makeText(ProfileActivity.this, "Perfil de usuario no encontrado. Pruebe otra vez.",
                        Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
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
        paisesTxt = findViewById(R.id.paisesVisitadosTxt);
        seguidoresTxt = findViewById(R.id.seguidoresTxt);
        seguidosTxt = findViewById(R.id.seguidosTxt);
        seguirBtn = findViewById(R.id.seguirBtn);
        dejarSeguirBtn = findViewById(R.id.dejar_seguir_Btn);
        editBtn = findViewById(R.id.editarBtn);
        frameLayout = findViewById(R.id.frame_layout);
    }

    private boolean isFollowing() {
        //TODO: IMPLEMENTAR METODO PARA VER SI LO SIGUE
        return false;
    }
}
