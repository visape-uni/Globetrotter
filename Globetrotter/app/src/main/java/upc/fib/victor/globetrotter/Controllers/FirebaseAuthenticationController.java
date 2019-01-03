package upc.fib.victor.globetrotter.Controllers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthenticationController {

    private static FirebaseAuthenticationController instance;

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private FirebaseAuthenticationController(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseAuthenticationController getInstance(Context context) {
        if (instance == null) instance = new FirebaseAuthenticationController(context);
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

    public void sendPasswordResetEmail(String emailAddress, final RecoverPasswordResponse recoverPasswordResponse) {
        mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) recoverPasswordResponse.success();
                else recoverPasswordResponse.error();
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

    public interface RecoverPasswordResponse {
        void success();
        void error();
    }

    public void onDestroy() {
        if (context != null) context = null;
    }
}
