package com.aplicacion.mypet.activities.configuracion;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;

public class ActivityInformacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
    }

    public void cerrarInformacion(View view) {
        finish();
    }
}