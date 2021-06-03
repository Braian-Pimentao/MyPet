package com.aplicacion.mypet.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.chat.ActivityChat;
import com.aplicacion.mypet.models.Chat;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.ChatsProvider;
import com.aplicacion.mypet.providers.MensajeProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    private Context context;
    private UserProvider userProvider;
    private AuthProvider authProvider;
    private ChatsProvider chatsProvider;
    private MensajeProvider mensajeProvider;

    private ListenerRegistration mListener;
     private ListenerRegistration mListenerLastMessage;


    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        userProvider = new UserProvider();
        authProvider = new AuthProvider();
        chatsProvider = new ChatsProvider();
        mensajeProvider = new MensajeProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();

        if (authProvider.getUid().equals(chat.getIdUser1())){
            getUserInfo(chat.getIdUser2(), holder);
        } else {
            getUserInfo(chat.getIdUser1(), holder);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(chatId, chat.getIdUser1(),chat.getIdUser2());
            }
        });

        getUltimoMensaje(chatId, holder.textViewUltimoMensaje);
        String idSender = "";
        if (authProvider.getUid().equals(chat.getIdUser1())){
            idSender = chat.getIdUser2();
        } else {
            idSender = chat.getIdUser1();
        }
        getMensajesNoLeidos(chatId,idSender,holder.mensajesNoLeidos, holder.frameLayoutMensajesNoLeidos);
    }

    private void getMensajesNoLeidos(String chatId, String idSender, TextView mensajesNoLeidos, FrameLayout frameLayout) {
        mListener = mensajeProvider.getMensajeByChatAndSender(chatId,idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    int size = value.size();
                    if (size>0){
                        frameLayout.setVisibility(View.VISIBLE);
                        mensajesNoLeidos.setText(String.valueOf(size));
                    } else {
                        frameLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void getUltimoMensaje(String chatId, TextView textViewUltimoMensaje) {
        mListenerLastMessage = mensajeProvider.getLastMensaje(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    int size = value.size();
                    if (size > 0) {
                        String ultimoMensaje = value.getDocuments().get(0).getString("mensaje");
                        textViewUltimoMensaje.setText(ultimoMensaje);
                    }
                }
            }
        });
    }

    private void goToChatActivity(String chatId,String idUser1, String idUser2) {
        Intent intent = new Intent(context, ActivityChat.class);
        intent.putExtra("idChat",chatId);
        intent.putExtra("idUser1",idUser1);
        intent.putExtra("idUser2",idUser2);
        context.startActivity(intent);
    }

    private void getUserInfo(String idUser, final ViewHolder holder) {
        userProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText(username);
                    }
                    if (documentSnapshot.contains("urlPerfil")) {
                        String imageProfile = documentSnapshot.getString("urlPerfil");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.get().load(imageProfile).into(holder.circleImageFotoUsuario);
                            }
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewUltimoMensaje;
        CircleImageView circleImageFotoUsuario;
        TextView mensajesNoLeidos;
        FrameLayout frameLayoutMensajesNoLeidos;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewUsername = view.findViewById(R.id.user_chat);
            textViewUltimoMensaje = view.findViewById(R.id.ultimo_mensaje);
            circleImageFotoUsuario = view.findViewById(R.id.circleImageChat);
            mensajesNoLeidos = view.findViewById(R.id.contador_mensajes_no_leidos);
            frameLayoutMensajesNoLeidos = view.findViewById(R.id.circulo_mensajes_no_leidos);
            viewHolder = view;
        }
    }

    public ListenerRegistration getListener() {
        return mListener;
    }

    public ListenerRegistration getListenerLastMessage() {
        return mListenerLastMessage;
    }

}
