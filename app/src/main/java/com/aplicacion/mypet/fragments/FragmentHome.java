package com.aplicacion.mypet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.publicar.FiltrosActivity;
import com.aplicacion.mypet.adaptadores.AdaptadorPublicacion;
import com.aplicacion.mypet.models.Publicacion;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class FragmentHome extends Fragment implements MaterialSearchBar.OnSearchActionListener {
    private View view;
    private RecyclerView recyclerView;
    private PublicacionProvider publicacionProvider;
    private AdaptadorPublicacion adaptadorPublicacion;
    private AdaptadorPublicacion adaptadorPublicacionBuscar;

    private ImageView imagenPerro;
    private ImageView imagenGato;
    private ImageView imagenConejo;
    private ImageView imagenRoedor;
    private ImageView imagenTortuga;
    private ImageView imagenPez;
    private ImageView imagenPajaro;
    private ImageView imagenReptil;
    private MaterialSearchBar searchBar;

    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.listar_anuncios);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        publicacionProvider = new PublicacionProvider();

        PulsarAnimal pulsarAnimal = new PulsarAnimal();

        imagenPerro = view.findViewById(R.id.imagen_perro);
        imagenGato = view.findViewById(R.id.imagen_gato);
        imagenConejo = view.findViewById(R.id.imagen_conejo);
        imagenRoedor = view.findViewById(R.id.imagen_roedor);
        imagenTortuga = view.findViewById(R.id.imagen_tortuga);
        imagenPez = view.findViewById(R.id.imagen_pez);
        imagenPajaro = view.findViewById(R.id.imagen_pajaro);
        imagenReptil = view.findViewById(R.id.imagen_reptil);
        searchBar = view.findViewById(R.id.searchBar);

        imagenPerro.setOnClickListener(pulsarAnimal);
        imagenGato.setOnClickListener(pulsarAnimal);
        imagenConejo.setOnClickListener(pulsarAnimal);
        imagenRoedor.setOnClickListener(pulsarAnimal);
        imagenTortuga.setOnClickListener(pulsarAnimal);
        imagenPez.setOnClickListener(pulsarAnimal);
        imagenPajaro.setOnClickListener(pulsarAnimal);
        imagenReptil.setOnClickListener(pulsarAnimal);
        searchBar.setOnSearchActionListener(this);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        getTodasPublicaciones();
    }

    private void getTodasPublicaciones() {
        Query query = publicacionProvider.getAll();

        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query,Publicacion.class)
                .build();

        adaptadorPublicacion = new AdaptadorPublicacion(options,getContext());
        adaptadorPublicacion.notifyDataSetChanged();
        recyclerView.setAdapter(adaptadorPublicacion);
        adaptadorPublicacion.startListening();
    }

    private void buscarPorRaza(String raza) {
        Query query = publicacionProvider.getPublicacionByRaza(raza);
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query,Publicacion.class)
                .build();

        adaptadorPublicacionBuscar = new AdaptadorPublicacion(options,getContext());
        adaptadorPublicacionBuscar.notifyDataSetChanged();
        recyclerView.setAdapter(adaptadorPublicacionBuscar);
        adaptadorPublicacionBuscar.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptadorPublicacion.stopListening();
        if (adaptadorPublicacionBuscar!=null) {
            adaptadorPublicacionBuscar.stopListening();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            getTodasPublicaciones();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        buscarPorRaza(text.toString());
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        Toast.makeText(getContext(),buttonCode,Toast.LENGTH_SHORT).show();
    }

    private class PulsarAnimal implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ImageView imagenPulsada = (ImageView) v;

            if (imagenPulsada.equals(imagenPerro)){
                filtrarPorAnimal("0");
            } else if (imagenPulsada.equals(imagenGato)){
                filtrarPorAnimal("1");
            } else if (imagenPulsada.equals(imagenConejo)){
                filtrarPorAnimal("2");
            } else if (imagenPulsada.equals(imagenRoedor)){
                filtrarPorAnimal("3");
            } else if (imagenPulsada.equals(imagenTortuga)){
                filtrarPorAnimal("4");
            } else if (imagenPulsada.equals(imagenPez)){
                filtrarPorAnimal("5");
            } else if (imagenPulsada.equals(imagenPajaro)){
                filtrarPorAnimal("6");
            } else if (imagenPulsada.equals(imagenReptil)){
                filtrarPorAnimal("7");
            }
        }
    }

    private void filtrarPorAnimal(String tipo) {
        Intent intentFiltrar = new Intent(getContext(), FiltrosActivity.class);
        intentFiltrar.putExtra("tipo", tipo);
        startActivity(intentFiltrar);
    }


}
