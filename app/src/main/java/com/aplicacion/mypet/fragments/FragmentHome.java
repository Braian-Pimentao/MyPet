package com.aplicacion.mypet.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.publicar.FiltrosActivity;
import com.aplicacion.mypet.adaptadores.AdaptadorPublicacion;
import com.aplicacion.mypet.models.Publicacion;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

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
    private Slider slider;

    private LinearLayout linearLayoutNoBusqueda;

    private ArrayList<String> ids;

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

        linearLayoutNoBusqueda = view.findViewById(R.id.mensaje_informativo_home);

        imagenPerro = view.findViewById(R.id.imagen_perro);
        imagenGato = view.findViewById(R.id.imagen_gato);
        imagenConejo = view.findViewById(R.id.imagen_conejo);
        imagenRoedor = view.findViewById(R.id.imagen_roedor);
        imagenTortuga = view.findViewById(R.id.imagen_tortuga);
        imagenPez = view.findViewById(R.id.imagen_pez);
        imagenPajaro = view.findViewById(R.id.imagen_pajaro);
        imagenReptil = view.findViewById(R.id.imagen_reptil);
        searchBar = view.findViewById(R.id.searchBar);
        slider = view.findViewById(R.id.slider_kilometros);


        imagenPerro.setOnClickListener(pulsarAnimal);
        imagenGato.setOnClickListener(pulsarAnimal);
        imagenConejo.setOnClickListener(pulsarAnimal);
        imagenRoedor.setOnClickListener(pulsarAnimal);
        imagenTortuga.setOnClickListener(pulsarAnimal);
        imagenPez.setOnClickListener(pulsarAnimal);
        imagenPajaro.setOnClickListener(pulsarAnimal);
        imagenReptil.setOnClickListener(pulsarAnimal);
        searchBar.setOnSearchActionListener(this);

        ids = new ArrayList<>();

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                getPublicacionesUbicacion(value);
            }
        });

        return view;


    }


    @Override
    public void onStart() {
        super.onStart();
        if (checkHighAccuracyLocationMode(getActivity()) && !getPermision())
            getPublicacionesUbicacion(slider.getValue());
        else {
            getTodasPublicaciones();
            slider.setVisibility(View.GONE);
        }
    }

    private boolean getPermision() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void getPublicacionesUbicacion(float value) {
        ids.clear();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ArrayList<Double> ubicacion = new ArrayList<>();
        ubicacion.add(location.getLatitude());
        ubicacion.add(location.getLongitude());

        publicacionProvider.getIDPostByUbication(ubicacion, value).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDocumentEntered(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {
                ids.add(documentSnapshot.getId());
            }

            @Override
            public void onDocumentExited(@NotNull DocumentSnapshot documentSnapshot) {

            }

            @Override
            public void onDocumentMoved(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {

            }

            @Override
            public void onDocumentChanged(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {

            }

            @Override
            public void onGeoQueryReady() {
                if (ids.size()> 0) {
                    System.out.println("--------------------------holaaa----------------------"+ids);
                    linearLayoutNoBusqueda.setVisibility(View.GONE);
                    Query query = publicacionProvider.getPublicacionByUbication(ids);
                    FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                            .setQuery(query,Publicacion.class)
                            .build();

                    adaptadorPublicacion = new AdaptadorPublicacion(options,getContext());
                    adaptadorPublicacion.notifyDataSetChanged();
                    recyclerView.setAdapter(adaptadorPublicacion);
                    adaptadorPublicacion.startListening();
                } else {
                    linearLayoutNoBusqueda.setVisibility(View.VISIBLE);
                    if (adaptadorPublicacion!= null) {
                        recyclerView.setAdapter(null);
                    }
                }
            }

            @Override
            public void onGeoQueryError(@NotNull Exception e) {

            }
        });

    }



    public static boolean checkHighAccuracyLocationMode(Context context) {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void getTodasPublicaciones() {
        Query query = publicacionProvider.getAll();

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.size()==0) {
                    linearLayoutNoBusqueda.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutNoBusqueda.setVisibility(View.GONE);
                }
            }
        });


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
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size()==0) {
                    linearLayoutNoBusqueda.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutNoBusqueda.setVisibility(View.GONE);
                }
            }
        });
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
            linearLayoutNoBusqueda.setVisibility(View.GONE);
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
