package com.aplicacion.mypet.activities.publicar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.models.Publicacion;
import com.aplicacion.mypet.providers.ImageProvider;
import com.aplicacion.mypet.providers.PublicarProvider;
import com.aplicacion.mypet.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class ActivityCrearPublicacion extends AppCompatActivity {
    private final int GALLERY_REQUEST_CODE=1;
    private PublicarProvider publicarProvider;

    private File[] imagenes;
    private ImageView[] imagenesAnimal;
    private ImageProvider imageProvider;
    private ImageView imagenSeleccionada;

    private Button boton_tipo;
    private TextInputEditText inputTextNombre;
    private TextInputEditText inputTextEdad;
    private TextInputEditText inputTextRaza;
    private TextInputEditText inputTextDescripcion;

    private String nombre;
    private String edad;
    private String tipo;
    private String raza;
    private String descripcion;
    private String sexo;
    private ArrayList<String> urlImagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_publicacion);

        imageProvider = new ImageProvider();
        sexo="";
        imagenes = new File[5];

        boton_tipo = findViewById(R.id.boton_cambiar_tipo);
        inputTextNombre = findViewById(R.id.campo_nombre_animal);
        inputTextEdad = findViewById(R.id.campo_edad);
        inputTextRaza = findViewById(R.id.campo_raza);
        inputTextDescripcion = findViewById(R.id.campo_descripcion);


        urlImagenes = new ArrayList<>();
        publicarProvider = new PublicarProvider();
    }

    public void cancelarPublicacion(View v) {
        finish();
    }

    public void cambiarTipoAnimal(View view) {
        AlertDialog dialogo;
        String[] array = getResources().getStringArray(R.array.lista_animales);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.tipo_animal))
                .setSingleChoiceItems(array,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boton_tipo.setText(array[which]);
                        boton_tipo.setTextColor(getResources().getColor(R.color.black));
                        dialog.cancel();
                    }
                });

        dialogo = builder.create();
        dialogo.show();
    }

    public void elegirImagen(View view) {
        imagenSeleccionada = (ImageView) view;
        openGallery();
    }

    public void subirPublicacion(View view) {
        nombre = inputTextNombre.getText().toString();
        edad = inputTextEdad.getText().toString();
        tipo = boton_tipo.getText().toString();
        raza = inputTextRaza.getText().toString();
        descripcion = inputTextDescripcion.getText().toString();


        if (!nombre.isEmpty() && !edad.isEmpty() && !tipo.equals(getString(R.string.tipo_animal))
            && !raza.isEmpty() && !descripcion.isEmpty() && !comprobarImagenes()) {
            saveImage();

        } else {
            Toast.makeText(ActivityCrearPublicacion.this, getString(R.string.campos_vacios), Toast.LENGTH_LONG).show();
        }
    }

    private boolean comprobarImagenes() {
        for (int i = 0; i < imagenes.length ; i++) {
            if (imagenes[i]!= null){
                return false;
            }
        }
        return true;
    }

    public void seleccionarSexo(View view) {
        CardView cardViewMasculono = findViewById(R.id.card_masculino);
        CardView cardViewFemenino = findViewById(R.id.card_femenino);
        LinearLayout linearSexo = (LinearLayout) view;
        if (linearSexo.getId() == R.id.sexo_masculino) {
            cardViewFemenino.setCardBackgroundColor(getResources().getColor(R.color.white));
            cardViewMasculono.setCardBackgroundColor(getResources().getColor(R.color.negro_opaco));
        } else if (linearSexo.getId() == R.id.sexo_femenino) {
            cardViewMasculono.setCardBackgroundColor(getResources().getColor(R.color.white));
            cardViewFemenino.setCardBackgroundColor(getResources().getColor(R.color.negro_opaco));
        }
        sexo = (String) linearSexo.getContentDescription();
        System.out.println(sexo);
    }

    private void saveImage() {
        for (int i = 0; i < imagenes.length; i++) {
            if (imagenes[i] != null) {
                imageProvider.save(this,imagenes[i]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlImagenes.add(uri.toString());
                                }
                            });
                        } else {
                            Toast.makeText(ActivityCrearPublicacion.this, getString(R.string.error_subir_imagen), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Publicacion publicacion = new Publicacion();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                File imagen = FileUtil.from(this,data.getData());
                asignarImaginasArray(imagen);
                imagenSeleccionada.setImageBitmap(BitmapFactory.decodeFile(imagen.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR: ", e.getMessage());
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void asignarImaginasArray(File file) {
        if (imagenSeleccionada.getId() == R.id.imagen_publicar_1){
            imagenes[0] = file;
        } else if (imagenSeleccionada.getId() == R.id.imagen_publicar_2){
            imagenes[1] = file;
        } else if (imagenSeleccionada.getId() == R.id.imagen_publicar_3){
            imagenes[2] = file;
        } else if (imagenSeleccionada.getId() == R.id.imagen_publicar_4){
            imagenes[3] = file;
        } else if (imagenSeleccionada.getId() == R.id.imagen_publicar_5){
            imagenes[4] = file;
        }
    }
}