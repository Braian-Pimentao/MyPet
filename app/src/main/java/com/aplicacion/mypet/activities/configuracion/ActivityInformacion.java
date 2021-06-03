package com.aplicacion.mypet.activities.configuracion;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;

public class ActivityInformacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
    }

    public void cerrarInformacion(View view) {
        finish();
    }

    public void enviarCorreo(View view) {
        String[] TO = {"application.mypet@gmail.com"};


        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.asunto));

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email."));
            Log.i("EMAIL", "Enviando email...");
        }
        catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "NO existe ningún cliente de email instalado!.", Toast.LENGTH_SHORT).show();
        }
    }

    public void pulsarBotonRedesSociales(View view) {
        ImageView imagenPulsada = (ImageView) view;

        if (imagenPulsada.getId() == R.id.boton_instagram) {
            abrirInstagram();
        } else if (imagenPulsada.getId() == R.id.boton_twitter){
            abrirTwitter();
        }
    }

    private void abrirTwitter() {
        Uri uri = Uri.parse("twitter://user?screen_name=MyPetApplicati1");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.twitter.android");
        try {
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/#!/MyPetApplicati1")));
        }
    }

    private void abrirInstagram() {
        Uri uri = Uri.parse("http://instagram.com/_u/mypet.application");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.instagram.android");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

            //No encontró la aplicación, abre la versión web.
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/mypet.application")));

        }
    }
}