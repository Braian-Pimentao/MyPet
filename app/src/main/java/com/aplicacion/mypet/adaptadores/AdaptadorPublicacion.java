package com.aplicacion.mypet.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.publicar.ActivityPublicacion;
import com.aplicacion.mypet.activities.sesion.IniciarSesion;
import com.aplicacion.mypet.models.Favorito;
import com.aplicacion.mypet.models.Publicacion;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.FavoritoProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreArray;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.ads.AdSize.BANNER;

public class AdaptadorPublicacion extends FirestoreRecyclerAdapter<Publicacion, AdaptadorPublicacion.ViewHolderPost> {
    private Context context;
    private UserProvider userProvider;
    private FavoritoProvider favoritoProvider;
    private AuthProvider authProvider;

    private TextView contadorPublicaciones;

    public AdaptadorPublicacion(@NonNull FirestoreRecyclerOptions<Publicacion> options, Context context) {
        super(options);
        this.context = context;
        userProvider = new UserProvider();
        favoritoProvider = new FavoritoProvider();
        authProvider = new AuthProvider();
    }


    public AdaptadorPublicacion(@NonNull FirestoreRecyclerOptions<Publicacion> options, Context context, TextView contadorPublicaciones) {
        super(options);
        this.context = context;
        userProvider = new UserProvider();
        favoritoProvider = new FavoritoProvider();
        authProvider = new AuthProvider();
        this.contadorPublicaciones = contadorPublicaciones;
    }

    @NonNull
    @Override
    public ViewHolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_publicacion,parent,false);
        return new ViewHolderPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPost holder, int position, @NonNull final Publicacion publicacion) {
        String[] listaAnimales = context.getResources().getStringArray(R.array.lista_animales);
        DocumentSnapshot document = getSnapshots().getSnapshot(position);

        final String idPublicacion = document.getId();

        if (contadorPublicaciones != null) {
            int number = getSnapshots().size();
            contadorPublicaciones.setText(String.valueOf(number));
        }

        holder.nombre.setText(publicacion.getNombre());
        int tipo = Integer.parseInt(publicacion.getTipo());

        holder.tipo.setText(listaAnimales[tipo]);


        holder.raza.setText(publicacion.getRaza());
        holder.edad.setText(publicacion.getEdad());
        Picasso.get().load(publicacion.getImagenes().get(0)).into(holder.foto);

        if (publicacion.getSexo().equalsIgnoreCase(context.getString(R.string.desc_femenino))) {
            holder.sexo.setImageResource(R.drawable.ic_femenine);
        } else if (publicacion.getSexo().equalsIgnoreCase(context.getString(R.string.desc_masculino))) {
            holder.sexo.setImageResource(R.drawable.ic_masculino);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityPublicacion.class);
                intent.putExtra("id", idPublicacion);
                context.startActivity(intent);
            }
        });

        holder.favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authProvider.getAuth().getCurrentUser() != null) {
                    Favorito favorito = new Favorito();
                    favorito.setIdUser(authProvider.getUid());
                    favorito.setIdPublicacion(idPublicacion);
                    favorito.setTimestamp(new Date().getTime());
                    favorito(favorito, holder);
                } else {
                    Intent items = new Intent(context, IniciarSesion.class);
                    context.startActivity(items);
                }
            }
        });

        if (authProvider.getAuth().getCurrentUser() != null) {
            checkIsExistFavorite(idPublicacion, authProvider.getUid(), holder);
        }

        if (authProvider.getAuth().getCurrentUser() != null) {
            ocultarBotones(publicacion.getIdUser(), holder);
        }

        if (publicacion.getIdUser() != null) {
            getUserInfo(publicacion.getIdUser(), holder);
        }
    }

    private void getUserInfo(String idUser, ViewHolderPost holder) {
        userProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("ubicacion")){
                        ArrayList<Double> ubicacionRecogida = (ArrayList<Double>) documentSnapshot.get("ubicacion");
                        if (ubicacionRecogida!=null){
                            localizacion(ubicacionRecogida.get(0),ubicacionRecogida.get(1), holder);
                        }
                    }
                }
            }
        });
    }

    private void localizacion(double latitude, double longitude, ViewHolderPost holder){
        Geocoder geocoder;
        List<Address> direccion = null;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            direccion = geocoder.getFromLocation(latitude, longitude, 1); // 1 representa la cantidad de resultados a obtener
        } catch (IOException e) {
            e.printStackTrace();
        }

        String city = direccion.get(0).getLocality();
        String postalCode = direccion.get(0).getPostalCode();
        String country = direccion.get(0).getCountryName();

        if (city!=null)
            holder.ubicacion.setText(city);
        else
            holder.ubicacion.setText(country);

    }

    private void favorito(final Favorito favorito, final ViewHolderPost holder) {
        favoritoProvider.getFavoriteByPostAndUser(favorito.getIdPublicacion(),authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if (numeroDocumentos>0){
                    String idFavorito = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.favorito.setImageResource(R.drawable.ic_no_favorito);
                    favoritoProvider.delete(idFavorito);
                } else {
                    holder.favorito.setImageResource(R.drawable.ic_favorito);
                    favoritoProvider.create(favorito);
                }
            }
        });
    }

    private void ocultarBotones(String idUserPublicacion, ViewHolderPost holder) {
        if (authProvider.getAuth().getCurrentUser()!=null) {
            if (idUserPublicacion.equals(authProvider.getUid())) {
                holder.favorito.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void checkIsExistFavorite(String idPublicacion, String idUser, final ViewHolderPost holder) {
        favoritoProvider.getFavoriteByPostAndUser(idPublicacion,idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if (numeroDocumentos>0){
                    holder.favorito.setImageResource(R.drawable.ic_favorito);
                } else {
                    holder.favorito.setImageResource(R.drawable.ic_no_favorito);
                }
            }
        });
    }

    public class ViewHolderPost extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView tipo;
        TextView raza;
        TextView edad;
        TextView ubicacion;
        ImageView foto;
        ImageView favorito;
        ImageView sexo;
        View viewHolder;

        public ViewHolderPost(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombre);
            tipo = itemView.findViewById(R.id.tipo_animal);
            raza = itemView.findViewById(R.id.raza_animal);
            edad = itemView.findViewById(R.id.edad_animal);
            ubicacion = itemView.findViewById(R.id.ubicacion_cardview);
            foto = itemView.findViewById(R.id.imagen_animal);
            sexo = itemView.findViewById(R.id.sexo);
            favorito = itemView.findViewById(R.id.iamgen_favorito);
            viewHolder = itemView;
        }

    }
}
