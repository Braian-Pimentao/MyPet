package com.aplicacion.mypet.activities.publicar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.adaptadores.AdaptadorPublicacion;
import com.aplicacion.mypet.models.Publicacion;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class FiltrosActivity extends AppCompatActivity {
    private String extraTipoAnimal;
    private TextView tipoAnimalTextView;
    private TextView contadorAnimalesFiltros;

    private RecyclerView recyclerView;
    private PublicacionProvider publicacionProvider;
    private AdaptadorPublicacion adaptadorPublicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        extraTipoAnimal = getIntent().getStringExtra("tipo");

        recyclerView = findViewById(R.id.listar_anuncios_filtro);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        publicacionProvider = new PublicacionProvider();

        tipoAnimalTextView = findViewById(R.id.tipo_animal_filtro);
        contadorAnimalesFiltros = findViewById(R.id.contador_animal_filtro);
        seleccionartipo();
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = publicacionProvider.getPublicacionByCategoryAndTimesTamp(extraTipoAnimal);
        FirestoreRecyclerOptions<Publicacion> options = new FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query,Publicacion.class)
                .build();

        adaptadorPublicacion = new AdaptadorPublicacion(options,this,contadorAnimalesFiltros);
        recyclerView.setAdapter(adaptadorPublicacion);
        adaptadorPublicacion.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adaptadorPublicacion.stopListening();
    }

    private void seleccionartipo() {
        String[] tipos = getResources().getStringArray(R.array.lista_animales);
        tipoAnimalTextView.setText(String.format("%s:",tipos[Integer.parseInt(extraTipoAnimal)]));
    }

    public void cerrarFiltro(View view) {
        finish();
    }
}