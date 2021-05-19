package com.aplicacion.mypet.activities.perfil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Ubicacion extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener {
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
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMarkerDragListener(this);
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


    private void localizacion(double latitude, double longitude){
        Geocoder geocoder;
        List<Address> direccion = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            direccion = geocoder.getFromLocation(latitude, longitude, 1); // 1 representa la cantidad de resultados a obtener
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = direccion.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = direccion.get(0).getLocality();
        String state = direccion.get(0).getAdminArea();
        String country = direccion.get(0).getCountryName();
        String postalCode = direccion.get(0).getPostalCode();

        Toast.makeText(this, "Current location:\n" + address, Toast.LENGTH_LONG).show();
    }





    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location.getLatitude() +"\n" + location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        ubicacionMarca.setLatitude(marker.getPosition().latitude);
        ubicacionMarca.setLongitude(marker.getPosition().longitude);
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