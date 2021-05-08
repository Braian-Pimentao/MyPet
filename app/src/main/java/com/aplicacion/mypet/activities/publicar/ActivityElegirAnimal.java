package com.aplicacion.mypet.activities.publicar;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;

public class ActivityElegirAnimal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegir_animal);
    }

    public void pulsar(View view) {
        Toast.makeText(this, "PRuebaa", Toast.LENGTH_LONG).show();
    }
}