package com.aplicacion.mypet.activities.publicar;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.perfil.ActivityUsuario;
import com.aplicacion.mypet.adaptadores.SliderAdaptador;
import com.aplicacion.mypet.models.SliderItem;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);
        sliderView = findViewById(R.id.imageSlider);
        listaSliderItem = new ArrayList<>();



        idPublicacion = (String) getIntent().getExtras().get("id");

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


        idUser="";
        publicacionProvider = new PublicacionProvider();
        userProvider = new UserProvider();
        getPost();
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
                    if (documentSnapshot.contains("descripcion")){
                        descripcionAnimal.setText(documentSnapshot.getString("descripcion"));
                    }
                    if (documentSnapshot.contains("idUser")){
                        idUser=documentSnapshot.getString("idUser");
                        getUser(idUser);
                    }

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
                            ocultarUbicacion = documentSnapshot.getBoolean("ocultarUbicacion");
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