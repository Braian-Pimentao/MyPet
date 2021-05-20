package com.aplicacion.mypet.activities.perfil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.aplicacion.mypet.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Ubicacion extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private LocationManager locationManager;
    private Location ubicacionReal;

    private GoogleMap mMap;
    private Location ubicacionMarca;
    private Marker marcador;

    private LatLng latLngRecibida;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            latLngRecibida = new LatLng(extras.getDouble("latitude"), extras.getDouble("longitude"));
            ubicacionMarca = new Location("Marca");
        }

        ubicacionReal = new Location("Real");

    }

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
        mMap.setOnMapClickListener(this);


        if (latLngRecibida!=null) {
            ubicacionMarca.setLatitude(latLngRecibida.latitude);
            ubicacionMarca.setLatitude(latLngRecibida.longitude);
            marcador = mMap.addMarker(new MarkerOptions().position(latLngRecibida)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRecibida, 10));
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ubicacionReal = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void cerrarUbicacion(View view) {
        finish();
    }

    public void pulsarBoton(View view) {
        Button botonPulsado = (Button) view;

        if (botonPulsado.getId() == R.id.boton_gurdar_ubicacion) {
            if (locationManager!=null){
                Intent data = new Intent();
                data.putExtra("latitude",ubicacionReal.getLatitude());
                data.putExtra("longitude",ubicacionReal.getLongitude());
                setResult(Activity.RESULT_OK,data);
                finish();
            }
        } else if (botonPulsado.getId() == R.id.boton_gurdar_marca) {
            if (ubicacionMarca != null) {
                Intent data = new Intent();
                data.putExtra("latitude",ubicacionMarca.getLatitude());
                data.putExtra("longitude",ubicacionMarca.getLongitude());
                setResult(Activity.RESULT_OK,data);
                finish();
            }else{
                Toast.makeText(this, "Coloca un marcador", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (marcador != null){
            marcador.remove();
        }

        if (ubicacionMarca == null) {
            ubicacionMarca = new Location("Marca");
        }
        marcador = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true));
        ubicacionMarca.setLatitude(latLng.latitude);
        ubicacionMarca.setLongitude(latLng.longitude);
    }
}