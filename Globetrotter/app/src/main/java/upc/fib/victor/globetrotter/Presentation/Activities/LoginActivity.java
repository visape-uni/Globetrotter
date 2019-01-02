package upc.fib.victor.globetrotter.Presentation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import upc.fib.victor.globetrotter.Controllers.FirebaseAuthenticationController;
import upc.fib.victor.globetrotter.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuthenticationController firebaseAuthenticationController;
    private final String TAG = "LoginActivity";

    private Button loginBtn;
    private Button registerBtn;

    private EditText emailTxt;
    private EditText passwordTxt;

    private LinearLayout backgroundLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        setFirebaseAuthenticationController();
        findViews();
        setBackgroundAnimation();
        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuthenticationController.attachAuthListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuthenticationController.removeAuthListener();
    }

    private void setFirebaseAuthenticationController() {
        firebaseAuthenticationController = FirebaseAuthenticationController.getInstance();

        firebaseAuthenticationController.setAuthListener(new FirebaseAuthenticationController.AuthListenerResponse() {
            @Override
            public void signedIn() {
                updateUI();
            }

            @Override
            public void signedOut() {

            }
        });
    }

    private void updateUI() {
        FirebaseUser currentUser = firebaseAuthenticationController.getCurrentUser();
        if (currentUser != null) {
            //TODO: arreglar bug en register, doble login
            String uid = currentUser.getUid();

            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit();
            editor.putString("uid", uid);
            editor.apply();

            Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
            profileIntent.putExtra("uidOwner", uid);
            startActivity(profileIntent);
            firebaseAuthenticationController.removeAuthListener();
            finish();
        }
    }

    private void setBackgroundAnimation() {
        backgroundLayout.setBackgroundResource(R.drawable.passport_gif);

        AnimationDrawable frameAnimator = (AnimationDrawable) backgroundLayout.getBackground();
        frameAnimator.start();

    }

    private void findViews() {
        backgroundLayout = findViewById(R.id.background_layout);
        emailTxt = findViewById(R.id.email_txt);
        passwordTxt = findViewById(R.id.password_txt);
        loginBtn = findViewById(R.id.entrar_btn);
        registerBtn = findViewById(R.id.register_btn);
    }

    private void setListeners() {

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailTxt.getText().toString().trim();
                String password = passwordTxt.getText().toString().trim();

                if (email.trim().equals("") || password.trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "Por favor rellena todos los campos.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuthenticationController.signIn(email, password, new FirebaseAuthenticationController.SignInResponse() {
                        @Override
                        public void success() {
                            // Sign in success, update UI with the signed-in user's information
                            updateUI();
                        }

                        @Override
                        public void error() {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Email o contraseña incorrecta. Prueba otra vez.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

    }
}
