package upc.fib.victor.globetrotter.Presentation.Controllers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    private FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

        fragmentManager = getSupportFragmentManager();

        fragment = RegisterNameFragment.newInstance();
        displayFragment(R.id.frame_layout, fragment, "name");
        currentFragment = NAME;
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
        createAccount(correo, password);
    }

    private void createAccount (String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");

                    Toast.makeText(getApplicationContext(), "Registrado correctamente.", Toast.LENGTH_LONG).show();
                    FirebaseUser user = mAuth.getCurrentUser();

                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);
                    finish();

                } else {

                    try {
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(getApplicationContext(), "Error: La contraseña debe ser más larga", Toast.LENGTH_LONG).show();
                    } catch(FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(getApplicationContext(), "Error: Formato del email incorrecto", Toast.LENGTH_LONG).show();
                    } catch(FirebaseAuthUserCollisionException e) {
                        Toast.makeText(getApplicationContext(), "Error: Ya hay una cuenta con este email", Toast.LENGTH_LONG).show();
                    } catch(Exception e) {
                        Toast.makeText(getApplicationContext(), "Error interno. Prueba más tarde", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
