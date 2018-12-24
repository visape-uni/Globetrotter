package upc.fib.victor.globetrotter.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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

    public void getPlaceById (String idPlace, final GetPlaceByIdResponse getPlaceByIdResponse) {
        mGeoDateClient.getPlaceById(idPlace).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
            @Override
            public void onSuccess(PlaceBufferResponse places) {
                getPlaceByIdResponse.success(places.get(0));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getPlaceByIdResponse.error();
            }
        });
    }

    public interface GetPlaceByIdResponse {
        void success(Place place);
        void error();
    }
}
