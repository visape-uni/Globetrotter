package upc.fib.victor.globetrotter.Controllers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import upc.fib.victor.globetrotter.Presentation.Activities.LoginActivity;
import upc.fib.victor.globetrotter.Presentation.Activities.ProfileActivity;

public class FirebaseAuthenticationController {

    private static FirebaseAuthenticationController instance;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private FirebaseAuthenticationController() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseAuthenticationController getInstance() {
        if (instance == null) instance = new FirebaseAuthenticationController();
        return instance;
    }

    public void setAuthListener(final AuthListenerResponse authListenerResponse) {

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    authListenerResponse.signedIn();
                } else {
                    authListenerResponse.signedOut();
                }
            }
        };
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void signIn (String email, String password, final SignInResponse signInResponse) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        signInResponse.success();
                    } else {
                        signInResponse.error();
                    }
                }
            });
    }

    public void createAccount (String email, String password, final CreateAccountResponse createAccountResponse) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            createAccountResponse.success();
                        } else {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                createAccountResponse.error("Error: La contraseña debe ser más larga");
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                createAccountResponse.error("Error: Formato del email incorrecto");
                            } catch(FirebaseAuthUserCollisionException e) {
                                createAccountResponse.error("Error: Ya existe una cuenta con este email");
                            } catch(Exception e) {
                                createAccountResponse.error("Error interno. Prueba más tarde");
                            }
                        }
                    }
                });
    }

    public void attachAuthListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void removeAuthListener() {
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signOut() {
        mAuth.signOut();
    }

    public interface SignInResponse {
        void success();
        void error();
    }

    public interface CreateAccountResponse {
        void success();
        void error(String message);
    }

    public interface AuthListenerResponse {
        void signedIn();
        void signedOut();
    }
}
