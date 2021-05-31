package com.aplicacion.mypet.activities.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.adaptadores.MessageAdapter;
import com.aplicacion.mypet.models.Chat;
import com.aplicacion.mypet.models.FCMBody;
import com.aplicacion.mypet.models.Mensaje;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.ChatsProvider;
import com.aplicacion.mypet.providers.MensajeProvider;
import com.aplicacion.mypet.providers.NotificationProvider;
import com.aplicacion.mypet.providers.TokenProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.aplicacion.mypet.utils.AppInfo;
import com.aplicacion.mypet.utils.RelativeTime;
import com.aplicacion.mypet.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityChat extends AppCompatActivity {
    private String extraIdUser1;
    private String extraIdUser2;
    private String extraIdChat;

    private ChatsProvider chatsProvider;
    private MensajeProvider mensajeProvider;
    private AuthProvider authProvider;
    private UserProvider userProvider;
    private NotificationProvider notificationProvider;
    private TokenProvider tokenProvider;

    private CircleImageView imagenUsuario;
    private TextView nombreUsuario;
    private TextView infoChat;


    private EditText editTextMensaje;
    private RecyclerView recyclerViewMensajes;
    private LinearLayoutManager linearLayoutManager;


    private MessageAdapter messageAdapter;

    private String miNombreDeUsuario;
    private String nombreDeUsuarioChat;
    private String imagenSender;


    private View actionBarView;
    private long idNotificationChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatsProvider = new ChatsProvider();
        mensajeProvider = new MensajeProvider();
        authProvider = new AuthProvider();
        userProvider = new UserProvider();
        notificationProvider = new NotificationProvider();
        tokenProvider = new TokenProvider();

        extraIdUser1 = getIntent().getStringExtra("idUser1");
        extraIdUser2 = getIntent().getStringExtra("idUser2");
        extraIdChat = getIntent().getStringExtra("idChat");

        editTextMensaje = findViewById(R.id.edit_text_mensaje);

        recyclerViewMensajes = findViewById(R.id.recyclerViewMensajes);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewMensajes.setLayoutManager(linearLayoutManager);

        showCustomToolbar(R.layout.custom_chat_toolbar);
        getMyInfoUser();

        AppInfo.init(true);
        checkifChatExist();
    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolBar_chat);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBarView = inflater.inflate(resource,null);
        actionBar.setCustomView(actionBarView);

        imagenUsuario = actionBarView.findViewById(R.id.imagen_perfil_chat);
        nombreUsuario = actionBarView.findViewById(R.id.user_chat_toolbar);
        infoChat = actionBarView.findViewById(R.id.info_user_toolbar);

        getUserInfo();

    }

    private void getUserInfo() {
        String idUserInfo = "";
        if (authProvider.getUid().equals(extraIdUser1)){
            idUserInfo = extraIdUser2;
        } else {
            idUserInfo = extraIdUser1;
        }
        userProvider.getUserRealTime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("username")) {
                        nombreDeUsuarioChat = documentSnapshot.getString("username");
                        nombreUsuario.setText(nombreDeUsuarioChat);
                    }

                    if (documentSnapshot.contains("online")) {
                        boolean online = documentSnapshot.getBoolean("online");
                        if (online){
                            infoChat.setText(getString(R.string.online));
                        } else if (documentSnapshot.contains("ultimaConexion")) {
                            infoChat.setText(RelativeTime.timeFormatAMPM(documentSnapshot.getLong("ultimaConexion"),ActivityChat.this));
                        }
                    }

                    if (documentSnapshot.contains("urlPerfil")) {
                        String imageProfile = documentSnapshot.getString("urlPerfil");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.get().load(imageProfile).into(imagenUsuario);
                            }
                        }
                    }
                }
            }
        });
    }

    private void checkifChatExist() {
        chatsProvider.getChatByUser1AndUser2(extraIdUser1,extraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    createChat();
                } else {
                    extraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    idNotificationChat = queryDocumentSnapshots.getDocuments().get(0).getLong("idNotificacion");
                    getMensajesChat();
                    updateViewed();

                }

            }
        });
    }

    private void updateViewed() {
        String idSender = "";

        if (authProvider.getUid().equals(extraIdUser1)) {
            idSender = extraIdUser2;
        } else {
            idSender = extraIdUser1;
        }

        mensajeProvider.getMensajeByChatAndSender(extraIdChat,idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    mensajeProvider.updateVisto(document.getId(),true);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (messageAdapter != null) {
            messageAdapter.startListening();
        }
        ViewedMessageHelper.updateOnline(true, ActivityChat.this);

    }

    private void getMensajesChat() {
        Query query = mensajeProvider.getMensajesByChat(extraIdChat);
        FirestoreRecyclerOptions<Mensaje> options = new FirestoreRecyclerOptions.Builder<Mensaje>()
                .setQuery(query,Mensaje.class)
                .build();

        messageAdapter = new MessageAdapter(options,this);
        recyclerViewMensajes.setAdapter(messageAdapter);
        messageAdapter.startListening();
        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int numeroMensajes = messageAdapter.getItemCount();
                int ultimoMensajePsocion = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (ultimoMensajePsocion == -1 || (positionStart >= (numeroMensajes-1)
                        && ultimoMensajePsocion == (positionStart-1))) {
                    recyclerViewMensajes.scrollToPosition(positionStart);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        messageAdapter.stopListening();
        AppInfo.init(false);
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(extraIdUser1);
        chat.setIdUser2(extraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(extraIdUser1+extraIdUser2);
        ArrayList<String> ids = new ArrayList<>();
        ids.add(extraIdUser1);
        ids.add(extraIdUser2);
        Random random = new Random();
        int numero = random.nextInt(1000000);
        idNotificationChat = numero;
        chat.setIdNotificacion(numero);
        chat.setIds(ids);
        chatsProvider.create(chat);
        extraIdChat = chat.getId();
        getMensajesChat();
    }

    public void cerrarChat(View view) {
        comprobarMensajes();
        finish();
    }

    private void comprobarMensajes() {
        mensajeProvider.getMensajesByChat(extraIdChat).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() == 0) {
                    chatsProvider.deleteChat(extraIdChat);
                }
            }
        });
    }

    public void enviarMensaje(View view) {
        String textoMensaje = editTextMensaje.getText().toString();
        if (!textoMensaje.isEmpty()){
            final Mensaje mensaje = new Mensaje();
            mensaje.setIdChat(extraIdChat);
            if (authProvider.getUid().equals(extraIdUser1)){
                mensaje.setIdSender(extraIdUser1);
                mensaje.setIdReceiver(extraIdUser2);
            } else {
                mensaje.setIdSender(extraIdUser2);
                mensaje.setIdReceiver(extraIdUser1);
            }

            mensaje.setMensaje(textoMensaje);
            mensaje.setTimestamp(new Date().getTime());
            mensaje.setVisto(false);

            mensajeProvider.create(mensaje).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        editTextMensaje.setText("");
                        messageAdapter.notifyDataSetChanged();
                        getToken(mensaje);
                    }
                }
            });

        }
    }

    private void getToken(final Mensaje mensaje) {
        String idUser = "";
        if (authProvider.getUid().equals(extraIdUser1)) {
            idUser = extraIdUser2;
        } else {
            idUser = extraIdUser1;
        }
        if (idUser == null) {
            return;
        }
        tokenProvider.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        getTresUltimosMensajes(mensaje,token);
                    }
                }
                else {
                    Toast.makeText(ActivityChat.this, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getTresUltimosMensajes(final Mensaje mensaje, final String token) {
        mensajeProvider.getTresUltimosMensajesByChatAndSender(extraIdChat,authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Mensaje> mensajeArrayList = new ArrayList<>();

                for (DocumentSnapshot d: queryDocumentSnapshots.getDocuments()) {
                    if (d.exists()){
                        Mensaje mensaje = d.toObject(Mensaje.class);
                        mensajeArrayList.add(mensaje);
                    }
                }

                if (mensajeArrayList.size()==0) {
                    mensajeArrayList.add(mensaje);
                    Collections.reverse(mensajeArrayList);
                }
                Gson gson = new Gson();
                String mensajes = gson.toJson(mensajeArrayList);

                enviarNotificacion(token,mensajes,mensaje);

            }
        });
    }

    private void enviarNotificacion(String token, String mensajes, Mensaje mensaje) {

        Map<String, String> data = new HashMap<>();
        data.put("title", getString(R.string.mensaje_nuevo));
        data.put("body", mensaje.getMensaje());
        data.put("idNotification", String.valueOf(idNotificationChat));
        data.put("mensajes", mensajes);
        data.put("usernameSender", miNombreDeUsuario);
        data.put("imagenSender", imagenSender);
        FCMBody body = new FCMBody(token, "high", "4500s", data);
        notificationProvider.sendNotification(body);
    }

    private void getMyInfoUser() {
        userProvider.getUser(authProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        miNombreDeUsuario = documentSnapshot.getString("username");
                    }

                    if (documentSnapshot.contains("urlPerfil")) {
                        imagenSender = documentSnapshot.getString("urlPerfil");
                    }
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (authProvider.getAuth().getCurrentUser() != null) {
            ViewedMessageHelper.updateOnline(false, this);
        }


    }
}