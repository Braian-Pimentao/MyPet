package com.example.mypet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> listaDatos;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.lista_anuncios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaDatos = new ArrayList<>();

        for (int i = 0; i <=50 ; i++) {
            listaDatos.add("Dato: " +i);
        }

        AdaptadorDatos adaptadorDatos = new AdaptadorDatos(listaDatos);
        recyclerView.setAdapter(adaptadorDatos);
    }
}