package com.aplicacion.mypet.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.publicar.ActivityPublicacion;
import com.aplicacion.mypet.models.Publicacion;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class AdaptadorPublicacion extends FirestoreRecyclerAdapter<Publicacion, AdaptadorPublicacion.ViewHolder> {
    private Context context;
    public AdaptadorPublicacion(@NonNull FirestoreRecyclerOptions<Publicacion> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_publicacion,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position,  @NonNull Publicacion publicacion) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);

        final String idPublicacion = document.getId();

        holder.nombre.setText(publicacion.getNombre());
        holder.tipo.setText(publicacion.getTipo());


        holder.raza.setText(publicacion.getRaza());
        holder.edad.setText(publicacion.getEdad());
        Picasso.get().load(publicacion.getImagenes().get(0)).into(holder.foto);

        if (publicacion.getSexo().equalsIgnoreCase(context.getString(R.string.femenino))) {
            holder.sexo.setImageDrawable(context.getDrawable(R.drawable.ic_femenine));
        } else if (publicacion.getSexo().equalsIgnoreCase(context.getString(R.string.masculino))) {
            holder.sexo.setImageDrawable(context.getDrawable(R.drawable.ic_masculino));
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityPublicacion.class);
                intent.putExtra("id",idPublicacion);
                context.startActivity(intent);
            }
        });


    }

    private void traduccionTipoAnimal(ViewHolder holder, Publicacion publicacion) {
        String[] tiposAnimales = context.getResources().getStringArray(R.array.lista_animales);
        for (String animal : tiposAnimales) {
            if (animal.equalsIgnoreCase(publicacion.getTipo())) {
                holder.tipo.setText(animal);
                break;
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView tipo;
        TextView raza;
        TextView edad;
        ImageView foto;
        ImageView sexo;
        View viewHolder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            tipo = itemView.findViewById(R.id.tipo_animal);
            raza = itemView.findViewById(R.id.raza_animal);
            edad = itemView.findViewById(R.id.edad_animal);
            foto = itemView.findViewById(R.id.imagen_animal);
            sexo = itemView.findViewById(R.id.sexo);
            viewHolder = itemView;
        }

    }
}
