package com.example.alzguardpage1;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.alzguardpage1.databinding.ActivityMapsBinding;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MaisonActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    private ActivityMapsBinding binding;
    private FirebaseDatabase database ;
    private DatabaseReference dbReference;
    private Button find_location_btn;

    private static final String TAG = "MapsActivity"; // for debugging
    public Double locationLat = 0.0;
    public Double locationLong = 0.0;

    private static final float GEOFENCE_RADIUS = 1000; // 1 km in meters
    private GeofencingClient geofencingClient;
    private Geofence geofence;
    private PendingIntent geofencePendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userId = getIntent().getStringExtra("userId");
        database = FirebaseDatabase.getInstance("https://alzguardpage1-default-rtdb.europe-west1.firebasedatabase.app");
        dbReference = database.getReference("patient/" + userId + "/location");

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        find_location_btn = findViewById(R.id.btn_find_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Get a reference from the database so that the app can read and write operations
        dbReference.addValueEventListener(locListener);


    }

    ValueEventListener locListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String userId = getIntent().getStringExtra("userId");
            if (snapshot.exists()) {
                Log.e("userId",userId);
                Double locationLat = snapshot.child("latitude").getValue(Double.class);
                Double locationLong = snapshot.child("longitude").getValue(Double.class);
                find_location_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (locationLat != null && locationLong != null) {
                            // Create a LatLng object from location
                            LatLng latLng = new LatLng(locationLat, locationLong);

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .title("The user is currently here")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));


                            // Create a marker at the read location and display it on the map
                            mMap.addMarker(markerOptions);

                            // Create a geofence around the retrieved location

                            // Specify how the map camera is updated
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f);
                            // Update the camera with the CameraUpdate object
                            mMap.moveCamera(update);
                            geofence = new Geofence.Builder()
                                    .setRequestId("geofence")
                                    .setCircularRegion(locationLat, locationLong, GEOFENCE_RADIUS)
                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                            Geofence.GEOFENCE_TRANSITION_EXIT)
                                    .build();

                        } else {
                            // If location is null , log an error message
                            Log.e(TAG, "User location cannot be found");
                        }
                    }
                });
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getApplicationContext(), "Could not read from database", Toast.LENGTH_LONG).show();

        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        geofencingClient = LocationServices.getGeofencingClient(this);



        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));

        // Create a marker at the read location and display it on the map
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Geofence added");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to add geofence", e);
                    }
                });
    }
    private GeofencingRequest getGeofencingRequest() {
        geofence = new Geofence.Builder()
                .setRequestId("geofence")
                .setCircularRegion(locationLat, locationLong, GEOFENCE_RADIUS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

}