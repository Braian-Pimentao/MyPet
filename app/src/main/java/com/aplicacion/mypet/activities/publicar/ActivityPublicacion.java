package com.aplicacion.mypet.activities.publicar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.chat.ActivityChat;
import com.aplicacion.mypet.activities.perfil.ActivityUsuario;
import com.aplicacion.mypet.activities.sesion.IniciarSesion;
import com.aplicacion.mypet.adaptadores.SliderAdaptador;
import com.aplicacion.mypet.models.Favorito;
import com.aplicacion.mypet.models.SliderItem;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.FavoritoProvider;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.aplicacion.mypet.utils.RelativeTime;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityPublicacion extends AppCompatActivity {
    private SliderView sliderView;
    private SliderAdaptador sliderAdaptador;
    private List<SliderItem> listaSliderItem;
    private String idPublicacion;
    private ArrayList<Double> ubicacionRecogida;
    private String idUser;
    private boolean ocultarUbicacion;



    private PublicacionProvider publicacionProvider;
    private UserProvider userProvider;
    private FavoritoProvider favoritoProvider;
    private AuthProvider authProvider;


    private TextView nombreAnimal;
    private TextView nombreUsuario;
    private TextView tipoAnimal;
    private TextView edadAnimal;
    private TextView razaAnimal;
    private TextView descripcionAnimal;
    private TextView ubicacion;
    private ImageView imagenSexo;

    private ImageView imagenTipoAnimal;
    private CircleImageView fotoUsuario;
    private TextView tiempo;
    private TextView favoritos;
    private FloatingActionButton botonFavorito;
    private Toolbar toolbar;
    private MenuItem reportarAnuncio;
    private MenuItem borrarAnuncio;
    private Button botonChat;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_publicacion);
        toolbar = (Toolbar) findViewById(R.id.toolBar_anuncio);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        sliderView = findViewById(R.id.imageSlider);
        listaSliderItem = new ArrayList<>();



        idPublicacion = getIntent().getExtras().getString("id");

        nombreAnimal = findViewById(R.id.nombre_animal_anuncio);
        edadAnimal = findViewById(R.id.edad_animal_anuncio);
        nombreUsuario = findViewById(R.id.nombre_usuario_anuncio);
        tipoAnimal = findViewById(R.id.tipo_animal_anuncio);
        razaAnimal = findViewById(R.id.raza_animal_anuncio);
        descripcionAnimal = findViewById(R.id.descripcion_animal_anuncio);
        ubicacion = findViewById(R.id.nombre_ubicacion_anuncio);
        ubicacion = findViewById(R.id.nombre_ubicacion_anuncio);
        imagenSexo = findViewById(R.id.sexo_anuncio);
        imagenTipoAnimal = findViewById(R.id.icono_tipo_animal);
        fotoUsuario = findViewById(R.id.foto_perfil_anuncio);
        tiempo = findViewById(R.id.tiempo_publicacion);
        favoritos = findViewById(R.id.contador_favoritos);
        botonFavorito = findViewById(R.id.fabFavorite);
        botonChat = findViewById(R.id.boton_abrir_chat_anuncio);


        idUser="";
        publicacionProvider = new PublicacionProvider();
        userProvider = new UserProvider();
        favoritoProvider = new FavoritoProvider();
        authProvider = new AuthProvider();

        if (authProvider.getAuth().getCurrentUser()!=null){
            checkIsExistFavorite(idPublicacion,authProvider.getUid());
        }
    }

    private void getNumberFavorites() {
        favoritoProvider.getFavoriteByPost(idPublicacion).addSnapshotListener(
        new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int numeroFavoritos = value.size();
                favoritos.setText(String.valueOf(numeroFavoritos));
            }
        });
    }

    private void instanciarSlider() {
        sliderAdaptador = new SliderAdaptador(ActivityPublicacion.this,listaSliderItem);
        sliderView.setSliderAdapter(sliderAdaptador);
        sliderView.setIndicatorSelectedColor(R.color.principal_app);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);

    }

    private void getPost(){
        publicacionProvider.getPostById(idPublicacion).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("imagenes")) {
                        ArrayList<String> urlImagenes = (ArrayList<String>) documentSnapshot.get("imagenes");
                        for (int i = 0; i <urlImagenes.size() ; i++) {
                            SliderItem item = new SliderItem();
                            item.setUrlImagen(urlImagenes.get(i));
                            listaSliderItem.add(item);
                        }
                        instanciarSlider();
                    }
                    if(documentSnapshot.contains("nombre")){
                        nombreAnimal.setText(documentSnapshot.getString("nombre"));
                    }

                    if(documentSnapshot.contains("edad")){
                        edadAnimal.setText(documentSnapshot.getString("edad"));
                    }

                    if(documentSnapshot.contains("raza")){
                        nombreAnimal.setText(documentSnapshot.getString("raza"));
                    }

                    if(documentSnapshot.contains("tipo")){
                        nombreAnimal.setText(documentSnapshot.getString("nombre"));
                    }

                    if(documentSnapshot.contains("sexo")){
                        String sexo = documentSnapshot.getString("sexo");
                        if (sexo.equalsIgnoreCase(getString(R.string.femenino))) {
                            imagenSexo.setImageDrawable(getDrawable(R.drawable.ic_femenine));
                        } else if (sexo.equalsIgnoreCase(getString(R.string.masculino))) {
                            imagenSexo.setImageDrawable(getDrawable(R.drawable.ic_masculino));
                        }
                    }

                    if (documentSnapshot.contains("tipo")){
                        int tipo = Integer.parseInt(documentSnapshot.getString("tipo"));
                        tipoAnimal.setText(getResources().getStringArray(R.array.lista_animales)[tipo]);
                        cambiarImagenTipo(tipo);
                    }

                    if (documentSnapshot.contains("raza")){
                        razaAnimal.setText(documentSnapshot.getString("raza"));
                    }

                    if (documentSnapshot.contains("fechaPublicacion")){
                        long timestamp = documentSnapshot.getLong("fechaPublicacion");
                        tiempo.setText(RelativeTime.getTimeAgo(timestamp,ActivityPublicacion.this));
                    }

                    if (documentSnapshot.contains("descripcion")){
                        descripcionAnimal.setText(documentSnapshot.getString("descripcion"));
                    }
                    if (documentSnapshot.contains("idUser")){
                        idUser=documentSnapshot.getString("idUser");
                        getUser(idUser);
                        modificarMenu();
                        ocultarBotones();
                    }

                }
            }
        });
    }

    private void ocultarBotones() {
        if (authProvider.getAuth().getCurrentUser()!=null) {
            if (idUser.equals(authProvider.getUid())) {
                botonFavorito.setVisibility(View.INVISIBLE);
                botonChat.setText(getString(R.string.eliminar));
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        getPost();
        getNumberFavorites();
    }

    private void modificarMenu() {
        if (authProvider.getAuth().getCurrentUser()!=null){
            if (idUser.equals(authProvider.getUid())){
                reportarAnuncio.setVisible(false);
            } else {
                borrarAnuncio.setVisible(false);
            }
        } else {
            borrarAnuncio.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_eliminar,menu);

        borrarAnuncio = menu.findItem(R.id.opcion_borrar);
        reportarAnuncio = menu.findItem(R.id.opcion_reportar);

        return super.onCreateOptionsMenu(menu) ;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.equals(borrarAnuncio)) {
            mostrarConfirmacionBorrar(idPublicacion);
        } else if (item.equals(reportarAnuncio)){
            Toast.makeText(this,"Adioos",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarConfirmacionBorrar(String idPublicacion) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.eliminar))
                .setMessage(getString(R.string.comprobar_eliminar))
                .setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePublicacion(idPublicacion);
                    }
                })
                .setNegativeButton(getString(R.string.no),null).show();
    }

    private void deletePublicacion(String idPublicacion) {
        publicacionProvider.delete(idPublicacion).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ActivityPublicacion.this,getString(R.string.publicacion_eliminada),Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void cambiarImagenTipo(int tipo) {
        final String[] tipos = getResources().getStringArray(R.array.lista_animales);

        switch (tipo) {
            case 0:
                imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_perro));
                break;
            case 1:
                imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_gato));
                break;
            case 2:
                imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_conejo));
                break;
            case 3:
                imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_ardilla));
                break;
            case 4:
                imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_tortuga));
                break;
            case 5:
                imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_pez));
                break;
            case 6:
                imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_pajaro));
                break;
            case 7:
                imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_serpiente));
                break;
        }
    }

    private void localizacion(double latitude, double longitude){
        Geocoder geocoder;
        List<Address> direccion = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            direccion = geocoder.getFromLocation(latitude, longitude, 1); // 1 representa la cantidad de resultados a obtener
        } catch (IOException e) {
            e.printStackTrace();
        }

        String city = direccion.get(0).getLocality();
        String postalCode = direccion.get(0).getPostalCode();
        String country = direccion.get(0).getCountryName();
        String state = direccion.get(0).getLocality();

        if (postalCode!=null) {
            if (city!= null){
                ubicacion.setText(String.format("%s, %s", city, postalCode));
            } else {
                if (state!=null){
                    ubicacion.setText(String.format("%s, %s", state,country));
                } else {
                    ubicacion.setText(String.format("%s", country));
                }
            }
        } else {
            if (state!=null){
                ubicacion.setText(String.format("%s, %s", state,country));
            } else {
                ubicacion.setText(String.format("%s", country));
            }
        }
    }

    private void getUser(String idUser){
        userProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                    if (documentSnapshot.contains("username")) {
                        nombreUsuario.setText(documentSnapshot.getString("username"));
                    }
                    if (documentSnapshot.contains("ubicacion")){
                        ubicacionRecogida = (ArrayList<Double>) documentSnapshot.get("ubicacion");
                        if (ubicacionRecogida!=null){
                            localizacion(ubicacionRecogida.get(0),ubicacionRecogida.get(1));
                            new mapa();
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map_publicacion);
                            mapFragment.getMapAsync(new mapa());
                        } else {
                            ubicacion.setText(getString(R.string.sin_ubicaion));
                        }

                        if (documentSnapshot.contains("ocultarUbicacion")) {
                            if (documentSnapshot.getBoolean("ocultarUbicacion")!=null){
                                ocultarUbicacion = documentSnapshot.getBoolean("ocultarUbicacion");
                            }
                        }
                    }

                    if (documentSnapshot.contains("urlPerfil")){
                        String urlFotoPerfil = documentSnapshot.getString("urlPerfil");
                        if (urlFotoPerfil!= null){
                            Picasso.get().load(urlFotoPerfil).into(fotoUsuario);
                        }
                    }

                }
            }
        });
    }

    public void cerrarPublicacion(View view) {
        finish();
    }

    public void verPerfil(View view) {
        if (!idUser.isEmpty()) {
            Intent perfil = new Intent(this, ActivityUsuario.class);
            perfil.putExtra("idUser", idUser);
            startActivity(perfil);
        }
    }

    public void darFavorito(View view) {
        if (authProvider.getAuth().getCurrentUser()!=null){
            Favorito favorito = new Favorito();
            favorito.setIdUser(authProvider.getUid());
            favorito.setIdPublicacion(idPublicacion);
            favorito.setTimestamp(new Date().getTime());
            favorito(favorito);
        } else {
            Intent items = new Intent(this, IniciarSesion.class);
            startActivity(items);
        }
    }

    private void checkIsExistFavorite(String idPublicacion, String idUser) {
        favoritoProvider.getFavoriteByPostAndUser(idPublicacion,idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if (numeroDocumentos>0){
                    botonFavorito.setImageResource(R.drawable.ic_favorito);
                } else {
                    botonFavorito.setImageResource(R.drawable.ic_no_favorito);
                }
            }
        });
    }

    private void favorito(final Favorito favorito) {
        favoritoProvider.getFavoriteByPostAndUser(favorito.getIdPublicacion(),authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if (numeroDocumentos>0){
                    String idFavorito = queryDocumentSnapshots.getDocuments().get(0).getId();
                    botonFavorito.setImageResource(R.drawable.ic_no_favorito);
                    favoritoProvider.delete(idFavorito);
                } else {
                    botonFavorito.setImageResource(R.drawable.ic_favorito);
                    favoritoProvider.create(favorito);
                }
            }
        });
    }

    public void botonChat(View view) {
        Button boton = (Button) view;

        if (boton.getText().equals(getString(R.string.eliminar))) {
            mostrarConfirmacionBorrar(idPublicacion);
        } else if (boton.getText().equals(getString(R.string.chat))) {
            if (authProvider.getAuth().getCurrentUser() != null) {
               Intent intent = new Intent(this, ActivityChat.class);
               intent.putExtra("idUser1",authProvider.getUid());
               intent.putExtra("idUser2",idUser);
               startActivity(intent);
            } else {
                Intent iniciarSesion = new Intent(this, IniciarSesion.class);
                startActivity(iniciarSesion);
            }
        }
    }

    private class mapa implements OnMapReadyCallback{
        private GoogleMap mMap;

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;
            LatLng latLng = new LatLng(ubicacionRecogida.get(0),ubicacionRecogida.get(1));

            if (!ocultarUbicacion) {
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            } else {
                Circle circuloMarca = mMap.addCircle(new CircleOptions().center(latLng).radius(600));
                circuloMarca.setFillColor(getColor(R.color.negro_opaco));
                circuloMarca.setStrokeColor(getColor(R.color.opaco));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        }
    }
}