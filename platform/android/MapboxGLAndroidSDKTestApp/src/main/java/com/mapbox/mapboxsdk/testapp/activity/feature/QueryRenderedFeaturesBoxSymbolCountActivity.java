package com.mapbox.mapboxsdk.testapp.activity.feature;

import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.RawRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.testapp.R;
import com.mapbox.services.commons.geojson.Feature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

/**
 * Demo's query rendered features on Symbols
 */
public class QueryRenderedFeaturesBoxSymbolCountActivity extends AppCompatActivity {
    private static final String TAG = QueryRenderedFeaturesBoxSymbolCountActivity.class.getSimpleName();

    public MapView mapView;
    private MapboxMap mapboxMap;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_features_box);
        setupActionBar();

        final float density = getResources().getDisplayMetrics().density;

        final View selectionBox = findViewById(R.id.selection_box);

        //Initialize map as normal
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                QueryRenderedFeaturesBoxSymbolCountActivity.this.mapboxMap = mapboxMap;

                //Add a symbol layer (also works with annotations)
                try {
                    mapboxMap.addSource(new GeoJsonSource("symbols-source", readRawResource(R.raw.test_points_utrecht)));
                } catch (IOException e) {
                    Log.e(TAG, "Could not load geojson: " + e.getMessage());
                    return;
                }
                mapboxMap.addImage("test-icon", BitmapFactory.decodeResource(getResources(), R.drawable.default_marker));
                mapboxMap.addLayer(new SymbolLayer("symbols-layer", "symbols-source").withProperties(iconImage("test-icon")));


                selectionBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Query
                        int top = selectionBox.getTop() - mapView.getTop();
                        int left = selectionBox.getLeft() - mapView.getLeft();
                        RectF box = new RectF(left, top, left + selectionBox.getWidth(), top + selectionBox.getHeight());
                        Log.i(TAG, String.format("Querying box %s", box));
                        List<Feature> features = mapboxMap.queryRenderedFeatures(box, "symbols-layer");

                        //Show count
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(
                                QueryRenderedFeaturesBoxSymbolCountActivity.this,
                                String.format("%s features in box", features.size()),
                                Toast.LENGTH_SHORT);
                        toast.show();

                    }
                });

                //Little taste of home
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.0907, 5.1214), 16));
            }
        });

    }

    private String readRawResource(@RawRes int rawResource) throws IOException {
        InputStream is = getResources().openRawResource(rawResource);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        return writer.toString();
    }

    public MapboxMap getMapboxMap() {
        return mapboxMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

}
