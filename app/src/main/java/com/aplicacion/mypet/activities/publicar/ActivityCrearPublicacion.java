package com.aplicacion.mypet.activities.publicar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.fragments.BottomSheetFragmentPersonalizado;
import com.aplicacion.mypet.models.Publicacion;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.ImageProvider;
import com.aplicacion.mypet.providers.PublicacionProvider;
import com.aplicacion.mypet.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class ActivityCrearPublicacion extends AppCompatActivity implements BottomSheetFragmentPersonalizado.BottomSheetListener {
    private final int GALLERY_REQUEST_CODE=1;
    private final int CAMARA_REQUEST_CODE=2;
    private final int REQUEST_PERMISSION_CAMERA=100;
    private final int REQUEST_PERMISSION_GALLERY=101;

    private AuthProvider authProvider;
    private int contador=0;


    private PublicacionProvider publicacionProvider;
    private File[] imagenes;
    private ArrayList<File> imagenesAlmacenadas;
    private ImageProvider imageProvider;
    private ImageView imagenSeleccionada;
    private File fotoCamaraArchivo;

    private Button boton_tipo;
    private TextInputEditText inputTextNombre;
    private TextInputEditText inputTextEdad;
    private TextInputEditText inputTextRaza;
    private TextInputEditText inputTextDescripcion;
    private int posicionArraytipo;

    private String nombre;
    private String edad;
    private String tipo;
    private String raza;
    private String descripcion;
    private String sexo;
    private ArrayList<String> urlImagenes;

    private AlertDialog dialog;

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
        publicacionProvider = new PublicacionProvider();

        authProvider = new AuthProvider();

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.subiendo_publicacion)
                .setCancelable(false).build();
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
                        posicionArraytipo = which;
                        //boton_tipo.setTextColor(getResources().getColor(R.color.black));
                        dialog.cancel();
                    }
                });

        dialogo = builder.create();
        dialogo.show();
    }

    public void elegirImagen(View view) {
        imagenSeleccionada = (ImageView) view;
        BottomSheetFragmentPersonalizado bottomSheet = new BottomSheetFragmentPersonalizado();
        bottomSheet.show(getSupportFragmentManager(),"TAG");
    }




    public void subirPublicacion(View view) {
        nombre = inputTextNombre.getText().toString();
        edad = inputTextEdad.getText().toString();
        tipo = boton_tipo.getText().toString();
        raza = inputTextRaza.getText().toString();
        descripcion = inputTextDescripcion.getText().toString();


        if (!nombre.isEmpty() && !edad.isEmpty() && !tipo.equals(getString(R.string.tipo_animal))
            && !raza.isEmpty() && !sexo.isEmpty() && !descripcion.isEmpty() && imagenesAlmacenadas.size()>0) {
            dialog.show();
            saveImage();
        } else {
            Toast.makeText(ActivityCrearPublicacion.this, getString(R.string.campos_vacios), Toast.LENGTH_LONG).show();
        }

    }



    public void seleccionarSexo(View view) {
        ImageView imagenMasculino = findViewById(R.id.masculino);
        ImageView imagenFemenino = findViewById(R.id.femenino);
        LinearLayout linearSexo = (LinearLayout) view;
        if (linearSexo.getId() == R.id.sexo_masculino) {
            imagenFemenino.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
            imagenMasculino.setBackgroundColor(getResources().getColor(R.color.principal_app,getTheme()));
        } else if (linearSexo.getId() == R.id.sexo_femenino) {
            imagenMasculino.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
            imagenFemenino.setBackgroundColor(getResources().getColor(R.color.principal_app,getTheme()));
        }
        sexo = (String) linearSexo.getContentDescription();
    }

    private void saveImage() {
        if (contador < imagenesAlmacenadas.size()) {
            contador++;
            imageProvider.save(this,imagenesAlmacenadas.get(contador-1),contador-1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageProvider.getStorage().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urlImagenes.add(uri.toString());
                            if (contador == imagenesAlmacenadas.size()) {

                                Publicacion publicacion = new Publicacion();
                                publicacion.setNombre(nombre.trim());
                                publicacion.setEdad(edad.trim());
                                publicacion.setTipo(String.valueOf(posicionArraytipo));
                                publicacion.setRaza(raza.trim());
                                publicacion.setDescripcion(descripcion.trim());
                                publicacion.setSexo(sexo.trim());
                                publicacion.setFechaPublicacion(new Date().getTime());
                                publicacion.setIdUser(authProvider.getUid());
                                publicacion.setImagenes(urlImagenes);

                                publicacionProvider.save(publicacion).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                        if (taskSave.isSuccessful()) {
                                            dialog.dismiss();
                                            Toast.makeText(ActivityCrearPublicacion.this, getString(R.string.animal_publicado), Toast.LENGTH_LONG).show();
                                            finish();
                                        }else {
                                            Toast.makeText(ActivityCrearPublicacion.this, getString(R.string.error_animal_publicado), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            saveImage();
                        }
                    });
                }
            });
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getPackageManager())!=null){

            fotoCamaraArchivo = null;
            try {
                fotoCamaraArchivo = FileUtil.fileCamera(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fotoCamaraArchivo != null) {
                Uri fotoUri = FileProvider.getUriForFile(
                        this,
                        "com.aplicacion.mypet",
                        fotoCamaraArchivo);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(cameraIntent,CAMARA_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                File imagen = FileUtil.from(this,data.getData());
                asignarImagenesArray(imagen);
                imagenSeleccionada.setImageBitmap(BitmapFactory.decodeFile(imagen.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR: ", e.getMessage());
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMARA_REQUEST_CODE && resultCode == RESULT_OK){
            asignarImagenesArray(fotoCamaraArchivo);

            imagenSeleccionada.setImageBitmap(BitmapFactory.decodeFile(fotoCamaraArchivo.getAbsolutePath()));
        }
    }

    private void asignarImagenesArray(File file) {
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
        actualizarArrayListImagenes();
    }

    private void eliminarImagenesArray() {
        if (imagenSeleccionada.getId() == R.id.imagen_publicar_1){
            imagenes[0] = null;
        } else if (imagenSeleccionada.getId() == R.id.imagen_publicar_2){
            imagenes[1] = null;
        } else if (imagenSeleccionada.getId() == R.id.imagen_publicar_3){
            imagenes[2] = null;
        } else if (imagenSeleccionada.getId() == R.id.imagen_publicar_4){
            imagenes[3] = null;
        } else if (imagenSeleccionada.getId() == R.id.imagen_publicar_5){
            imagenes[4] = null;
        }
        actualizarArrayListImagenes();
    }

    private void actualizarArrayListImagenes() {
        imagenesAlmacenadas = new ArrayList<>();
        for (File imagen : imagenes) {
            if (imagen!= null)
                imagenesAlmacenadas.add(imagen);
        }
    }

    @Override
    public void onButtonClicked(int numero) {
        if (numero == 0){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                openCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},REQUEST_PERMISSION_CAMERA);
            }
        } else if (numero == 1) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISSION_GALLERY);
            }
        } else if (numero == 2) {
            imagenSeleccionada.setImageResource(R.drawable.ic_camara);
            eliminarImagenesArray();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CAMERA) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            } else {
                Toast.makeText(ActivityCrearPublicacion.this, getString(R.string.permisos), Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == REQUEST_PERMISSION_GALLERY) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            } else {
                Toast.makeText(ActivityCrearPublicacion.this, getString(R.string.permisos), Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}