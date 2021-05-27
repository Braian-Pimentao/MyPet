package com.aplicacion.mypet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.configuracion.ActivityConfiguracion;
import com.aplicacion.mypet.activities.perfil.ActivityUsuario;
import com.aplicacion.mypet.activities.sesion.IniciarSesion;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentPerfil extends Fragment {
    private View vista;

    private Button botonConfiguracion;
    private TextView nombrePerfil;
    private UserProvider userProvider;

    private LinearLayout layoutPerfil;

    private AuthProvider auth;
    private String nombre;
    private CircleImageView imagenPerfil;

    public FragmentPerfil() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_perfil, container, false);

        botonConfiguracion = vista.findViewById(R.id.configuracion);
        botonConfiguracion.setOnClickListener(new pulsarBoton());

        layoutPerfil = vista.findViewById(R.id.banner_perfil);

        imagenPerfil = vista.findViewById(R.id.foto_fragment_perfil);
        layoutPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getAuth().getCurrentUser()!=null){
                    Intent perfil = new Intent(getContext(), ActivityUsuario.class);
                    perfil.putExtra("idUser",auth.getUid());
                    startActivity(perfil);
                } else {
                    Intent iniciarSesion = new Intent(getContext(), IniciarSesion.class);
                    startActivity(iniciarSesion);
                }
            }
        });

        nombrePerfil = vista.findViewById(R.id.nombre_perfil);



        auth = new AuthProvider();
        userProvider = new UserProvider();

        if (auth.getAuth().getCurrentUser()!=null) {
            getUser();
        }
        return vista;
    }



    private class pulsarBoton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Button botonPulsado = (Button) v;
            if (botonPulsado.getId() == R.id.configuracion) {
                Intent configuracion = new Intent(getContext(), ActivityConfiguracion.class);
                startActivity(configuracion);
            }
        }
    }

    @Override
    public void onResume() {
        if (auth.getAuth().getCurrentUser()!= null) {
            getUser();
        } else {
            imagenPerfil.setImageResource(R.drawable.ic_person_2);
            nombrePerfil.setText(getString(R.string.iniciar));
        }
        super.onResume();
    }

    private void getUser(){
        userProvider.getUser(auth.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                    Toast.makeText(getContext(), "No existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}