package com.aplicacion.mypet.activities.publicar

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplicacion.mypet.R
import com.aplicacion.mypet.adaptadores.AdaptadorPublicacion
import com.aplicacion.mypet.models.Publicacion
import com.aplicacion.mypet.providers.AuthProvider
import com.aplicacion.mypet.providers.PublicacionProvider
import com.aplicacion.mypet.utils.ViewedMessageHelper
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class FiltrosActivity : AppCompatActivity() {
    private var extraTipoAnimal: String? = null
    private var tipoAnimalTextView: TextView? = null
    private var contadorAnimalesFiltros: TextView? = null
    private var auth: AuthProvider? = null

    private var recyclerView: RecyclerView? = null
    private var publicacionProvider: PublicacionProvider? = null
    private var adaptadorPublicacion: AdaptadorPublicacion? = null

    private var linearLayoutNoBusqueda: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtros)

        extraTipoAnimal = intent.getStringExtra("tipo")

        recyclerView = findViewById(R.id.listar_anuncios_filtro)
        auth = AuthProvider()


        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = linearLayoutManager
        publicacionProvider = PublicacionProvider()

        tipoAnimalTextView = findViewById(R.id.tipo_animal_filtro)
        contadorAnimalesFiltros = findViewById(R.id.contador_animal_filtro)
        linearLayoutNoBusqueda = findViewById(R.id.mensaje_informativo_filtros)

        seleccionartipo()
    }

    override fun onStart() {
        super.onStart()
        val query = publicacionProvider!!.getPublicacionByCategoryAndTimesTamp(extraTipoAnimal)
        query.get().addOnSuccessListener { queryDocumentSnapshots ->
            if (queryDocumentSnapshots.size() == 0) {
                linearLayoutNoBusqueda!!.visibility = View.VISIBLE
            } else {
                linearLayoutNoBusqueda!!.visibility = View.GONE
            }
        }
        val options = FirestoreRecyclerOptions.Builder<Publicacion>()
                .setQuery(query, Publicacion::class.java)
                .build()
        if (auth!!.auth.currentUser != null) {
            ViewedMessageHelper.updateOnline(true, this@FiltrosActivity)
        }
        adaptadorPublicacion = AdaptadorPublicacion(options, this, contadorAnimalesFiltros)
        recyclerView!!.adapter = adaptadorPublicacion
        adaptadorPublicacion!!.startListening()
    }

    override fun onPause() {
        super.onPause()
        if (auth!!.auth.currentUser != null) {
            ViewedMessageHelper.updateOnline(false, this)
        }
    }

    override fun onStop() {
        super.onStop()
        adaptadorPublicacion!!.stopListening()
    }

    private fun seleccionartipo() {
        val tipos = resources.getStringArray(R.array.lista_animales)
        tipoAnimalTextView!!.text = String.format("%s:", tipos[extraTipoAnimal!!.toInt()])
    }

    fun cerrarFiltro(view: View?) {
        finish()
    }
}