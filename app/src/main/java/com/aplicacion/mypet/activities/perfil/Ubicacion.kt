@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.aplicacion.mypet.activities.perfil

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.aplicacion.mypet.R
import com.aplicacion.mypet.utils.ViewedMessageHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class Ubicacion : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private val RADIO: Double = 600.0
    private lateinit var locationManager: LocationManager
    private lateinit var ubicacionReal: Location

    private lateinit var ubicacionMarca: Location
    private lateinit var marcador: Marker
    private lateinit var ocultarUbicacion: CheckBox
    private lateinit var circuloMarca: Circle
    private var verificarOcultarUbicacion = false

    private var latLngRecibida: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubicacion)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val extras: Bundle? = intent.extras

        ocultarUbicacion = findViewById(R.id.ocultar_ubicacion)

        if (extras != null) {
            latLngRecibida = LatLng(extras.getDouble("latitude"), extras.getDouble("longitude"))
            ubicacionMarca = Location("Marca")
            verificarOcultarUbicacion = extras.getBoolean("ocultarUbicacion")
            ocultarUbicacion.isChecked = verificarOcultarUbicacion
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)

        if (latLngRecibida!= null) {
            ubicacionMarca.latitude = latLngRecibida!!.latitude
            ubicacionMarca.longitude = latLngRecibida!!.longitude
            if (!ocultarUbicacion.isChecked) {
                marcador = mMap.addMarker(MarkerOptions().position(latLngRecibida)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true))
            } else {
                circuloMarca = mMap.addCircle(CircleOptions().center(latLngRecibida).radius(RADIO))
                circuloMarca.fillColor = getColor(R.color.negro_opaco)
                circuloMarca.strokeColor = getColor(R.color.opaco)
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRecibida, 15F))
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ubicacionReal = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        mMap.isMyLocationEnabled=true
    }

    fun cerrarUbicacion(view: View?) {
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun pulsarBoton(view: View?) {
        val botonPulsado: Button = view as Button

        if (botonPulsado.id == R.id.boton_gurdar_ubicacion) {
            if (locationManager!= null) {
                val data = Intent()
                data.putExtra("latitude", ubicacionReal.latitude)
                data.putExtra("longitude", ubicacionReal.longitude)
                data.putExtra("ocultarUbicacion", verificarOcultarUbicacion)
                setResult(RESULT_OK, data)
                finish()
            }
        } else if (botonPulsado.id == R.id.boton_gurdar_marca) {
            if (ubicacionMarca != null) {
                val data = Intent()
                data.putExtra("latitude", ubicacionMarca.latitude)
                data.putExtra("longitude", ubicacionMarca.longitude)
                data.putExtra("ocultarUbicacion", verificarOcultarUbicacion)
                setResult(RESULT_OK, data)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.colocar_marcador), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapClick(p0: LatLng) {
        if (!verificarOcultarUbicacion) {
            if (marcador!=null) {
                marcador.remove()
            }

            if (ubicacionMarca == null) {
                ubicacionMarca = Location("Marca")
            }

            marcador = mMap.addMarker(MarkerOptions().position(p0)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true))
        } else {
            if (circuloMarca != null) {
                circuloMarca.remove()
            }

            if (ubicacionMarca == null) {
                ubicacionMarca = Location("Marca")
            }
            circuloMarca = mMap.addCircle(CircleOptions().center(p0).radius(RADIO))
            circuloMarca.fillColor = getColor(R.color.negro_opaco)
            circuloMarca.strokeColor = getColor(R.color.opaco)
            ubicacionMarca.latitude = p0.latitude
            ubicacionMarca.longitude = p0.longitude
        }
    }

    fun cambiarOcultarUbicacion(view: View?) {
        verificarOcultarUbicacion = !verificarOcultarUbicacion
        if (verificarOcultarUbicacion) {
            if (marcador != null) {
                circuloMarca = mMap.addCircle(CircleOptions()
                        .center(marcador.position)
                        .radius(RADIO))
                circuloMarca.fillColor = getColor(R.color.negro_opaco)
                circuloMarca.strokeColor = getColor(R.color.opaco)
                marcador.remove()
            }
        } else {
            if (circuloMarca != null) {
                marcador = mMap.addMarker(MarkerOptions().position(circuloMarca.center)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).draggable(true))
                circuloMarca.remove()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        ViewedMessageHelper.updateOnline(true, this)
    }


    override fun onPause() {
        super.onPause()
        ViewedMessageHelper.updateOnline(false, this)
    }

}