package upc.fib.victor.globetrotter.Presentation.Activities;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

public class InterestPointActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView nameTxt;
    private TextView addressTxt;
    private TextView phoneTxt;
    private TextView websiteTxt;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;


    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    private final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_point);
        findViews();

        setTitle("Punto de Interés");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mGeoDataClient = Places.getGeoDataClient(this);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_interest_point, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_fav:
                break;

            case R.id.action_rmv_fav:
                break;

            case android.R.id.home:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        nameTxt = findViewById(R.id.nameTxt);
        addressTxt = findViewById(R.id.direccionTxt);
        phoneTxt = findViewById(R.id.numeroTelefonoTxt);
        websiteTxt = findViewById(R.id.paginaWebTxt);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);
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
            //mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //Retrieve the place that the user has selected
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                mMap.moveCamera( CameraUpdateFactory.newLatLngBounds(place.getViewport(), 0));
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()));

                nameTxt.setText(place.getName());
                addressTxt.setText(place.getAddress());
                if (place.getPhoneNumber() == null) {
                    phoneTxt.setText("Desconocido");
                } else {
                    phoneTxt.setText(place.getPhoneNumber());
                }
                if (place.getWebsiteUri() == null) {
                    websiteTxt.setText("Desconocida");
                } else {
                    websiteTxt.setText(place.getWebsiteUri().toString());
                }
            }
        }
    }
}
