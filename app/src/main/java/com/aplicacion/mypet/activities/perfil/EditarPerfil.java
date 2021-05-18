package com.aplicacion.mypet.activities.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;

public class EditarPerfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
    }

    public void cerrarEditarPerfil(View view) {
        finish();
    }

    public void cambiarUbicacion(View view) {
        Intent ubicacion = new Intent(this,Ubicacion.class);
        startActivity(ubicacion);
    }
}