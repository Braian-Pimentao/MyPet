package com.aplicacion.mypet.activities.perfil

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.aplicacion.mypet.R
import com.aplicacion.mypet.fragments.BottomSheetFragmentPersonalizado
import com.aplicacion.mypet.models.User
import com.aplicacion.mypet.providers.AuthProvider
import com.aplicacion.mypet.providers.ImageProvider
import com.aplicacion.mypet.providers.UserProvider
import com.aplicacion.mypet.utils.FileUtil
import com.aplicacion.mypet.utils.ViewedMessageHelper
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import dmax.dialog.SpotsDialog
import java.io.File
import java.io.IOException
import java.util.*

class EditarPerfil : AppCompatActivity(), BottomSheetFragmentPersonalizado.BottomSheetListener {
    private  val REQUEST_PERMISSION_CAMERA = 100
    private val REQUEST_PERMISSION_GALLERY = 101
    private lateinit var dialog: AlertDialog
    private var imagen: File? = null
    private lateinit var nombre: String
    private var ocultarUbicacion = false

    private lateinit var imageProvider: ImageProvider
    private lateinit var userProvider: UserProvider
    private var urlFotoPerfil : String? = ""

    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var botonUbicacion: Button

    private lateinit var fotoPerfil: CircleImageView
    private lateinit var nombreUsuario: TextInputEditText
    private lateinit var botonSubirFoto: ImageButton

    private lateinit var auth: AuthProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)


        ocultarUbicacion = false
        nombre = ""

        botonUbicacion = findViewById(R.id.boton_marcar_ubicacion)
        nombreUsuario = findViewById(R.id.campo_nombre_editar)
        fotoPerfil = findViewById(R.id.foto_perfil)
        botonSubirFoto = findViewById(R.id.boton_subir_foto)

        dialog = SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.actualizando_perfil)
                .setCancelable(false).build()

        imageProvider = ImageProvider()
        userProvider = UserProvider()
        auth = AuthProvider()

        getUser()
    }

    fun cerrarEditarPerfil(view: View?) {
        finish()
    }

    private fun getUser() {
        userProvider.getUser(auth.uid).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("username")) {
                    nombre = documentSnapshot.getString("username")!!
                    nombreUsuario.setText(nombre)
                }
                if (documentSnapshot.contains("ubicacion")) {
                    val ubicacion = documentSnapshot["ubicacion"] as ArrayList<Double>?
                    if (ubicacion != null) {
                        latitude = ubicacion[0]
                        longitude = ubicacion[1]
                        localizacion(latitude!!, longitude!!)
                    }
                }
                if (documentSnapshot.contains("ocultarUbicacion")) {
                    if (documentSnapshot["ocultarUbicacion"] != null) ocultarUbicacion = documentSnapshot.getBoolean("ocultarUbicacion")!!
                }
                if (documentSnapshot.contains("urlPerfil")) {
                    urlFotoPerfil = documentSnapshot.getString("urlPerfil")
                    if (urlFotoPerfil != null)
                        Picasso.get().load(urlFotoPerfil).into(fotoPerfil)

                }
            }
        }
    }

    var resultUbicacion = registerForActivityResult(
            StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val extras = result.data!!.extras
            latitude = extras!!.getDouble("latitude")
            longitude = extras.getDouble("longitude")
            ocultarUbicacion = extras.getBoolean("ocultarUbicacion")
            localizacion(latitude!!, longitude!!)
        }
    }

    var resultGaleria = registerForActivityResult(
            StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                imagen = FileUtil.from(this@EditarPerfil, result.data!!.data)
                fotoPerfil.setImageBitmap(BitmapFactory.decodeFile(imagen?.absolutePath))
            } catch (e: Exception) {
                Log.d("ERROR: ", e.message!!)
                Toast.makeText(this@EditarPerfil, "Error: " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    var resultCamara = registerForActivityResult(
            StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            fotoPerfil.setImageBitmap(BitmapFactory.decodeFile(imagen?.absolutePath))
        }
    }

    fun cambiarUbicacion(view: View?) {
        val ubicacion = Intent(this, Ubicacion::class.java)
        if (latitude != null && longitude != null) {
            ubicacion.putExtra("latitude", latitude!!)
            ubicacion.putExtra("longitude", longitude!!)
            ubicacion.putExtra("ocultarUbicacion", ocultarUbicacion)
        }
        resultUbicacion.launch(ubicacion)
    }

    private fun localizacion(latitude: Double, longitude: Double) {
        val geocoder: Geocoder
        var direccion: List<Address>? = null
        geocoder = Geocoder(this, Locale.getDefault())
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
                botonUbicacion.text = String.format("%s, %s", city, postalCode)
            } else {
                botonUbicacion.text = String.format("%s", country)
            }
        } else {
            if (city != null) {
                botonUbicacion.text = String.format("%s, %s", city, country)
            } else {
                botonUbicacion.text = String.format("%s", country)
            }
        }
    }

    private fun subirActualizacionPerfil() {
        if (imagen != null) {
            if (urlFotoPerfil!= "")
                imageProvider.deleteByUrl(urlFotoPerfil)

            imageProvider.save(this@EditarPerfil, imagen, 0).addOnSuccessListener {
                imageProvider.storage.addOnSuccessListener { uri ->
                    val url = uri.toString()
                    val usuario = User()
                    usuario.username = nombre
                    usuario.id = auth.uid
                    if (latitude != null && longitude != null) {
                        val ubicacion = ArrayList<Double>()
                        ubicacion.add(latitude!!)
                        ubicacion.add(longitude!!)
                        usuario.ubicacion = ubicacion
                        usuario.ocultarUbicacion = ocultarUbicacion
                    }
                    usuario.urlPerfil = url
                    userProvider.update(usuario).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            dialog.dismiss()
                            Toast.makeText(this@EditarPerfil, getString(R.string.perfil_actualizado), Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            dialog.dismiss()
                            Toast.makeText(this@EditarPerfil, getString(R.string.perfil_no_actualizado), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } else {
            val usuario = User()
            usuario.id = auth.uid
            usuario.username = nombre
            if (latitude != null && longitude != null) {
                val ubicacion = ArrayList<Double>()
                ubicacion.add(latitude!!)
                ubicacion.add(longitude!!)
                usuario.ubicacion = ubicacion
                usuario.ocultarUbicacion = ocultarUbicacion
            }
            userProvider.update(usuario).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dialog.dismiss()
                    Toast.makeText(this@EditarPerfil, getString(R.string.perfil_actualizado), Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    dialog.dismiss()
                    Toast.makeText(this@EditarPerfil, getString(R.string.perfil_no_actualizado), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        resultGaleria.launch(galleryIntent)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            imagen = null
            try {
                imagen = FileUtil.fileCamera(this)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (imagen != null) {
                val fotoUri = FileProvider.getUriForFile(
                        this,
                        "com.aplicacion.mypet",
                        imagen!!)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri)
                resultCamara.launch(cameraIntent)
            }
        }
    }

    override fun onButtonClicked(numero: Int) {
        if (numero == 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
            }
        } else if (numero == 1) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_GALLERY)
            }
        } else if (numero == 2) {
            fotoPerfil.setImageResource(R.drawable.ic_person_2)
            imagen = null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this@EditarPerfil, getString(R.string.permisos), Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == REQUEST_PERMISSION_GALLERY) {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this@EditarPerfil, getString(R.string.permisos), Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun subirFoto(view: View?) {
        val bottomSheet = BottomSheetFragmentPersonalizado()
        bottomSheet.show(supportFragmentManager, "TAG")
    }

    fun actualizarPerfil(view: View?) {
        nombre = nombreUsuario.text.toString()
        if (!nombre.isEmpty()) {
            dialog.show()
            subirActualizacionPerfil()
        } else {
            Toast.makeText(this, getString(R.string.campos_vacios), Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.auth.currentUser != null) {
            ViewedMessageHelper.updateOnline(true, this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (auth.auth.currentUser != null) {
            ViewedMessageHelper.updateOnline(false, this)
        }
    }
}