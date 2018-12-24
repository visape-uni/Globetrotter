package upc.fib.victor.globetrotter.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class GooglePlacesController {

    private GeoDataClient mGeoDateClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    public GooglePlacesController(Activity activity) {
        mGeoDateClient = Places.getGeoDataClient(activity);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(activity);
    }

    public PlacePicker.IntentBuilder getPlacePickerBuilder () {
        return new PlacePicker.IntentBuilder();
    }

    public Place getPlace(Context context, Intent intent) {
        return PlacePicker.getPlace(context, intent);
    }
}
