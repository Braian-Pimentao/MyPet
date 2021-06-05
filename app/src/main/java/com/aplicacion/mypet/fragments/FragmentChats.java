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
import com.aplicacion.mypet.adaptadores.ChatsAdapter;
import com.aplicacion.mypet.models.Chat;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.ChatsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentChats extends Fragment {

    private ChatsAdapter chatsAdapter;
    private RecyclerView recyclerView;
    private ChatsProvider chatsProvider;
    private AuthProvider authProvider;
    private View view;

    private TextView textLinearLayout;
    private Button botonIniciar;
    private LinearLayout linearLayoutNoMensajes;

    private ListenerRegistration mListener;

    public FragmentChats() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewChats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        textLinearLayout = view.findViewById(R.id.text_no_mensajes);
        botonIniciar = view.findViewById(R.id.boton_iniciar_sesion);
        botonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iniciarSesion = new Intent(getContext(), IniciarSesion.class);
                startActivity(iniciarSesion);
            }
        });

        linearLayoutNoMensajes = view.findViewById(R.id.mensaje_informativo_chats);

        chatsProvider = new ChatsProvider();
        authProvider = new AuthProvider();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
         if (authProvider.getAuth().getCurrentUser()!= null) {
            Query query = chatsProvider.getAll(authProvider.getUid());
             mListener = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.size()== 0) {
                        botonIniciar.setVisibility(View.GONE);
                        textLinearLayout.setText(getString(R.string.sin_mensajes));
                        linearLayoutNoMensajes.setVisibility(View.VISIBLE);
                    } else {
                        linearLayoutNoMensajes.setVisibility(View.GONE);
                    }
                }
            });



            FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                    .setQuery(query,Chat.class)
                    .build();

            chatsAdapter = new ChatsAdapter(options,getContext());

            recyclerView.setAdapter(chatsAdapter);
            chatsAdapter.startListening();
        } else {
            textLinearLayout.setText(getString(R.string.no_iniciado_sesion));
            botonIniciar.setVisibility(View.VISIBLE);
            linearLayoutNoMensajes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatsAdapter != null){
            chatsAdapter.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatsAdapter!= null) {
            if (chatsAdapter.getListener() != null) {
                chatsAdapter.getListener().remove();
            }
            if (chatsAdapter.getListenerLastMessage() != null) {
                chatsAdapter.getListenerLastMessage().remove();
            }
        }

        if (mListener != null) {
            mListener.remove();
        }
    }

    private class PulsarBoton implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent iniciarSesion = new Intent(getContext(), IniciarSesion.class);
            startActivity(iniciarSesion);
        }
    }
}