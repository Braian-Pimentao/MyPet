package com.example.mypet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mypet.sesion.IniciarSesion;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> listaDatos;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        recyclerView = findViewById(R.id.lista_anuncios);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        listaDatos = new ArrayList<>();
//
//        for (int i = 0; i <=50 ; i++) {
//            listaDatos.add("Dato: " +i);
//        }
//
//        AdaptadorDatos adaptadorDatos = new AdaptadorDatos(listaDatos);
//        recyclerView.setAdapter(adaptadorDatos);


    }

    public void pulsarBoton(View v) {
        Intent items = new Intent(this, IniciarSesion.class);
        startActivity(items);
    }
}