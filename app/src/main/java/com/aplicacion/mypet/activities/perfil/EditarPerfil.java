package com.aplicacion.mypet.activities.perfil;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditarPerfil extends AppCompatActivity {
    private Double latitude;
    private Double longitude;
    private final int REQUEST_CODE_UBICACION = 5;
    Button botonUbicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        latitude = null;
        longitude = null;

        botonUbicacion = findViewById(R.id.boton_marcar_ubicacion);
    }

    public void cerrarEditarPerfil(View view) {
        finish();
    }

    public void cambiarUbicacion(View view) {
        Intent ubicacion = new Intent(this,Ubicacion.class);
        if (latitude!=null && longitude!=null){
            ubicacion.putExtra("latitude", latitude);
            ubicacion.putExtra("longitude", longitude);
        }
        startActivityForResult(ubicacion, REQUEST_CODE_UBICACION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==REQUEST_CODE_UBICACION){
            if (resultCode== Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                latitude = (double) extras.get("latitude");
                longitude = (double) extras.get("longitude");

                localizacion(latitude,longitude);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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

        String city = direccion.get(0).getLocality();
        String postalCode = direccion.get(0).getPostalCode();

        botonUbicacion.setText(city +", " + postalCode);
    }
}