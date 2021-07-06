package com.aplicacion.mypet.activities.publicar

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.aplicacion.mypet.R
import com.aplicacion.mypet.fragments.BottomSheetFragmentPersonalizado
import com.aplicacion.mypet.models.Publicacion
import com.aplicacion.mypet.providers.AuthProvider
import com.aplicacion.mypet.providers.ImageProvider
import com.aplicacion.mypet.providers.PublicacionProvider
import com.aplicacion.mypet.utils.FileUtil
import com.aplicacion.mypet.utils.ViewedMessageHelper
import com.google.android.material.textfield.TextInputEditText
import dmax.dialog.SpotsDialog
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ActivityCrearPublicacion : AppCompatActivity(),BottomSheetFragmentPersonalizado.BottomSheetListener {
    private val REQUEST_PERMISSION_CAMERA = 100
    private val REQUEST_PERMISSION_GALLERY = 101

    private var authProvider: AuthProvider? = null
    private var contador = 0


    private lateinit var publicacionProvider: PublicacionProvider
    private lateinit var imagenes: Array<File?>
    private lateinit var imagenesAlmacenadas: ArrayList<File?>
    private lateinit var imageProvider: ImageProvider
    private lateinit var imagenSeleccionada: ImageView
    private var fotoCamaraArchivo: File? = null

    private lateinit var boton_tipo: Button
    private lateinit var inputTextNombre: TextInputEditText
    private lateinit var inputTextEdad: TextInputEditText
    private lateinit var inputTextRaza: TextInputEditText
    private lateinit var inputTextDescripcion: TextInputEditText
    private var posicionArraytipo = 0

    private lateinit var nombre: String
    private lateinit var edad: String
    private lateinit var tipo: String
    private lateinit var raza: String
    private lateinit var descripcion: String
    private lateinit var sexo: String
    private lateinit var urlImagenes: ArrayList<String>

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_publicacion)

        imageProvider = ImageProvider()
        authProvider = AuthProvider()
        publicacionProvider = PublicacionProvider()

        sexo =""
        imagenes = arrayOfNulls(5)

        boton_tipo = findViewById(R.id.boton_cambiar_tipo)
        inputTextNombre = findViewById(R.id.campo_nombre_animal)
        inputTextEdad = findViewById(R.id.campo_edad)
        inputTextRaza = findViewById(R.id.campo_raza)
        inputTextDescripcion = findViewById(R.id.campo_descripcion)

        urlImagenes = ArrayList()
        imagenesAlmacenadas = ArrayList()

        dialog = SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.subiendo_publicacion)
                .setCancelable(false).build()



    }

    fun cancelarPublicacion(v: View?) {
        finish()
    }

    fun cambiarTipoAnimal(view: View?) {
        val dialogo: AlertDialog
        val array = resources.getStringArray(R.array.lista_animales)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.tipo_animal))
                .setSingleChoiceItems(array, 0) { dialog, which ->
                    boton_tipo.text = array[which]
                    posicionArraytipo = which
                    dialog.cancel()
                }

        dialogo = builder.create()
        dialogo.show()
    }

    fun elegirImagen(view: View) {
        imagenSeleccionada = view as ImageView
        val bottomSheet = BottomSheetFragmentPersonalizado()
        bottomSheet.show(supportFragmentManager, "TAG")
    }

    fun seleccionarSexo(view: View?) {
        val imagenMasculino: ImageView = findViewById(R.id.masculino)
        val imagenFemenino: ImageView = findViewById(R.id.femenino)

        val linearSexo = view as LinearLayout

        if (linearSexo.id == R.id.sexo_masculino) {
            imagenFemenino.setBackgroundColor(resources.getColor(R.color.white, theme))
            imagenMasculino.setBackgroundColor(resources.getColor(R.color.principal_app, theme))
        } else if (linearSexo.id == R.id.sexo_femenino) {
            imagenMasculino.setBackgroundColor(resources.getColor(R.color.white, theme))
            imagenFemenino.setBackgroundColor(resources.getColor(R.color.principal_app, theme))
        }

        sexo = linearSexo.contentDescription as String
    }

    fun subirPublicacion(view: View?) {
        nombre = inputTextNombre.text.toString()
        edad = inputTextEdad.text.toString()
        tipo = boton_tipo.text.toString()
        raza = inputTextRaza.text.toString()
        descripcion = inputTextDescripcion.text.toString()

        if(nombre.isNotEmpty() && edad.isNotEmpty() && !tipo.equals(getString(R.string.tipo_animal))
                && raza.isNotEmpty() && sexo.isNotEmpty() && descripcion.isNotEmpty() && imagenesAlmacenadas.size > 0) {
            dialog.show()
            saveImages()
        } else {
            Toast.makeText(this@ActivityCrearPublicacion, getString(R.string.campos_vacios), Toast.LENGTH_LONG).show()
        }
    }

    private fun saveImages() {
        if (contador < imagenesAlmacenadas.size) {
            contador++
            imageProvider.save(this, imagenesAlmacenadas.get(contador - 1), contador - 1).addOnSuccessListener { taskSnapshot ->
                imageProvider.storage.addOnSuccessListener { uri ->
                    urlImagenes.add(uri.toString())

                    if (contador == imagenesAlmacenadas.size) {
                        val publicacion = Publicacion()

                        publicacion.nombre = nombre.trim()
                        publicacion.edad = edad.trim()
                        publicacion.tipo = posicionArraytipo.toString()
                        publicacion.raza = raza.trim()
                        publicacion.descripcion = descripcion.trim()
                        publicacion.sexo = sexo.trim()
                        publicacion.fechaPublicacion = Date().time
                        publicacion.idUser = authProvider!!.uid
                        publicacion.imagenes = urlImagenes

                        publicacionProvider.save(publicacion).addOnCompleteListener { taskSave ->
                            if (taskSave.isSuccessful) {
                                dialog.dismiss()
                                Toast.makeText(this@ActivityCrearPublicacion, getString(R.string.animal_publicado), Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                Toast.makeText(this@ActivityCrearPublicacion, getString(R.string.error_animal_publicado), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    saveImages()
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
            try {
                fotoCamaraArchivo = FileUtil.fileCamera(this)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (fotoCamaraArchivo != null) {
                val fotoUri = FileProvider.getUriForFile(
                        this,
                        "com.aplicacion.mypet",
                        fotoCamaraArchivo!!)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri)
                resultCamara.launch(cameraIntent)
            }
        }
    }

    var resultGaleria = registerForActivityResult(
            StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                val imagen = FileUtil.from(this@ActivityCrearPublicacion, result.data!!.data)
                asignarImagenesArray(imagen)
                imagenSeleccionada.setImageBitmap(BitmapFactory.decodeFile(imagen.absolutePath))
            } catch (e: Exception) {
                Log.d("ERROR: ", e.message!!)
                Toast.makeText(this@ActivityCrearPublicacion, "Error: " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    var resultCamara = registerForActivityResult(
            StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            asignarImagenesArray(fotoCamaraArchivo!!)
            imagenSeleccionada.setImageBitmap(BitmapFactory.decodeFile(fotoCamaraArchivo!!.absolutePath))
        }
    }

    private fun asignarImagenesArray(file: File) {
        if (imagenSeleccionada.id == R.id.imagen_publicar_1) {
            imagenes[0] = file
        } else if (imagenSeleccionada.id == R.id.imagen_publicar_2) {
            imagenes[1] = file
        } else if (imagenSeleccionada.id == R.id.imagen_publicar_3) {
            imagenes[2] = file
        } else if (imagenSeleccionada.id == R.id.imagen_publicar_4) {
            imagenes[3] = file
        } else if (imagenSeleccionada.id == R.id.imagen_publicar_5) {
            imagenes[4] = file
        }
        actualizarArrayListImagenes()
    }

    private fun eliminarImagenesArray() {
        if (imagenSeleccionada.id == R.id.imagen_publicar_1) {
            imagenes[0] = null
        } else if (imagenSeleccionada.id == R.id.imagen_publicar_2) {
            imagenes[1] = null
        } else if (imagenSeleccionada.id == R.id.imagen_publicar_3) {
            imagenes[2] = null
        } else if (imagenSeleccionada.id == R.id.imagen_publicar_4) {
            imagenes[3] = null
        } else if (imagenSeleccionada.id == R.id.imagen_publicar_5) {
            imagenes[4] = null
        }
        actualizarArrayListImagenes()
    }

    private fun actualizarArrayListImagenes() {
        imagenesAlmacenadas = ArrayList()
        for (imagen in imagenes) {
            if (imagen != null) imagenesAlmacenadas.add(imagen)
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
            imagenSeleccionada.setImageResource(R.mipmap.ic_subir_foto)
            eliminarImagenesArray()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this@ActivityCrearPublicacion, getString(R.string.permisos), Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == REQUEST_PERMISSION_GALLERY) {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this@ActivityCrearPublicacion, getString(R.string.permisos), Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        ViewedMessageHelper.updateOnline(true, this@ActivityCrearPublicacion)
    }


    override fun onPause() {
        super.onPause()
        ViewedMessageHelper.updateOnline(false, this)
    }
}