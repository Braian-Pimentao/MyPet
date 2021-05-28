package com.aplicacion.mypet.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.models.Mensaje;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.aplicacion.mypet.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class MessageAdapter extends FirestoreRecyclerAdapter<Mensaje, MessageAdapter.ViewHolder> {

    private Context context;
    private UserProvider userProvider;
    private AuthProvider authProvider;


    public MessageAdapter(FirestoreRecyclerOptions<Mensaje> options, Context context) {
        super(options);
        this.context = context;
        userProvider = new UserProvider();
        authProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Mensaje mensaje) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String mensajeId = document.getId();
        String idUser = document.getString("idUser");
        holder.textViewMensaje.setText(mensaje.getMensaje());
        holder.textViewFecha.setText(RelativeTime.timeFormatAMPM(mensaje.getTimestamp()));

        if(mensaje.getIdSender().equals(authProvider.getUid())) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.setMargins(150,0,0,0);
            holder.linearLayoutMensaje.setLayoutParams(params);
            holder.linearLayoutMensaje.setPadding(30,20,25,20);
            holder.linearLayoutMensaje.setBackground(context.getDrawable(R.drawable.estilo_mensaje_enviado));
            holder.imagenVisto.setVisibility(View.VISIBLE);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.setMargins(0,0,150,0);
            holder.linearLayoutMensaje.setLayoutParams(params);
            holder.linearLayoutMensaje.setPadding(30,20,30,20);
            holder.linearLayoutMensaje.setBackground(context.getDrawable(R.drawable.estilo_mensaje_recibido));
            holder.imagenVisto.setVisibility(View.GONE);
        }
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mensaje, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMensaje;
        TextView textViewFecha;
        ImageView imagenVisto;
        LinearLayout linearLayoutMensaje;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            textViewMensaje = view.findViewById(R.id.texto_mensaje);
            textViewFecha = view.findViewById(R.id.fecha_mensaje);
            imagenVisto = view.findViewById(R.id.icono_visto_mensaje);
            linearLayoutMensaje = view.findViewById(R.id.linearLayout_mensaje);
            viewHolder = view;
        }
    }

}
