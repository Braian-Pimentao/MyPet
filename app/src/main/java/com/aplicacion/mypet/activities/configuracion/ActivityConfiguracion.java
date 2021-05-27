package com.aplicacion.mypet.activities.configuracion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.perfil.EditarPerfil;
import com.aplicacion.mypet.providers.AuthProvider;

public class ActivityConfiguracion extends AppCompatActivity {
    private LinearLayout linearSesion;
    private AuthProvider auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        auth = new AuthProvider();

        linearSesion = findViewById(R.id.linear_conf_perfil);
        if (auth.getAuth().getCurrentUser() == null) {
            linearSesion.setVisibility(View.INVISIBLE);
        }
    }

    public void cerrarConfiguracion(View view) {
        finish();
    }

    public void cerrarSesion(View view){
        if (auth.getAuth().getCurrentUser() != null) {
            auth.getAuth().signOut();
            Toast.makeText(this, getString(R.string.sesion_cerrada), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void editarPerfil(View view){
        Intent editarPerfil = new Intent(this, EditarPerfil.class);
        startActivity(editarPerfil);
    }
}