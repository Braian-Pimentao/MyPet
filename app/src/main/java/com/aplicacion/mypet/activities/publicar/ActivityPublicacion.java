package com.aplicacion.mypet.activities.publicar;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.adaptadores.SliderAdaptador;
import com.aplicacion.mypet.models.SliderItem;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class ActivityPublicacion extends AppCompatActivity {
    private SliderView sliderView;
    private SliderAdaptador sliderAdaptador;
    private List<SliderItem> listaSliderItem;
    private String idPublicacion;

    private PublicacionProvider publicacionProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);
        sliderView = findViewById(R.id.imageSlider);
        listaSliderItem = new ArrayList<>();

        idPublicacion = (String) getIntent().getExtras().get("id");




        publicacionProvider = new PublicacionProvider();
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
                    }
                    instanciarSlider();
                }
            }
        });
    }
}