package com.example.janrodriguez.picturethis.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.janrodriguez.picturethis.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends BaseGameActivity implements OnMapReadyCallback {

    static public final String INTENT_SHOW_RADIUS = "showRadius";
    static public final String INTENT_LATITUDE = "latitude";
    static public final String INTENT_LONGITUDE = "longitude";

    static private final String TAG = "MapActivity";
    static private final double noiseMax = 0.001;
    static private final int radius = 200; // meters

    private boolean showRadius;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            showRadius = extras.getBoolean(INTENT_SHOW_RADIUS);
            latitude = extras.getDouble(INTENT_LATITUDE);
            longitude = extras.getDouble(INTENT_LONGITUDE);
        } else {
            Log.e(TAG, "Called map activity without any arguments.");
        }
    }

    private double randomizeValue(double value) {
        double randomNoise = Math.random() * noiseMax;
        return Math.random() > 0.5 ? value + randomNoise : value - randomNoise;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng;

        if (!isAnyLocationProviderEnabled()) {
            showEnableProviderAlert();
        }

        if (showRadius) {
            latLng = new LatLng(randomizeValue(latitude), randomizeValue(longitude));

            CircleOptions circleOptions = new CircleOptions()
                    .strokeColor(0xFF00FF00)
                    .strokeWidth(2)
                    .fillColor(0x2800FF00)
                    .center(latLng)
                    .radius(radius);
            Circle circle = googleMap.addCircle(circleOptions);
        } else {
            latLng = new LatLng(latitude, longitude);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.setMyLocationEnabled(true);
    }

    private boolean isAnyLocationProviderEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showEnableProviderAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MapActivity.this);

        dialog.setMessage(getString(R.string.enable_location_providers));

        dialog.setPositiveButton(getString(R.string.change_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {}
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
