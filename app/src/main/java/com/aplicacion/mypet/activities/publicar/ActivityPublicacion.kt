package com.aplicacion.mypet.activities.publicar

import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.aplicacion.mypet.R
import com.aplicacion.mypet.activities.chat.ActivityChat
import com.aplicacion.mypet.activities.perfil.ActivityUsuario
import com.aplicacion.mypet.activities.sesion.IniciarSesion
import com.aplicacion.mypet.adaptadores.SliderAdaptador
import com.aplicacion.mypet.models.Favorito
import com.aplicacion.mypet.models.Reporte
import com.aplicacion.mypet.models.SliderItem
import com.aplicacion.mypet.providers.*
import com.aplicacion.mypet.utils.RelativeTime
import com.aplicacion.mypet.utils.ViewedMessageHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ActivityPublicacion : AppCompatActivity() {
    private var sliderView: SliderView? = null
    private lateinit var sliderAdaptador: SliderAdaptador
    private lateinit var listaSliderItem: ArrayList<SliderItem>
    private lateinit var idPublicacion: String
    private var ubicacionRecogida: ArrayList<Double>? = null
    private lateinit var idUser: String
    private var ocultarUbicacion = false
    private var urlImagenes: ArrayList<String>? = null


    private lateinit var publicacionProvider: PublicacionProvider
    private lateinit var userProvider: UserProvider
    private lateinit var favoritoProvider: FavoritoProvider
    private lateinit var authProvider: AuthProvider
    private lateinit var reporterProvider: ReporterProvider
    private lateinit var imageProvider: ImageProvider


    private lateinit var nombreAnimal: TextView
    private lateinit var nombreUsuario: TextView
    private lateinit var tipoAnimal: TextView
    private lateinit var edadAnimal: TextView
    private lateinit var razaAnimal: TextView
    private lateinit var descripcionAnimal: TextView
    private lateinit var ubicacion: TextView
    private lateinit var imagenSexo: ImageView

    private lateinit var imagenTipoAnimal: ImageView
    private lateinit var fotoUsuario: CircleImageView
    private lateinit var tiempo: TextView
    private lateinit var favoritos: TextView
    private lateinit var botonFavorito: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var reportarAnuncio: MenuItem
    private lateinit var borrarAnuncio: MenuItem
    private lateinit var botonChat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicacion)



        toolbar = findViewById<View>(R.id.toolBar_anuncio) as Toolbar
        toolbar!!.title = ""
        setSupportActionBar(toolbar)

        sliderView = findViewById(R.id.imageSlider)
        listaSliderItem = ArrayList()

        idPublicacion = intent.extras?.getString("id").toString()

        nombreAnimal = findViewById(R.id.nombre_animal_anuncio)
        edadAnimal = findViewById(R.id.edad_animal_anuncio)
        nombreUsuario = findViewById(R.id.nombre_usuario_anuncio)
        tipoAnimal = findViewById(R.id.tipo_animal_anuncio)
        razaAnimal = findViewById(R.id.raza_animal_anuncio)
        descripcionAnimal = findViewById(R.id.descripcion_animal_anuncio)
        ubicacion = findViewById(R.id.nombre_ubicacion_anuncio)
        ubicacion = findViewById(R.id.nombre_ubicacion_anuncio)
        imagenSexo = findViewById(R.id.sexo_anuncio)
        imagenTipoAnimal = findViewById(R.id.icono_tipo_animal)
        fotoUsuario = findViewById(R.id.foto_perfil_anuncio)
        tiempo = findViewById(R.id.tiempo_publicacion)
        favoritos = findViewById(R.id.contador_favoritos)
        botonFavorito = findViewById(R.id.fabFavorite)
        botonChat = findViewById(R.id.boton_abrir_chat_anuncio)

        idUser = ""
        publicacionProvider = PublicacionProvider()
        userProvider = UserProvider()
        favoritoProvider = FavoritoProvider()
        authProvider = AuthProvider()
        reporterProvider = ReporterProvider()
        imageProvider = ImageProvider()

        if (authProvider!!.auth.currentUser != null) {
            checkIsExistFavorite(idPublicacion, authProvider!!.uid)
        }


    }

    private fun getNumberFavorites() {
        favoritoProvider!!.getFavoriteByPost(idPublicacion).addSnapshotListener { value, error ->
            val numeroFavoritos = value!!.size()
            favoritos!!.text = numeroFavoritos.toString()
        }
    }

    private fun instanciarSlider() {
        sliderAdaptador = SliderAdaptador(this, listaSliderItem)
        sliderView!!.setSliderAdapter(sliderAdaptador!!)
        sliderView!!.indicatorSelectedColor = R.color.principal_app
        sliderView!!.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM)
        sliderView!!.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView!!.indicatorUnselectedColor = Color.GRAY
    }

    private fun getPost() {
        publicacionProvider.getPostById(idPublicacion).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("imagenes")) {
                    listaSliderItem.clear()
                    urlImagenes = documentSnapshot["imagenes"] as ArrayList<String>?
                    for (i in urlImagenes!!.indices) {
                        val item = SliderItem()
                        item.urlImagen = urlImagenes!![i]
                        listaSliderItem.add(item)
                    }
                    instanciarSlider()
                }

                if (documentSnapshot.contains("nombre")) {
                    nombreAnimal.text = documentSnapshot.getString("nombre")
                }

                if (documentSnapshot.contains("edad")) {
                    edadAnimal.text = documentSnapshot.getString("edad")
                }


                if (documentSnapshot.contains("sexo")) {
                    val sexo = documentSnapshot.getString("sexo")
                    if (sexo.equals(getString(R.string.desc_femenino), ignoreCase = true)) {
                        imagenSexo.setImageResource(R.drawable.ic_femenine)
                    } else if (sexo.equals(getString(R.string.desc_masculino), ignoreCase = true)) {
                        imagenSexo.setImageResource(R.drawable.ic_masculino)
                    }
                }

                if (documentSnapshot.contains("tipo")) {
                    val tipo = documentSnapshot.getString("tipo")!!.toInt()
                    tipoAnimal.text = resources.getStringArray(R.array.lista_animales)[tipo]
                    cambiarImagenTipo(tipo)
                }

                if (documentSnapshot.contains("raza")) {
                    razaAnimal.text = documentSnapshot.getString("raza")
                }

                if (documentSnapshot.contains("fechaPublicacion")) {
                    val timestamp = documentSnapshot.getLong("fechaPublicacion")!!
                    tiempo.text = RelativeTime.getTimeAgo(timestamp, this@ActivityPublicacion)
                }

                if (documentSnapshot.contains("descripcion")) {
                    descripcionAnimal.text = documentSnapshot.getString("descripcion")
                }
                if (documentSnapshot.contains("idUser")) {
                    idUser = documentSnapshot.getString("idUser")!!
                    getUser(idUser)
                    modificarMenu()
                    ocultarBotones()
                }
            }
        }
    }

    private fun ocultarBotones() {
        if (authProvider.auth.currentUser != null) {
            if (idUser == authProvider.uid) {
                botonFavorito.visibility = View.INVISIBLE
                botonChat.text = getString(R.string.actualizar_publicacion)
            }
        }
    }

    private fun modificarMenu() {
        if (authProvider.auth.currentUser != null) {
            if (idUser == authProvider.uid) {
                reportarAnuncio.isVisible = false
            } else {
                borrarAnuncio.isVisible = false
            }
        } else {
            borrarAnuncio.isVisible = false
        }
    }

    override fun onStart() {
        super.onStart()
        getPost()
        getNumberFavorites()
        if (authProvider.auth.currentUser != null) {
            ViewedMessageHelper.updateOnline(true, this@ActivityPublicacion)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_eliminar, menu)
        borrarAnuncio = menu.findItem(R.id.opcion_borrar)
        reportarAnuncio = menu.findItem(R.id.opcion_reportar)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item == borrarAnuncio) {
            mostrarConfirmacionBorrar(idPublicacion)
        } else if (item == reportarAnuncio) {
            reportar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reportar() {
        if (authProvider.auth.currentUser != null) {
            val reporte: Reporte = Reporte()
            reporte.idUser = idUser
            reporte.idPublicacion = idPublicacion

            val dialogo: AlertDialog
            val array = resources.getStringArray(R.array.lista_reportes)
            val builder = AlertDialog.Builder(this)

            builder.setTitle(getString(R.string.tipo_animal))
                    .setSingleChoiceItems(array, 0) { dialog, which ->
                        reporte.tipoReporte = array[which]
                        android.app.AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(getString(R.string.reportar))
                                .setMessage(getString(R.string.comprobar_reportar))
                                .setPositiveButton(getString(R.string.si)) { dialog2, which2 ->
                                    reporterProvider.crearReporte(reporte)
                                    AlertDialog.Builder(this@ActivityPublicacion)
                                            .setTitle(getString(R.string.reportar))
                                            .setMessage(getString(R.string.report_confirm))
                                            .setPositiveButton("OK", null)
                                            .show()
                                }
                                .setNegativeButton(getString(R.string.no), null).show()
                            dialog.cancel()
                    }
            dialogo = builder.create()
            dialogo.show()
        }
    }

    private fun mostrarConfirmacionBorrar(idPublicacion: String) {
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.eliminar))
                .setMessage(getString(R.string.comprobar_eliminar))
                .setPositiveButton(getString(R.string.si)) { dialog, which -> deletePublicacion() }
                .setNegativeButton(getString(R.string.no), null).show()
    }

    private fun deletePublicacion() {
        favoritoProvider.deleteByPublicacion(idPublicacion).get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val querySnapshotList: List<DocumentSnapshot> = querySnapshot.documents

                for (d in querySnapshotList) {
                    favoritoProvider.delete(d.id)
                }
            }
        }

        for (i in urlImagenes!!.indices) {
            imageProvider.deleteByUrl(urlImagenes!![i])
        }

        publicacionProvider.delete(idPublicacion).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@ActivityPublicacion, getString(R.string.publicacion_eliminada), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun cambiarImagenTipo(tipo: Int) {
        val tipos = resources.getStringArray(R.array.lista_animales)
        when (tipo) {
            0 -> imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_perro))
            1 -> imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_gato))
            2 -> imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_conejo))
            3 -> imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_ardilla))
            4 -> imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_tortuga))
            5 -> imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_pez))
            6 -> imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_pajaro))
            7 -> imagenTipoAnimal.setImageDrawable(getDrawable(R.drawable.ic_serpiente))
        }
    }

    private fun localizacion(latitude: Double, longitude: Double) {
        var direccion: List<Address>? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            direccion = geocoder.getFromLocation(latitude, longitude, 1) // 1 representa la cantidad de resultados a obtener
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val city = direccion!![0].locality
        val postalCode = direccion[0].postalCode
        val country = direccion[0].countryName
        if (postalCode != null) {
            if (city != null) {
                ubicacion.text = String.format("%s, %s", city, postalCode)
            } else {
                ubicacion.text = String.format("%s", country)
            }
        } else {
            if (city != null) {
                ubicacion.text = String.format("%s, %s", city, country)
            } else {
                ubicacion.text = String.format("%s", country)
            }
        }
    }

    private fun getUser(idUser: String) {
        userProvider.getUser(idUser).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("username")) {
                    nombreUsuario.text = documentSnapshot.getString("username")
                }
                if (documentSnapshot.contains("ubicacion")) {
                    ubicacionRecogida = documentSnapshot["ubicacion"] as ArrayList<Double>?
                    if (ubicacionRecogida != null) {
                        localizacion(ubicacionRecogida!![0], ubicacionRecogida!![1])
                        val mapFragment = supportFragmentManager
                                .findFragmentById(R.id.map_publicacion) as SupportMapFragment?
                        mapFragment!!.getMapAsync(Mapa())
                    } else {
                        ubicacion.text = getString(R.string.sin_ubicacion_seleccionada)
                    }
                    if (documentSnapshot.contains("ocultarUbicacion")) {
                        if (documentSnapshot.getBoolean("ocultarUbicacion") != null) {
                            ocultarUbicacion = documentSnapshot.getBoolean("ocultarUbicacion")!!
                        }
                    }
                }
                if (documentSnapshot.contains("urlPerfil")) {
                    val urlFotoPerfil = documentSnapshot.getString("urlPerfil")
                    if (urlFotoPerfil != null) {
                        Picasso.get().load(urlFotoPerfil).into(fotoUsuario)
                    }
                }
            }
        }
    }

    fun cerrarPublicacion(view: View?) {
        finish()
    }

    fun verPerfil(view: View?) {
        if (!idUser.isEmpty()) {
            val perfil = Intent(this, ActivityUsuario::class.java)
            perfil.putExtra("idUser", idUser)
            startActivity(perfil)
        }
    }

    fun darFavorito(view: View?) {
        if (authProvider.auth.currentUser != null) {
            val favorito = Favorito()
            favorito.idUser = authProvider.uid
            favorito.idPublicacion = idPublicacion
            favorito.timestamp = Date().time
            favorito(favorito)
        } else {
            val items = Intent(this, IniciarSesion::class.java)
            startActivity(items)
        }
    }

    private fun checkIsExistFavorite(idPublicacion: String, idUser: String) {
        favoritoProvider.getFavoriteByPostAndUser(idPublicacion, idUser).get().addOnSuccessListener { queryDocumentSnapshots ->
            val numeroDocumentos = queryDocumentSnapshots.size()
            if (numeroDocumentos > 0) {
                botonFavorito.setImageResource(R.drawable.ic_favorito)
            } else {
                botonFavorito.setImageResource(R.drawable.ic_no_favorito)
            }
        }
    }

    private fun favorito(favorito: Favorito) {
        favoritoProvider.getFavoriteByPostAndUser(favorito.idPublicacion, authProvider.uid).get().addOnSuccessListener { queryDocumentSnapshots ->
            val numeroDocumentos = queryDocumentSnapshots.size()
            if (numeroDocumentos > 0) {
                val idFavorito = queryDocumentSnapshots.documents[0].id
                botonFavorito.setImageResource(R.drawable.ic_no_favorito)
                favoritoProvider.delete(idFavorito)
            } else {
                botonFavorito.setImageResource(R.drawable.ic_favorito)
                favoritoProvider.create(favorito)
            }
        }
    }

    fun botonChat(view: View) {
        val boton = view as Button
        if (boton.text == getString(R.string.actualizar_publicacion)) {
            var intent = Intent(this,ActivityCrearPublicacion::class.java)
            intent.putExtra("idPublicacion", idPublicacion)
            startActivity(intent)
        } else if (boton.text == getString(R.string.chat)) {
            if (authProvider.auth.currentUser != null) {
                val intent = Intent(this, ActivityChat::class.java)
                intent.putExtra("idUser1", authProvider.uid)
                intent.putExtra("idUser2", idUser)
                startActivity(intent)
            } else {
                val iniciarSesion = Intent(this, IniciarSesion::class.java)
                startActivity(iniciarSesion)
            }
        }
    }

    inner class Mapa() : OnMapReadyCallback {

        private var mMap: GoogleMap? = null

        override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap
            val latLng = LatLng(ubicacionRecogida!![0], ubicacionRecogida!![1])
            if (!ocultarUbicacion) {
                mMap!!.addMarker(MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)))
            } else {
                val circuloMarca = mMap!!.addCircle(CircleOptions().center(latLng).radius(600.0))
                circuloMarca.fillColor = getColor(R.color.negro_opaco)
                circuloMarca.strokeColor = getColor(R.color.opaco)
            }
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
        }
    }


}