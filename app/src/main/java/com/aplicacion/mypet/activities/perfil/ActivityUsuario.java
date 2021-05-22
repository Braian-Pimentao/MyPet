package com.aplicacion.mypet.activities.perfil;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityUsuario extends AppCompatActivity {

    private String nombre;
    private CircleImageView imagenPerfil;
    private TextView nombrePerfil;
    private TextView publicacionesContador;
    private PublicacionProvider publicacionProvider;
    private String idUser;

    private AuthProvider auth;
    private UserProvider userProvider;

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

        idUser = getIntent().getStringExtra("idUser");


            getUser();
            getPostNumber();
    }

    private void getPostNumber() {
        publicacionProvider.getPostByUser(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroPublicaciones = queryDocumentSnapshots.size();
                publicacionesContador.setText(String.valueOf(numeroPublicaciones));
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

    public void cerrarPerfil(View v) {
        finish();
    }
}