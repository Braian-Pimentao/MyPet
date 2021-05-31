package com.aplicacion.mypet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.FavoritoProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;


public class FragmentFavorito extends Fragment {
    private FavoritoProvider favoritoProvider;
    private AuthProvider authProvider;

    public FragmentFavorito() {
        // Required empty public constructor
        favoritoProvider = new FavoritoProvider();
        authProvider = new AuthProvider();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorito, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        favoritoProvider.getFavoritesByUser(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                System.out.println("-----------------------------------------------------"+ queryDocumentSnapshots.size());
            }
        });

    }
}