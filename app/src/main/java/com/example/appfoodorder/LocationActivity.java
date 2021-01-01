package com.example.appfoodorder;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appfoodorder.Common.Common;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ImageButton btnBack;
    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get intent
        if(getIntent()!=null){
            address = getIntent().getStringExtra("Address");
        }
        if(address != null){
            if (Common.isConnectedToInternet(getBaseContext())){
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
            else {
                Toast.makeText(this, "No internet, please check the connection", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng jei = new LatLng(10.866879, 106.801950);
        mMap.addMarker(new MarkerOptions().position(jei).title("Just Eat It").icon(BitmapDescriptorFactory.defaultMarker()));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(jei).zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        List<Address> addressList;
        MarkerOptions orderMarker = new MarkerOptions();

        if (!TextUtils.isEmpty(address)) {
            Geocoder geocoder = new Geocoder(this);

            try {
                addressList = geocoder.getFromLocationName(address,6);
                if (addressList!=null){
                    for (int i=0; i<addressList.size();i++){
                        Address orderAddress = addressList.get(i);
                        LatLng latLng = new LatLng(orderAddress.getLatitude(),orderAddress.getLongitude());
                        orderMarker.position(latLng).title("Order's position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        mMap.addMarker(orderMarker);
                        CameraPosition NewcameraPosition = new CameraPosition.Builder().target(latLng).zoom(10).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(NewcameraPosition));
                    }
                } else {
                    Toast.makeText(this, "Location not found ...", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this, "There is no address", Toast.LENGTH_SHORT).show();
        }
    }
}
