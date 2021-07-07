package com.aplicacion.mypet.activities.configuracion

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aplicacion.mypet.R

class ActivityInformacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion)
    }

    fun cerrarInformacion(view: View?) {
        finish()
    }

    fun enviarCorreo(view: View?) {
        val to = arrayOf("application.mypet@gmail.com")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.asunto))
        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email."))
            Log.i("EMAIL", "Enviando email...")
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "NO existe ningún cliente de email instalado!.", Toast.LENGTH_SHORT).show()
        }
    }

    fun pulsarBotonRedesSociales(view: View) {
        val imagenPulsada = view as ImageView
        if (imagenPulsada.id == R.id.boton_instagram) {
            abrirInstagram()
        } else if (imagenPulsada.id == R.id.boton_twitter) {
            abrirTwitter()
        }
    }

    private fun abrirTwitter() {
        val uri = Uri.parse("twitter://user?screen_name=MyPetApplicati1")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.twitter.android")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/#!/MyPetApplicati1")))
        }
    }

    private fun abrirInstagram() {
        val uri = Uri.parse("http://instagram.com/_u/mypet.application")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.instagram.android")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {

            //No encontró la aplicación, abre la versión web.
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/mypet.application")))
        }
    }
}