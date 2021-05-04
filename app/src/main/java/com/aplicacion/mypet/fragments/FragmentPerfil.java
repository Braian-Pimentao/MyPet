package com.aplicacion.mypet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.sesion.IniciarSesion;

public class FragmentPerfil extends Fragment {
    private View vista;

    public FragmentPerfil() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_perfil, container, false);
        return vista;
    }

    public void pulsarBotonPrueba(View v) {
        Intent items = new Intent(getActivity(), IniciarSesion.class);
        startActivity(items);
    }
}