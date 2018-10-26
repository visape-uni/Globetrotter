package upc.fib.victor.globetrotter.Presentation.Activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import upc.fib.victor.globetrotter.Controllers.FirebaseAuthenticationController;
import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.Presentation.Fragments.RegisterEmailFragment;
import upc.fib.victor.globetrotter.Presentation.Fragments.RegisterBornDateFragment;
import upc.fib.victor.globetrotter.Presentation.Fragments.RegisterNameFragment;
import upc.fib.victor.globetrotter.Presentation.Fragments.RegisterPasswordFragment;
import upc.fib.victor.globetrotter.R;

public class RegisterActivity extends AppCompatActivity implements
        RegisterNameFragment.OnFragmentInteractionListener,
        RegisterBornDateFragment.OnFragmentInteractionListener,
        RegisterEmailFragment.OnFragmentInteractionListener,
        RegisterPasswordFragment.OnFragmentInteractionListener{

    private static final int NAME = 1;
    private static final int DATE = 2;
    private static final int EMAIL = 3;
    private static final int PASSWORD = 4;

    private final String TAG = "RegisterActivity";

    private Fragment fragment;
    protected FragmentManager fragmentManager;

    private FirebaseAuthenticationController firebaseAuthenticationController;
    private FirebaseDatabaseController firebaseDatabaseController;

    private LinearLayout backgroundLayout;

    private int currentFragment; //PUTSER ES POT ELIMINAR

    private String nombre;
    private String apellidos;
    private Date nacimiento;
    private String correo;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setFirebaseAuthenticationController();
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();

        backgroundLayout = findViewById(R.id.background_layout);

        setBackgroundAnimation();

        fragmentManager = getSupportFragmentManager();

        fragment = RegisterNameFragment.newInstance();
        displayFragment(R.id.frame_layout, fragment, "name");
        currentFragment = NAME;
    }

    private void setFirebaseAuthenticationController() {
        firebaseAuthenticationController = FirebaseAuthenticationController.getInstance();
        firebaseAuthenticationController.setAuthListener(new FirebaseAuthenticationController.AuthListenerResponse() {
            @Override
            public void signedIn() {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }

            @Override
            public void signedOut() {

            }
        });
    }

    private void setBackgroundAnimation() {

        backgroundLayout.setBackgroundResource(R.drawable.cascada_gif);

        AnimationDrawable frameAnimator = (AnimationDrawable) backgroundLayout.getBackground();
        frameAnimator.start();

    }

    // adds the given fragment to the front of the fragment stack
    protected void addFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .add(contentResId, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    // replaces the front fragment with the given fragment
    protected void replaceFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .replace(contentResId, fragment, tag)
                .commit();
    }

    // deletes all the fragments of the stack and displays the given one
    protected void displayFragment(int contentResId, Fragment fragment, String tag) {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        replaceFragment(contentResId, fragment, tag);
    }

    @Override
    public void onSetNombreApellidos(String nombre, String apellidos) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        fragment = RegisterBornDateFragment.newInstance();
        replaceFragment(R.id.frame_layout, fragment, "register_nacimiento");
    }

    @Override
    public void onSetNacimiento(Date date) {
        this.nacimiento = date;
        fragment = RegisterEmailFragment.newInstance();
        replaceFragment(R.id.frame_layout, fragment, "resgister_correo");
    }

    @Override
    public void onSetCorreo(String correo) {
        this.correo = correo;
        fragment = RegisterPasswordFragment.newInstance();
        replaceFragment(R.id.frame_layout, fragment, "register_password");
    }

    @Override
    public void onSetPassword(String password) {

        this.password = password;

        firebaseAuthenticationController.createAccount(correo, password, new FirebaseAuthenticationController.CreateAccountResponse() {
            @Override
            public void success() {
                // Sign in success, update UI with the signed-in user's information
                FirebaseUser user = firebaseAuthenticationController.getCurrentUser();

                //Store user in DB
                Profile profile = new Profile(user.getUid(), nombre, apellidos, nacimiento, correo);
                firebaseDatabaseController.storeProfile(profile, new FirebaseDatabaseController.StoreUserResponse() {
                    @Override
                    public void success() {
                        Toast.makeText(getApplicationContext(), "Registrado correctamente.", Toast.LENGTH_LONG).show();
                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }

                    @Override
                    public void error() {
                        Toast.makeText(getApplicationContext(), "Ha sucedido un error, pruebe más tarde", Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void error(String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

}

