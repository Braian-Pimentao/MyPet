package com.aplicacion.mypet.activities.perfil

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplicacion.mypet.R
import com.aplicacion.mypet.adaptadores.AdaptadorPublicacion
import com.aplicacion.mypet.models.Publicacion
import com.aplicacion.mypet.providers.AuthProvider
import com.aplicacion.mypet.providers.PublicacionProvider
import com.aplicacion.mypet.providers.UserProvider
import com.aplicacion.mypet.utils.ViewedMessageHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ActivityUsuario : AppCompatActivity() {

    private lateinit var nombre: String
    private lateinit var imagenPerfil: CircleImageView
    private lateinit var nombrePerfil: TextView
    private lateinit var publicacionesContador: TextView
    private lateinit var idUser: String
    private lateinit var recyclerView: RecyclerView


    private lateinit var publicacionProvider: PublicacionProvider
    private lateinit var auth: AuthProvider
    private lateinit var userProvider: UserProvider
    private lateinit var adaptadorPublicacion: AdaptadorPublicacion

    private lateinit var noHayPublicaciones: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        auth = AuthProvider()
        userProvider = UserProvider()
        imagenPerfil = findViewById(R.id.imagen_perfil_user)
        nombrePerfil = findViewById(R.id.nombre_perfil_user)
        publicacionesContador = findViewById(R.id.numero_publicaciones)
        publicacionProvider = PublicacionProvider()
        noHayPublicaciones = findViewById(R.id.mensaje_informativo_usuario)

        recyclerView = findViewById(R.id.listar_anuncios_usuario)

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        idUser = intent.getStringExtra("idUser")!!


        getUser()
        getPostNumber()
    }

    private fun getPostNumber() {
        publicacionProvider.getPostByUser(idUser).get().addOnSuccessListener { queryDocumentSnapshots ->
            val numeroPublicaciones = queryDocumentSnapshots.size()
            publicacionesContador.text = numeroPublicaciones.toString()
            if (numeroPublicaciones > 0) {
                noHayPublicaciones.visibility = View.GONE
            } else {
                noHayPublicaciones.visibility = View.VISIBLE
            }
        }
    }

    private fun getUser() {
        userProvider.getUser(idUser).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("username")) {
                    nombre = documentSnapshot.getString("username")!!
                    nombrePerfil.text = nombre
                }
                if (documentSnapshot.contains("urlPerfil")) {
                    val urlFotoPerfil = documentSnapshot.getString("urlPerfil")
                    if (urlFotoPerfil != null) {
                        Picasso.get().load(urlFotoPerfil).into(imagenPerfil)
                    }
                }
            } else {
                Toast.makeText(this@ActivityUsuario, "No existe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val query = publicacionProvider.getPostByUser(idUser)
        val options = FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query, Publicacion::class.java)
                .build()
        adaptadorPublicacion = AdaptadorPublicacion(options, this)
        recyclerView.adapter = adaptadorPublicacion
        adaptadorPublicacion.startListening()
        if (auth.auth.currentUser != null) ViewedMessageHelper.updateOnline(true, this@ActivityUsuario)
    }

    override fun onStop() {
        super.onStop()
        adaptadorPublicacion.stopListening()
    }

    override fun onPause() {
        super.onPause()
        if (auth.auth.currentUser != null) {
            ViewedMessageHelper.updateOnline(false, this)
        }
    }

    fun cerrarPerfil(v: View?) {
        finish()
    }
}