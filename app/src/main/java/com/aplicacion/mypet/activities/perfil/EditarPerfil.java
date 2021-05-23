package com.aplicacion.mypet.activities.perfil;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.fragments.BottomSheetFragmentPersonalizado;
import com.aplicacion.mypet.models.User;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.ImageProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.aplicacion.mypet.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditarPerfil extends AppCompatActivity  implements BottomSheetFragmentPersonalizado.BottomSheetListener {
    private final int GALLERY_REQUEST_CODE=1;
    private final int CAMARA_REQUEST_CODE=2;
    private final int REQUEST_PERMISSION_CAMERA=100;
    private final int REQUEST_PERMISSION_GALLERY=101;
    private AlertDialog dialog;
    private File imagen;
    private String nombre="";
    private boolean ocultarUbicacion;

    private ImageProvider imageProvider;
    private UserProvider userProvider;
    private String urlFotoPerfil ="";

    private Double latitude;
    private Double longitude;
    private final int REQUEST_CODE_UBICACION = 5;
    private Button botonUbicacion;

    private CircleImageView fotoPerfil;
    private TextInputEditText nombreUsuario;
    private ImageButton botonSubirFoto;

    private AuthProvider auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        latitude = null;
        longitude = null;
        ocultarUbicacion = false;

        botonUbicacion = findViewById(R.id.boton_marcar_ubicacion);
        nombreUsuario = findViewById(R.id.campo_nombre_editar);
        fotoPerfil = findViewById(R.id.foto_perfil);
        botonSubirFoto = findViewById(R.id.boton_subir_foto);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.actualizando_perfil)
                .setCancelable(false).build();

        imageProvider = new ImageProvider();
        userProvider = new UserProvider();
        auth = new AuthProvider();

        getUser();
    }

    public void cerrarEditarPerfil(View view) {
        finish();
    }

    public void cambiarUbicacion(View view) {
        Intent ubicacion = new Intent(this,Ubicacion.class);
        if (latitude!=null && longitude!=null){
            ubicacion.putExtra("latitude", latitude);
            ubicacion.putExtra("longitude", longitude);
            ubicacion.putExtra("ocultarUbicacion",ocultarUbicacion);
        }
        startActivityForResult(ubicacion, REQUEST_CODE_UBICACION);
    }

    private void getUser(){
        userProvider.getUser(auth.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){

                    if (documentSnapshot.contains("username")) {
                        nombre = documentSnapshot.getString("username");
                        nombreUsuario.setText(nombre);
                    }
                    if (documentSnapshot.contains("ubicacion")){
                        ArrayList<Double> ubicacion = (ArrayList<Double>) documentSnapshot.get("ubicacion");
                        if (ubicacion!=null){
                            latitude = ubicacion.get(0);
                            longitude = ubicacion.get(1);
                            localizacion(latitude,longitude);
                        }
                    }

                    if (documentSnapshot.contains("ocultarUbicacion")) {
                        ocultarUbicacion = documentSnapshot.getBoolean("ocultarUbicacion");
                    }

                    if (documentSnapshot.contains("urlPerfil")){
                        urlFotoPerfil = documentSnapshot.getString("urlPerfil");
                        if (urlFotoPerfil!= null){
                            Picasso.get().load(urlFotoPerfil).into(fotoPerfil);
                        }
                    }

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==REQUEST_CODE_UBICACION){
            if (resultCode== Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                latitude =  extras.getDouble("latitude");
                longitude = extras.getDouble("longitude");
                ocultarUbicacion = extras.getBoolean("ocultarUbicacion");

                localizacion(latitude,longitude);
            }
        } else if(requestCode==GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                imagen = FileUtil.from(this,data.getData());

                fotoPerfil.setImageBitmap(BitmapFactory.decodeFile(imagen.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR: ", e.getMessage());
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMARA_REQUEST_CODE && resultCode == RESULT_OK){
            fotoPerfil.setImageBitmap(BitmapFactory.decodeFile(imagen.getAbsolutePath()));
        }

        super.onActivityResult(requestCode, resultCode, data);
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
                botonUbicacion.setText(String.format("%s, %s", city, postalCode));
            } else {
                if (state!=null){
                    botonUbicacion.setText(String.format("%s, %s", state,country));
                } else {
                    botonUbicacion.setText(String.format("%s", country));
                }
            }
        } else {
            if (state!=null){
                botonUbicacion.setText(String.format("%s, %s", state,country));
            } else {
                botonUbicacion.setText(String.format("%s", country));
            }
        }
    }


    private void subirActualizacionPerfil() {
        if (imagen!=null) {
            imageProvider.save(EditarPerfil.this, imagen, 0).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageProvider.getStorage().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            User usuario = new User();
                            usuario.setUsername(nombre);
                            usuario.setId(auth.getUid());
                            if (latitude != null && longitude != null) {
                                ArrayList<Double> ubicacion= new ArrayList<>();
                                ubicacion.add(latitude);
                                ubicacion.add(longitude);
                                usuario.setUbicacion(ubicacion);
                                usuario.setOcultarUbicacion(ocultarUbicacion);
                            }
                            usuario.setUrlPerfil(url);

                            userProvider.update(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        dialog.dismiss();
                                        Toast.makeText(EditarPerfil.this, getString(R.string.perfil_actualizado), Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(EditarPerfil.this, getString(R.string.perfil_no_actualizado), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        } else {
            User usuario = new User();
            usuario.setId(auth.getUid());
            if (!urlFotoPerfil.equals(""))
                usuario.setUrlPerfil(urlFotoPerfil);
            usuario.setUsername(nombre);
            if (latitude != null && longitude != null) {
                ArrayList<Double> ubicacion= new ArrayList<>();
                ubicacion.add(latitude);
                ubicacion.add(longitude);
                usuario.setUbicacion(ubicacion);
                usuario.setOcultarUbicacion(ocultarUbicacion);
            }
            userProvider.update(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        dialog.dismiss();
                        Toast.makeText(EditarPerfil.this, getString(R.string.perfil_actualizado), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(EditarPerfil.this, getString(R.string.perfil_no_actualizado), Toast.LENGTH_LONG).show();
                    }
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

            imagen = null;
            try {
                imagen = FileUtil.fileCamera(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imagen != null) {
                Uri fotoUri = FileProvider.getUriForFile(
                        this,
                        "com.aplicacion.mypet",
                        imagen);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(cameraIntent,CAMARA_REQUEST_CODE);
            }
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
            fotoPerfil.setImageResource(R.drawable.ic_person_2);
            imagen=null;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CAMERA) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            } else {
                Toast.makeText(EditarPerfil.this, getString(R.string.permisos), Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == REQUEST_PERMISSION_GALLERY) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            } else {
                Toast.makeText(EditarPerfil.this, getString(R.string.permisos), Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void subirFoto(View view) {
        BottomSheetFragmentPersonalizado bottomSheet = new BottomSheetFragmentPersonalizado();
        bottomSheet.show(getSupportFragmentManager(),"TAG");
    }


    public void actualizarPerfil(View view) {
        nombre = nombreUsuario.getText().toString();
        if (!nombre.isEmpty()) {
            dialog.show();
            subirActualizacionPerfil();
        } else {
            Toast.makeText(this, getString(R.string.campos_vacios), Toast.LENGTH_LONG).show();
        }

    }


}