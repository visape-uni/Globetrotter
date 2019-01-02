package upc.fib.victor.globetrotter.Presentation.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import upc.fib.victor.globetrotter.Controllers.FirebaseAuthenticationController;
import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Controllers.FirebaseStorageController;
import upc.fib.victor.globetrotter.Controllers.GlideApp;
import upc.fib.victor.globetrotter.Domain.Profile;
import upc.fib.victor.globetrotter.Domain.Publication;
import upc.fib.victor.globetrotter.Presentation.Fragments.WallFragment;
import upc.fib.victor.globetrotter.R;

public class ProfileActivity extends AppCompatActivity implements WallFragment.OnFragmentInteractionListener {

    private ProgressDialog progressDialog;

    private String uidOwner;
    private String uid;
    private Profile activityProfile;

    private TextView nameTxt;
    private TextView nameTxt2;
    private TextView bornTxt;
    private TextView seguidosTxt;
    private TextView seguidoresTxt;
    private TextView paisesTxt;

    private ImageView crearRutaImgBtn;
    private ImageView diarioImgBtn;
    private ImageView buscarImgBtn;
    private ImageView recomendarImgBtn;

    private ImageView profileImg;

    private TextInputLayout publicationLayout;
    private TextInputEditText publicationTxt;
    private FloatingActionButton publicateBtn;

    private Button editBtn;
    private Button seguirBtn;
    private Button dejarSeguirBtn;

    private LinearLayout seguidosLayout;
    private LinearLayout seguidoresLayout;

    private CardView firstCardView;
    private LinearLayout bornLayout;
    private LinearLayout infoLayout;
    private ImageView arrowImg;

    private Fragment fragment;
    protected FragmentManager fragmentManager;

    private ImageButton pictureBtn;

    private boolean follower;

    private FirebaseDatabaseController firebaseDatabaseController;
    private FirebaseAuthenticationController firebaseAuthenticationController;
    private FirebaseStorageController firebaseStorageController;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        uidOwner = getIntent().getExtras().getString("uidOwner");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cargando perfil...");
        progressDialog.show();

        firebaseAuthenticationController = FirebaseAuthenticationController.getInstance();
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
        firebaseStorageController = FirebaseStorageController.getInstance();

        findViews();
        bottomBar();

        fragmentManager = getSupportFragmentManager();

        firstCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((profileImg.getVisibility() == View.GONE) && (bornLayout.getVisibility() == View.GONE) && (infoLayout.getVisibility()==View.GONE)) {
                    showInteraction();
                }
            }
        });

        publicateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String content = publicationTxt.getText().toString().trim();
                publicationTxt.setText("");
                if(!content.isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(publicationTxt.getWindowToken(), 0);
                    firebaseDatabaseController.getUserName(uid, new FirebaseDatabaseController.GetUserNameResponse() {
                        @Override
                        public void success(String userName) {
                            firebaseDatabaseController.storePublication(new Publication(uid, userName, content, Calendar.getInstance().getTime()), new FirebaseDatabaseController.StorePublicationResponse() {
                                @Override
                                public void success() {
                                    loadFragment();
                                    Toast.makeText(getApplicationContext(), "Se ha publicado correctamente.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void error() {
                                    //Error publicating
                                    Toast.makeText(getApplicationContext(), "No se ha podido publicar, pruebe más tarde", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void error() {
                            //Error getting userName
                            Toast.makeText(getApplicationContext(), "No se ha podido publicar, pruebe más tarde", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No has escrito nada", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPicture();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(editIntent);
                finish();
            }
        });

        seguirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabaseController.setRelationFollowing(uid, uidOwner);
                firebaseDatabaseController.setRelationFollower(uidOwner, uid);
                activityProfile.setNumSeguidores(activityProfile.getNumSeguidores() + 1);
                seguidoresTxt.setText(String.valueOf(activityProfile.getNumSeguidores()));

                dejarSeguirBtn.setVisibility(View.VISIBLE);
                seguirBtn.setVisibility(View.GONE);
            }
        });

        dejarSeguirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabaseController.deleteRelationFollowing(uid, uidOwner);
                firebaseDatabaseController.deleteRelationFollower(uidOwner,uid);
                activityProfile.setNumSeguidores(activityProfile.getNumSeguidores() - 1);
                seguidoresTxt.setText(String.valueOf(activityProfile.getNumSeguidores()));

                seguirBtn.setVisibility(View.VISIBLE);
                dejarSeguirBtn.setVisibility(View.GONE);
            }
        });

        seguidoresLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSeguidores = new Intent(getApplicationContext(), FollowersActivity.class);
                intentSeguidores.putExtra("idOwner", uidOwner);
                startActivity(intentSeguidores);
                finish();
            }
        });

        seguidosLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FollowingActivity.class);
                intent.putExtra("idOwner", uidOwner);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getProfileAndDisplay(uidOwner);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            if (uid.equals(uidOwner)) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:

                                firebaseAuthenticationController.signOut();
                                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit();
                                editor.remove("uid");
                                editor.apply();
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
            } else {
                uidOwner = uid;

                progressDialog = new ProgressDialog(this);
                progressDialog.setIndeterminate(true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Cargando perfil...");
                progressDialog.show();

                getProfileAndDisplay(uidOwner);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment() {
        fragment = WallFragment.newInstance(uid, uidOwner);
        displayFragment(R.id.frame_layout, fragment, "wall");
    }

    private void uploadPicture() {
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
                final Uri imageUri = data.getData();

                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Subiendo...");
                progressDialog.show();


                final String content = publicationTxt.getText().toString().trim();
                publicationTxt.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(publicationTxt.getWindowToken(), 0);
                firebaseDatabaseController.getUserName(uid, new FirebaseDatabaseController.GetUserNameResponse() {
                    @Override
                    public void success(String userName) {
                        firebaseDatabaseController.storePublicationWithPicture(new Publication(uid, userName, content, Calendar.getInstance().getTime(), true), new FirebaseDatabaseController.StorePublicationWithPictureResponse() {
                            @Override
                            public void success(String id) {
                                firebaseStorageController.uploadPublicationImage(imageUri, id, new FirebaseStorageController.UploadImageResponse() {
                                    @Override
                                    public void success() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Foto subida", Toast.LENGTH_SHORT).show();

                                        loadFragment();
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
                            }

                            @Override
                            public void error() {
                                //Error publicating
                                Toast.makeText(getApplicationContext(), "No se ha podido publicar, pruebe más tarde", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void error() {
                        //Error getting userName
                        Toast.makeText(getApplicationContext(), "No se ha podido publicar, pruebe más tarde", Toast.LENGTH_SHORT).show();
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

    private void getProfileAndDisplay(final String id) {

        firebaseDatabaseController.getProfile(id, new FirebaseDatabaseController.GetProfileResponse() {
            @Override
            public void success(Profile profile) {
                activityProfile = profile;

                if (activityProfile.getUid().equals(uid)) {
                    seguirBtn.setVisibility(View.GONE);
                    dejarSeguirBtn.setVisibility(View.GONE);
                    editBtn.setVisibility(View.VISIBLE);

                } else {
                    editBtn.setVisibility(View.GONE);
                    firebaseDatabaseController.isFollowing(uid, uidOwner, new FirebaseDatabaseController.IsFollowingResponse() {
                        @Override
                        public void itIsFollowing() {
                            seguirBtn.setVisibility(View.INVISIBLE);
                            dejarSeguirBtn.setVisibility(View.VISIBLE);
                            follower = true;
                        }

                        @Override
                        public void itIsNotFollowing() {
                            seguirBtn.setVisibility(View.VISIBLE);
                            dejarSeguirBtn.setVisibility(View.INVISIBLE);
                            follower = false;
                        }

                        @Override
                        public void error() {
                            seguirBtn.setVisibility(View.VISIBLE);
                            dejarSeguirBtn.setVisibility(View.INVISIBLE);
                            follower = false;
                        }
                    });
                    publicateBtn.setVisibility(View.GONE);
                    publicationLayout.setVisibility(View.GONE);
                }

                nameTxt.setText(activityProfile.getNombreCompleto());
                nameTxt2.setText(activityProfile.getNombreCompleto());
                seguidoresTxt.setText(String.valueOf(activityProfile.getNumSeguidores()));
                seguidosTxt.setText(String.valueOf(activityProfile.getNumSeguidos()));
                paisesTxt.setText(String.valueOf(activityProfile.getNumPaises()));

                DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
                bornTxt.setText(dataFormat.format(activityProfile.getNacimiento()));


                firebaseStorageController.loadImageToView("profiles/" + uidOwner + ".jpg", new FirebaseStorageController.GetImageResponse() {
                    @Override
                    public void load(StorageReference ref) {
                        GlideApp.with(getApplicationContext())
                                .load(ref)
                                .placeholder(getResources().getDrawable(R.drawable.silueta))
                                .into(profileImg);
                    }
                });
                setTitle("Perfil de " + profile.getNombre());

                progressDialog.dismiss();
            }

            @Override
            public void notFound() {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Perfil de usuario no encontrado. Vuelve a intentarlo",
                        Toast.LENGTH_SHORT).show();
                firebaseAuthenticationController.signOut();
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }

            @Override
            public void error(String message) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Error: " + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
        loadFragment();
    }

    private void findViews() {
        arrowImg = findViewById(R.id.arrowImg);
        bornLayout = findViewById(R.id.bornLayout);
        infoLayout = findViewById(R.id.info_layout);
        firstCardView = findViewById(R.id.cardView);
        nameTxt = findViewById(R.id.nameLbl);
        nameTxt2 = findViewById(R.id.nameLbl2);
        bornTxt = findViewById(R.id.bornLbl);
        profileImg = findViewById(R.id.profileImageView);
        paisesTxt = findViewById(R.id.paisesVisitadosTxt);
        seguidoresTxt = findViewById(R.id.seguidoresTxt);
        seguidosTxt = findViewById(R.id.seguidosTxt);
        seguirBtn = findViewById(R.id.seguirBtn);
        dejarSeguirBtn = findViewById(R.id.dejar_seguir_Btn);
        editBtn = findViewById(R.id.editarBtn);
        buscarImgBtn = findViewById(R.id.ic_buscar);
        crearRutaImgBtn = findViewById(R.id.ic_crear_ruta);
        diarioImgBtn = findViewById(R.id.ic_diario_viajero);
        recomendarImgBtn = findViewById(R.id.ic_crear_recomendacion);
        publicationTxt = findViewById(R.id.publication_input);
        publicateBtn = findViewById(R.id.send_btn);
        seguidosLayout = findViewById(R.id.layout_seguidos);
        seguidoresLayout = findViewById(R.id.layout_seguidores);
        publicationLayout = findViewById(R.id.textInputLayout);
        pictureBtn = findViewById(R.id.pictureBtn);
    }

    private void bottomBar() {
        crearRutaImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webIntent = new Intent(getApplicationContext(), UserMapActivity.class);
                startActivity(webIntent);
            }
        });
        diarioImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent diaryIntent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(diaryIntent);
            }
        });
        buscarImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(searchIntent);
                finish();
            }
        });
        recomendarImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recommendationIntent = new Intent(getApplicationContext(), RecommendationsActivity.class);
                startActivity(recommendationIntent);
            }
        });
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

    // Called by fragment
    @Override
    public void hideInteraction() {
        profileImg.animate().translationY(-firstCardView.getHeight());
        bornLayout.animate().translationY(-firstCardView.getHeight());
        infoLayout.animate().translationY(-firstCardView.getHeight());
        editBtn.animate().translationY(-firstCardView.getHeight());
        dejarSeguirBtn.animate().translationY(-firstCardView.getHeight());
        seguirBtn.animate().translationY(-firstCardView.getHeight());
        nameTxt.animate().translationY(-firstCardView.getHeight());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                profileImg.setVisibility(View.GONE);
                bornLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
                editBtn.setVisibility(View.GONE);
                dejarSeguirBtn.setVisibility(View.GONE);
                seguirBtn.setVisibility(View.GONE);
                nameTxt.setVisibility(View.GONE);
                nameTxt2.setVisibility(View.VISIBLE);
                firstCardView.setCardBackgroundColor(getResources().getColor(R.color.colorCardViewAnimation));
                pictureBtn.setBackgroundColor(getResources().getColor(R.color.colorCardViewAnimation));
                arrowImg.setVisibility(View.VISIBLE);
            }
        }, 450);

    }

    public void showInteraction() {

        firstCardView.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
        nameTxt2.setVisibility(View.GONE);
        arrowImg.setVisibility(View.GONE);

        if (activityProfile.getUid().equals(uid))
            editBtn.setVisibility(View.VISIBLE);
        else {
            if (follower) dejarSeguirBtn.setVisibility(View.VISIBLE);
            else seguirBtn.setVisibility(View.VISIBLE);
        }

        profileImg.setVisibility(View.VISIBLE);
        bornLayout.setVisibility(View.VISIBLE);
        infoLayout.setVisibility(View.VISIBLE);
        nameTxt.setVisibility(View.VISIBLE);

        profileImg.animate().translationY(0);
        bornLayout.animate().translationY(0);
        infoLayout.animate().translationY(0);
        seguirBtn.animate().translationY(0);
        dejarSeguirBtn.animate().translationY(0);
        editBtn.animate().translationY(0);
        nameTxt.animate().translationY(0);


        WallFragment wallFragment = (WallFragment) fragmentManager.findFragmentById(R.id.frame_layout);
        wallFragment.showInteraction();
    }
}
