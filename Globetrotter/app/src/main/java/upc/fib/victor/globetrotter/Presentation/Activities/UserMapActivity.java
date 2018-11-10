package upc.fib.victor.globetrotter.Presentation.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.carto.core.MapPos;
import com.carto.core.MapRange;
import com.carto.datasources.LocalVectorDataSource;
import com.carto.graphics.Color;
import com.carto.layers.CartoBaseMapStyle;
import com.carto.layers.CartoOnlineVectorTileLayer;
import com.carto.layers.VectorLayer;
import com.carto.projections.Projection;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.ui.MapView;
import com.carto.vectorelements.Marker;

import upc.fib.victor.globetrotter.R;

public class UserMapActivity extends AppCompatActivity {

    final String LICENSE = "bd12140a113a4ea2dc3e18b39e5eb0fad536cdda";
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        MapView.registerLicense(LICENSE, getApplicationContext());
        mapView = findViewById(R.id.mapView);
        CartoOnlineVectorTileLayer baseLayer = new CartoOnlineVectorTileLayer(CartoBaseMapStyle.CARTO_BASEMAP_STYLE_VOYAGER);
        mapView.getLayers().add(baseLayer);

        Projection proj = mapView.getOptions().getBaseProjection();

        //Initialize a vector data source where to put the elements
        LocalVectorDataSource vectorDataSource1 = new LocalVectorDataSource(proj);

        //Initialize a vector layer with the provioud data source
        VectorLayer vectorLayer1 = new VectorLayer(vectorDataSource1);

        //Add the previous vector layer to the map
        mapView.getLayers().add(vectorLayer1);

        //Set limited visible zoom range for the vecto layer (optional)
        vectorLayer1.setVisibleZoomRange(new MapRange(10, 24));

        //Create marker style
        MarkerStyleBuilder markerStyleBuilder = new MarkerStyleBuilder();

        markerStyleBuilder.setSize(30);
        markerStyleBuilder.setColor(new Color(0xFF00FF00)); //Green

        MarkerStyle markerStyle1 = markerStyleBuilder.buildStyle();

        //Add marker
        MapPos pos1 = proj.fromWgs84(new MapPos(24.646469, 59.426939)); //Tallinn
        Marker marker1 = new Marker(pos1, markerStyle1);

        //Add the marker to the datasource
        vectorDataSource1.add(marker1);
    }
}
