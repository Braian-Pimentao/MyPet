package com.aplicacion.mypet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.ActivityUsuario;
import com.aplicacion.mypet.activities.configuracion.ActivityConfiguracion;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class FragmentPerfil extends Fragment {
    private View vista;

    private Button botonConfiguracion;
    private TextView nombrePerfil;
    private UserProvider userProvider;

    private LinearLayout layoutPerfil;

    private AuthProvider auth;

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

        layoutPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent perfil = new Intent(getContext(), ActivityUsuario.class);
                startActivity(perfil);
            }
        });

        nombrePerfil = vista.findViewById(R.id.nombre_perfil);



        auth = new AuthProvider();
        userProvider = new UserProvider();

        comprobarUsuario();
        return vista;
    }

    private void comprobarUsuario() {
        if (auth.getAuth().getCurrentUser()!=null) {
            userProvider.getUser(auth.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    //if (documentSnapshot)
                }
            });
        } else {

        }
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
}