package com.aplicacion.mypet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.adaptadores.ChatsAdapter;
import com.aplicacion.mypet.models.Chat;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.ChatsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FragmentChats extends Fragment {

    private ChatsAdapter chatsAdapter;
    private RecyclerView recyclerView;
    private ChatsProvider chatsProvider;
    private AuthProvider authProvider;
    private View view;

    private TextView noIniciado;
    private TextView noMensajes;
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

        noIniciado = view.findViewById(R.id.no_inicado);
        noMensajes = view.findViewById(R.id.no_mensajes);

        chatsProvider = new ChatsProvider();
        authProvider = new AuthProvider();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (authProvider.getAuth().getCurrentUser()!= null) {
            Query query = chatsProvider.getAll(authProvider.getUid());
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.size()== 0) {
                        noMensajes.setVisibility(View.VISIBLE);
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
            noIniciado.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatsAdapter != null){
            chatsAdapter.stopListening();
        }
    }
}