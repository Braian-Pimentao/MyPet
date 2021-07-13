package com.aplicacion.mypet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.configuracion.ActivityConfiguracion;
import com.aplicacion.mypet.activities.configuracion.ActivityInformacion;
import com.aplicacion.mypet.activities.perfil.ActivityUsuario;
import com.aplicacion.mypet.activities.sesion.IniciarSesion;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentPerfil extends Fragment {
    private View vista;

    private Button botonConfiguracion;
    private Button botonSobreNosotros;
    private TextView nombrePerfil;
    private UserProvider userProvider;

    private LinearLayout linearLayoutConf;

    private LinearLayout layoutPerfil;
    private transient CoordinatorLayout layout;

    private AuthProvider auth;
    private String nombre;
    private CircleImageView imagenPerfil;

    private AdView mAdView;

    public FragmentPerfil() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_perfil, container, false);
        botonConfiguracion = vista.findViewById(R.id.configuracion);
        botonSobreNosotros = vista.findViewById(R.id.sobre_nosotros);




        MobileAds.initialize(vista.getContext(), new OnInitializationCompleteListener() {
            @Override

            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = vista.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        PulsarBoton pulsarBoton = new PulsarBoton();
        botonConfiguracion.setOnClickListener(pulsarBoton);
        botonSobreNosotros.setOnClickListener(pulsarBoton);

        layoutPerfil = vista.findViewById(R.id.banner_perfil);

        linearLayoutConf = vista.findViewById(R.id.linear_conf_perfil);

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
            linearLayoutConf.setVisibility(View.VISIBLE);
        } else {
            linearLayoutConf.setVisibility(View.GONE);
        }
        return vista;
    }

    private class PulsarBoton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Button botonPulsado = (Button) v;
            if (botonPulsado.getId() == R.id.configuracion) {
                Intent configuracion = new Intent(getContext(), ActivityConfiguracion.class);
                startActivity(configuracion);
            } else if (botonPulsado.getId() == R.id.sobre_nosotros) {
                Intent sobreNosotros = new Intent(getContext(), ActivityInformacion.class);
                startActivity(sobreNosotros);
            }
        }
    }

    @Override
    public void onResume() {
        if (auth.getAuth().getCurrentUser()!= null) {
            getUser();
            linearLayoutConf.setVisibility(View.VISIBLE);
        } else {
            linearLayoutConf.setVisibility(View.GONE);
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