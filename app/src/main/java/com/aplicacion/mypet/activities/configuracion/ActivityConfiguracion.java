package com.aplicacion.mypet.activities.configuracion;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;

public class ActivityConfiguracion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
    }

    public void cerrarConfiguracion(View view) {
        finish();
    }
}