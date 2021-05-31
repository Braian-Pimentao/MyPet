package com.aplicacion.mypet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.sesion.IniciarSesion;
import com.aplicacion.mypet.adaptadores.AdaptadorPublicacion;
import com.aplicacion.mypet.models.Publicacion;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.FavoritoProvider;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class FragmentFavorito extends Fragment {
    private FavoritoProvider favoritoProvider;
    private AuthProvider authProvider;
    private PublicacionProvider publicacionProvider;

    private AdaptadorPublicacion adaptadorPublicacion;
    private RecyclerView recyclerView;

    private TextView textLinearLayout;
    private Button botonIniciar;
    private LinearLayout linearLayoutNoFavoritos;

    private ListenerRegistration listener;
    public FragmentFavorito() {
        // Required empty public constructor



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorito, container, false);

        favoritoProvider = new FavoritoProvider();
        authProvider = new AuthProvider();
        publicacionProvider = new PublicacionProvider();
        recyclerView = view.findViewById(R.id.listar_anuncios_favoritos);

        textLinearLayout = view.findViewById(R.id.text_no_favoritos);
        botonIniciar = view.findViewById(R.id.boton_iniciar_sesion_favoritos);
        botonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iniciarSesion = new Intent(getContext(), IniciarSesion.class);
                startActivity(iniciarSesion);
            }
        });
        linearLayoutNoFavoritos = view.findViewById(R.id.mensaje_informativo_favorito);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (authProvider.getAuth().getCurrentUser()!= null) {
            listener = favoritoProvider.getFavoritesByUser(authProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.size()== 0) {
                        botonIniciar.setVisibility(View.GONE);
                        textLinearLayout.setText(getString(R.string.sin_favoritos));
                        linearLayoutNoFavoritos.setVisibility(View.VISIBLE);
                    } else {
                        linearLayoutNoFavoritos.setVisibility(View.GONE);
                    }

                    ArrayList<String> idsPublicaciones = new ArrayList<>();
                    for (DocumentSnapshot d : value) {
                        if (d.contains("idPublicacion")) {
                            idsPublicaciones.add(d.getString("idPublicacion"));
                        }
                    }

                    if (!idsPublicaciones.isEmpty())
                        obtenerFavoritos(idsPublicaciones);
                    else {
                        recyclerView = null;
                        if (adaptadorPublicacion!= null) {
                            adaptadorPublicacion.stopListening();
                        }
                    }
                }
            });
        } else {
            textLinearLayout.setText(getString(R.string.no_iniciado_sesion));
            botonIniciar.setVisibility(View.VISIBLE);
            linearLayoutNoFavoritos.setVisibility(View.VISIBLE);
        }

    }

    private void obtenerFavoritos(ArrayList<String> idsPublicaciones) {
        Query query = publicacionProvider.getPublicacionByFavoriteUser(idsPublicaciones);
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query,Publicacion.class)
                .build();
        adaptadorPublicacion = new AdaptadorPublicacion(options,getContext());
        adaptadorPublicacion.notifyDataSetChanged();
        recyclerView.setAdapter(adaptadorPublicacion);
        adaptadorPublicacion.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adaptadorPublicacion!= null) {
            adaptadorPublicacion.stopListening();
        }
        if (listener != null) {
            listener.remove();
        }
    }
}