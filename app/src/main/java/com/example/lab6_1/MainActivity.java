package com.example.lab6_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaSession2;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends FragmentActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private static final int PRIORITY_HIGH_ACCURACY = 13;//
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();//
    private final LatLng m = new LatLng(43.0757378, -89.4061951);
    private LatLngBounds mB = new LatLngBounds(
            new LatLng(40, -90), new LatLng(50, -90));
    private GoogleMap mMap;
    private FusedLocationProviderClient mF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            googleMap.addMarker(new MarkerOptions().position(m).title("Destination"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mB.getCenter(), 5));
            createLocationRequest();
            displayLocation();
        });

        mF = LocationServices.getFusedLocationProviderClient(this);
    }
    //
    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    //

    private void displayLocation() {
        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_DENIED) {
            createLocationRequest();
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //if permission granted, display marker at current
        else {
            mF.getLastLocation().addOnCompleteListener(this, task -> {
                createLocationRequest();
                Location mL = task.getResult();

//                LatLng now = new LatLng(mL.getLatitude(),mL.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(now).title("now"));
                Log.i("HI","12222222222222222");
                if (task.isSuccessful() && mL!=null){
                    LatLng now = new LatLng(mL.getLatitude(),mL.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(now).title("now"));
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(mL.getLatitude(),mL.getLongitude()),m));
                    polyline.setTag("Route");
                    Log.i("HI","111111111111111");
                }
                if (mL == null) Log.i("ERR","Location is Null");
                if (!task.isSuccessful()) Log.i("ERR1","Task unsuccessful");
            });
        }
    }

    //handles the result of the request for location permssion
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        createLocationRequest();
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayLocation();
            }
        }

    }
}