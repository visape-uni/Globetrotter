package upc.fib.victor.globetrotter.Presentation.Activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Controllers.GooglePlacesController;
import upc.fib.victor.globetrotter.Domain.Recommendation;
import upc.fib.victor.globetrotter.R;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class InterestPointActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView nameLbl;
    private TextView addressLbl;
    private TextView phoneLbl;
    private TextView websiteLbl;
    private TextView commentLbl;

    private AlertDialog dialog;
    private Menu menu;

    private TextView nameTxt;
    private TextView addressTxt;
    private TextView phoneTxt;
    private TextView websiteTxt;
    private TextView commentTxt;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    private Place place;

    private String uid;

    private ProgressDialog progressDialog;

    private String uidRec;
    private String idPlace;

    private FirebaseDatabaseController firebaseDatabaseController;
    private GooglePlacesController googlePlacesController;

    private final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_point);

        findViews();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        setTitle("Punto de Interés");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        googlePlacesController = new GooglePlacesController(this);
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();

        try {
            uidRec = getIntent().getExtras().getString("uid");
            idPlace = getIntent().getExtras().getString("idPlace");

            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Cargando perfil...");
            progressDialog.show();

            firebaseDatabaseController.getRecommendation(uidRec.concat(idPlace), new FirebaseDatabaseController.GetRecommendationResponse() {
                @Override
                public void success(final Recommendation recommendation) {
                    googlePlacesController.getPlaceById(recommendation.getIdInterestPoint(), new GooglePlacesController.GetPlaceByIdResponse() {
                        @Override
                        public void success(Place place) {
                            mMap.moveCamera( CameraUpdateFactory.newLatLngBounds(place.getViewport(), 0));
                            mMap.addMarker(new MarkerOptions().position(place.getLatLng()));

                            nameTxt.setText(place.getName());
                            addressTxt.setText(place.getAddress());
                            if (place.getPhoneNumber() == null || place.getPhoneNumber().toString().isEmpty()) {
                                phoneTxt.setText("Desconocido");
                            } else {
                                phoneTxt.setText(place.getPhoneNumber());
                            }
                            if (place.getWebsiteUri() == null || place.getWebsiteUri().toString().isEmpty()) {
                                websiteTxt.setText("Desconocida");
                            } else {
                                websiteTxt.setText(place.getWebsiteUri().toString());
                            }
                            if (recommendation.getComment() == null || recommendation.getComment().isEmpty()) {
                                commentTxt.setText("No hay comentario");
                            } else {
                                commentTxt.setText(recommendation.getComment());
                            }

                            nameTxt.setVisibility(View.VISIBLE);
                            addressTxt.setVisibility(View.VISIBLE);
                            phoneTxt.setVisibility(View.VISIBLE);
                            websiteTxt.setVisibility(View.VISIBLE);
                            commentTxt.setVisibility(View.VISIBLE);
                            nameLbl.setVisibility(View.VISIBLE);
                            addressLbl.setVisibility(View.VISIBLE);
                            phoneLbl.setVisibility(View.VISIBLE);
                            websiteLbl.setVisibility(View.VISIBLE);
                            commentLbl.setVisibility(View.VISIBLE);

                            progressDialog.dismiss();
                        }

                        @Override
                        public void error() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error recibiendo los datos. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void noRecommendation() {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "No existe esta recomendación.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void error(String message) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error recibiendo los datos. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                }
            });

            //Si la recomendacion es mia, poner icono del menu
            firebaseDatabaseController.existsRecommendation(uid, idPlace, new FirebaseDatabaseController.ExistsRecommendationResponse() {
                @Override
                public void exists() {
                    menu.findItem(R.id.action_add_fav).setVisible(false);
                    menu.findItem(R.id.action_rmv_fav).setVisible(true);
                }

                @Override
                public void doNotExists() {
                    menu.findItem(R.id.action_add_fav).setVisible(true);
                    menu.findItem(R.id.action_rmv_fav).setVisible(false);
                }

                @Override
                public void error() {
                    menu.findItem(R.id.action_add_fav).setVisible(true);
                    menu.findItem(R.id.action_rmv_fav).setVisible(false);
                }
            });
        } catch (NullPointerException en) {
            try {
                startActivityForResult(googlePlacesController.getPlacePickerBuilder().build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                Toast.makeText(getApplicationContext(), "No se puede conectar con google. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                finish();
            } catch (GooglePlayServicesNotAvailableException e) {
                Toast.makeText(getApplicationContext(), "No se puede conectar con google. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_interest_point, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_fav:
                createRecommendationDialog();
                break;

            case R.id.action_rmv_fav:
                deleteRecommendationDialog();
                break;

            case android.R.id.home:
                    finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteRecommendationDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        firebaseDatabaseController.deleteRecommendation(place.getId(), uid, new FirebaseDatabaseController.DeleteRecommendationResponse() {
                            @Override
                            public void success() {
                                Toast.makeText(getApplicationContext(), "Recomendación eliminada correctamente", Toast.LENGTH_LONG).show();
                                menu.findItem(R.id.action_add_fav).setVisible(true);
                                menu.findItem(R.id.action_rmv_fav).setVisible(false);
                            }

                            @Override
                            public void error() {
                                Toast.makeText(getApplicationContext(), "Error eliminando recomendación", Toast.LENGTH_LONG).show();
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Estás seguro de que quieres eliminar este lugar de tus puntos de interés?")
                .setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void createRecommendationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_create_recommendation, null);

        builder.setView(v);

        final EditText commentTxt = v.findViewById(R.id.editText);
        final CheckBox visitedCheckBox = v.findViewById(R.id.visitedCheckbox);
        Button aceptarBtn = v.findViewById(R.id.aceptarBtn);
        Button cancelarBtn = v.findViewById(R.id.cancelBtn);

        aceptarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (place == null) {
                    Toast.makeText(getApplicationContext(), "Debes seleccionar un punto de interés, vuelva a interntarlo", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    firebaseDatabaseController.getUserName(uid, new FirebaseDatabaseController.GetUserNameResponse() {
                        @Override
                        public void success(String userName) {
                            Recommendation recommendation = new Recommendation(place.getId(), place.getName().toString(), uid, userName, commentTxt.getText().toString(), Calendar.getInstance().getTime(),visitedCheckBox.isChecked());
                            firebaseDatabaseController.storeRecommendation(recommendation, new FirebaseDatabaseController.StoreRecommendationResponse() {
                                @Override
                                public void success() {
                                    Toast.makeText(getApplicationContext(), "Recomendación creada correctamente", Toast.LENGTH_LONG).show();
                                    menu.findItem(R.id.action_add_fav).setVisible(false);
                                    menu.findItem(R.id.action_rmv_fav).setVisible(true);
                                }

                                @Override
                                public void error() {
                                    Toast.makeText(getApplicationContext(), "Error creando recomendación", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void error() {
                            Toast.makeText(getApplicationContext(), "Error creando recomendación", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                dialog.dismiss();
            }
        });

        cancelarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void findViews() {
        nameTxt = findViewById(R.id.nameTxt);
        addressTxt = findViewById(R.id.direccionTxt);
        phoneTxt = findViewById(R.id.numeroTelefonoTxt);
        websiteTxt = findViewById(R.id.paginaWebTxt);
        commentTxt = findViewById(R.id.commentTxt);
        nameLbl = findViewById(R.id.nameLbl);
        addressLbl = findViewById(R.id.direccionLbl);
        phoneLbl = findViewById(R.id.numeroTelefonoLbl);
        websiteLbl = findViewById(R.id.paginaWebLbl);
        commentLbl = findViewById(R.id.commentLbl);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);

        nameTxt.setVisibility(View.GONE);
        addressTxt.setVisibility(View.GONE);
        phoneTxt.setVisibility(View.GONE);
        websiteTxt.setVisibility(View.GONE);
        commentTxt.setVisibility(View.GONE);
        nameLbl.setVisibility(View.GONE);
        addressLbl.setVisibility(View.GONE);
        phoneLbl.setVisibility(View.GONE);
        websiteLbl.setVisibility(View.GONE);
        commentLbl.setVisibility(View.GONE);

    }

    public void onMapReady(GoogleMap map) {
        mMap = map;
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //Retrieve the place that the user has selected
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = googlePlacesController.getPlace(getApplicationContext(), data);

                firebaseDatabaseController.getRecommendation(uid.concat(place.getId()), new FirebaseDatabaseController.GetRecommendationResponse() {
                    @Override
                    public void success(Recommendation recommendation) {
                        menu.findItem(R.id.action_add_fav).setVisible(false);
                        menu.findItem(R.id.action_rmv_fav).setVisible(true);

                        mMap.moveCamera( CameraUpdateFactory.newLatLngBounds(place.getViewport(), 0));
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()));

                        nameTxt.setText(place.getName());
                        addressTxt.setText(place.getAddress());
                        if (place.getPhoneNumber() == null || place.getPhoneNumber().toString().isEmpty()) {
                            phoneTxt.setText("Desconocido");
                        } else {
                            phoneTxt.setText(place.getPhoneNumber());
                        }
                        if (place.getWebsiteUri() == null || place.getWebsiteUri().toString().isEmpty()) {
                            websiteTxt.setText("Desconocida");
                        } else {
                            websiteTxt.setText(place.getWebsiteUri().toString());
                        }
                        if (recommendation.getComment() == null || recommendation.getComment().isEmpty()) {
                            commentTxt.setText("No hay comentario");
                        } else {
                            commentTxt.setText(recommendation.getComment());
                        }

                        nameTxt.setVisibility(View.VISIBLE);
                        addressTxt.setVisibility(View.VISIBLE);
                        phoneTxt.setVisibility(View.VISIBLE);
                        websiteTxt.setVisibility(View.VISIBLE);
                        commentTxt.setVisibility(View.VISIBLE);
                        nameLbl.setVisibility(View.VISIBLE);
                        addressLbl.setVisibility(View.VISIBLE);
                        phoneLbl.setVisibility(View.VISIBLE);
                        websiteLbl.setVisibility(View.VISIBLE);
                        commentLbl.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void noRecommendation() {
                        menu.findItem(R.id.action_add_fav).setVisible(true);
                        menu.findItem(R.id.action_rmv_fav).setVisible(false);

                        mMap.moveCamera( CameraUpdateFactory.newLatLngBounds(place.getViewport(), 0));
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()));

                        nameTxt.setText(place.getName());
                        addressTxt.setText(place.getAddress());
                        if (place.getPhoneNumber() == null || place.getPhoneNumber().toString().isEmpty()) {
                            phoneTxt.setText("Desconocido");
                        } else {
                            phoneTxt.setText(place.getPhoneNumber());
                        }
                        if (place.getWebsiteUri() == null || place.getWebsiteUri().toString().isEmpty()) {
                            websiteTxt.setText("Desconocida");
                        } else {
                            websiteTxt.setText(place.getWebsiteUri().toString());
                        }

                        nameTxt.setVisibility(View.VISIBLE);
                        addressTxt.setVisibility(View.VISIBLE);
                        phoneTxt.setVisibility(View.VISIBLE);
                        websiteTxt.setVisibility(View.VISIBLE);
                        nameLbl.setVisibility(View.VISIBLE);
                        addressLbl.setVisibility(View.VISIBLE);
                        phoneLbl.setVisibility(View.VISIBLE);
                        websiteLbl.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void error(String message) {
                        Toast.makeText(getApplicationContext(), "Error recibiendo los datos del servidor. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                    }
                });
                /*firebaseDatabaseController.existsRecommendation(uid, place.getId(), new FirebaseDatabaseController.ExistsRecommendationResponse() {
                    @Override
                    public void exists() {
                        menu.findItem(R.id.action_add_fav).setVisible(false);
                        menu.findItem(R.id.action_rmv_fav).setVisible(true);

                        mMap.moveCamera( CameraUpdateFactory.newLatLngBounds(place.getViewport(), 0));
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()));

                        nameTxt.setText(place.getName());
                        addressTxt.setText(place.getAddress());
                        if (place.getPhoneNumber() == null || place.getPhoneNumber().toString().isEmpty()) {
                            phoneTxt.setText("Desconocido");
                        } else {
                            phoneTxt.setText(place.getPhoneNumber());
                        }
                        if (place.getWebsiteUri() == null || place.getWebsiteUri().toString().isEmpty()) {
                            websiteTxt.setText("Desconocida");
                        } else {
                            websiteTxt.setText(place.getWebsiteUri().toString());
                        }

                        nameTxt.setVisibility(View.VISIBLE);
                        addressTxt.setVisibility(View.VISIBLE);
                        phoneTxt.setVisibility(View.VISIBLE);
                        websiteTxt.setVisibility(View.VISIBLE);
                        nameLbl.setVisibility(View.VISIBLE);
                        addressLbl.setVisibility(View.VISIBLE);
                        phoneLbl.setVisibility(View.VISIBLE);
                        websiteLbl.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void doNotExists() {
                        menu.findItem(R.id.action_add_fav).setVisible(true);
                        menu.findItem(R.id.action_rmv_fav).setVisible(false);

                        mMap.moveCamera( CameraUpdateFactory.newLatLngBounds(place.getViewport(), 0));
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()));

                        nameTxt.setText(place.getName());
                        addressTxt.setText(place.getAddress());
                        if (place.getPhoneNumber() == null || place.getPhoneNumber().toString().isEmpty()) {
                            phoneTxt.setText("Desconocido");
                        } else {
                            phoneTxt.setText(place.getPhoneNumber());
                        }
                        if (place.getWebsiteUri() == null || place.getWebsiteUri().toString().isEmpty()) {
                            websiteTxt.setText("Desconocida");
                        } else {
                            websiteTxt.setText(place.getWebsiteUri().toString());
                        }

                        nameTxt.setVisibility(View.VISIBLE);
                        addressTxt.setVisibility(View.VISIBLE);
                        phoneTxt.setVisibility(View.VISIBLE);
                        websiteTxt.setVisibility(View.VISIBLE);
                        nameLbl.setVisibility(View.VISIBLE);
                        addressLbl.setVisibility(View.VISIBLE);
                        phoneLbl.setVisibility(View.VISIBLE);
                        websiteLbl.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void error() {
                        Toast.makeText(getApplicationContext(), "Error recibiendo los datos del servidor. Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        }
    }
}
