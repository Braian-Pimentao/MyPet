package com.aplicacion.mypet.activities.perfil;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.adaptadores.AdaptadorPublicacion;
import com.aplicacion.mypet.models.Publicacion;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.aplicacion.mypet.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityUsuario extends AppCompatActivity {

    private String nombre;
    private CircleImageView imagenPerfil;
    private TextView nombrePerfil;
    private TextView publicacionesContador;
    private String idUser;
    private RecyclerView recyclerView;


    private PublicacionProvider publicacionProvider;
    private AuthProvider auth;
    private UserProvider userProvider;
    private AdaptadorPublicacion adaptadorPublicacion;

    private LinearLayout noHayPublicaciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        auth = new AuthProvider();
        userProvider = new UserProvider();
        imagenPerfil = findViewById(R.id.imagen_perfil_user);
        nombrePerfil = findViewById(R.id.nombre_perfil_user);
        publicacionesContador = findViewById(R.id.numero_publicaciones);
        publicacionProvider = new PublicacionProvider();
        noHayPublicaciones = findViewById(R.id.mensaje_informativo_usuario);

        recyclerView = findViewById(R.id.listar_anuncios_usuario);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        idUser = getIntent().getStringExtra("idUser");


            getUser();
            getPostNumber();
    }

    private void getPostNumber() {
        publicacionProvider.getPostByUser(idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPublicaciones = queryDocumentSnapshots.size();
                publicacionesContador.setText(String.valueOf(numeroPublicaciones));

                if (numeroPublicaciones > 0) {
                    noHayPublicaciones.setVisibility(View.GONE);
                } else {
                    noHayPublicaciones.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getUser(){
        userProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")) {
                        nombre = documentSnapshot.getString("username");
                        nombrePerfil.setText(nombre);
                    }

                    if (documentSnapshot.contains("urlPerfil")) {
                        String urlFotoPerfil = documentSnapshot.getString("urlPerfil");
                        if (urlFotoPerfil != null) {
                            Picasso.get().load(urlFotoPerfil).into(imagenPerfil);
                        }
                    }

                } else {
                    Toast.makeText(ActivityUsuario.this, "No existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = publicacionProvider.getPostByUser(idUser);
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query,Publicacion.class)
                .build();

        adaptadorPublicacion = new AdaptadorPublicacion(options,this);
        recyclerView.setAdapter(adaptadorPublicacion);
        adaptadorPublicacion.startListening();
        if (auth.getAuth().getCurrentUser() != null)
            ViewedMessageHelper.updateOnline(true, ActivityUsuario.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptadorPublicacion.stopListening();
    }




    @Override
    protected void onPause() {
        super.onPause();
        if (auth.getAuth().getCurrentUser() != null) {
            ViewedMessageHelper.updateOnline(false, this);
        }
    }

    public void cerrarPerfil(View v) {
        finish();
    }
}