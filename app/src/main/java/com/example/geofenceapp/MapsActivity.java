package com.example.geofenceapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "EddyMapsActivity";
    private GoogleMap mMap;
    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initMap();
        getLocationProviderClient();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getLocationProviderClient() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1020);
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void createHomeMarker() {
        try {
            List<Address> homeAddress = new Geocoder(this)
                    .getFromLocationName("329 E 197th Street, Bronx, NY 10458", 5);
            Address home = homeAddress.get(0);
            drawMarker(new LatLng(home.getLatitude(), home.getLongitude()), "The Crib");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getUserCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getUserCurrentLocation: No Permission");
        } else {
            locationProviderClient.getLastLocation()
                    .addOnSuccessListener(location ->
                            drawMarker(new LatLng(location.getLatitude(), location.getLongitude()), "User Location"));
        }
    }

    private void drawMarker(LatLng latLng, String title) {
        mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f));
    }

    private void createAndDisplayGeoFence(LatLng latLng) {
        GeoFenceCreator geoFenceCreator = new GeoFenceCreator(mMap,this);
        geoFenceCreator.initGeoFenceClient();
        geoFenceCreator.buildGeoFence(latLng);
        geoFenceCreator.createVisualGeoFence(latLng);
        geoFenceCreator.addGeoFenceToClient();
    }

    private void initOnMapLongPressListener() {
        mMap.setOnMapLongClickListener(latLng -> {
            createAndDisplayGeoFence(latLng);
            drawMarker(latLng, "Selected Location");
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestLocationPermission();
        createHomeMarker();
        getUserCurrentLocation();
        initOnMapLongPressListener();
    }
}
