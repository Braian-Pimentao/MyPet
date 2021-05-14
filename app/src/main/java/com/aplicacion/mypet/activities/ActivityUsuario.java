package com.aplicacion.mypet.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;

public class ActivityUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
    }

    public void cerrarPerfil(View v) {
        finish();
    }
}