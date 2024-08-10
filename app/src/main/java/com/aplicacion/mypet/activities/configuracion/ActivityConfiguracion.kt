package com.aplicacion.mypet.activities.configuracion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aplicacion.mypet.R
import com.aplicacion.mypet.activities.perfil.AvisoEliminar
import com.aplicacion.mypet.activities.perfil.EditarPerfil
import com.aplicacion.mypet.providers.AuthProvider
import com.aplicacion.mypet.utils.AppInfo
import com.aplicacion.mypet.utils.ViewedMessageHelper

class ActivityConfiguracion : AppCompatActivity() {
    private lateinit var mAuthProvider: AuthProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        mAuthProvider = AuthProvider()
    }

    fun cerrarConfiguracion(view: View?) {
        finish()
    }

    fun cerrarSesion(view: View?) {
        if (mAuthProvider.auth.currentUser != null) {
            mAuthProvider.auth.signOut()
            Toast.makeText(this, getString(R.string.sesion_cerrada), Toast.LENGTH_LONG).show()
            AppInfo.aviso(false)
            finish()
        }
    }

    fun editarPerfil(view: View?) {
        val editarPerfil = Intent(this, EditarPerfil::class.java)
        startActivity(editarPerfil)
    }

    override fun onStart() {
        super.onStart()
        if (mAuthProvider.auth.currentUser != null) {
            ViewedMessageHelper.updateOnline(true, this@ActivityConfiguracion)
        }
    }


    override fun onPause() {
        super.onPause()
        if (mAuthProvider.auth.currentUser != null) {
            ViewedMessageHelper.updateOnline(false, this)
        }
    }

    fun eliminarUsuario(view: View) {
        val eliminarUsuario = Intent(this, AvisoEliminar::class.java)
        startActivity(eliminarUsuario)
    }
}