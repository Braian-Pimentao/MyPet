package com.aplicacion.mypet.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.aplicacion.mypet.R
import com.aplicacion.mypet.activities.publicar.ActivityCrearPublicacion
import com.aplicacion.mypet.activities.sesion.IniciarSesion
import com.aplicacion.mypet.fragments.FragmentChats
import com.aplicacion.mypet.fragments.FragmentFavorito
import com.aplicacion.mypet.fragments.FragmentHome
import com.aplicacion.mypet.fragments.FragmentPerfil
import com.aplicacion.mypet.providers.AuthProvider
import com.aplicacion.mypet.providers.MensajeProvider
import com.aplicacion.mypet.providers.UserProvider
import com.aplicacion.mypet.utils.ViewedMessageHelper
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_UBICATION = 102
    private lateinit var mBottomNavigation: BottomNavigationView
    private lateinit var mAuth: AuthProvider
    private lateinit var mUserProvider: UserProvider
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var mMensajeProvider: MensajeProvider
    private lateinit var mBadge: BadgeDrawable
    private lateinit var mLayout : CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyPet)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_UBICATION)
        }
        mBottomNavigation = findViewById(R.id.nav_view)
        mBottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        mBadge = mBottomNavigation.getOrCreateBadge(R.id.navigation_mensajes)
        mBadge.backgroundColor = getColor(R.color.secundario_app)
        mBadge.maxCharacterCount = 10
        mBadge.isVisible = false


        mAuth = AuthProvider()
        mUserProvider = UserProvider()
        mMensajeProvider = MensajeProvider()

        openFragment(FragmentHome())
        contadorMensajes()
    }

    private fun comprobarDatos() {
        mUserProvider.getUser(mAuth.uid).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("ubicacion")){
                    if(documentSnapshot.get("ubicacion") == null) {
                        val rootView = window.decorView.findViewById<View>(R.id.container)
                        val snackbar = Snackbar.make(this, rootView, getString(R.string.recomendacion), Snackbar.LENGTH_INDEFINITE)
                        val snackbarLayout = snackbar.view as SnackbarLayout
                        val params = snackbarLayout.layoutParams as CoordinatorLayout.LayoutParams
                        snackbarLayout.layoutParams
                        params.gravity = Gravity.TOP
                        params.setMargins(
                                params.leftMargin+20,
                                params.topMargin+2600,
                                params.rightMargin,
                                params.bottomMargin
                        )
                        snackbar.show()

                        println("--------------------------------kjhkh-------------------------------" + snackbar.isShown)
                    }
                }
            }
        }
    }

    private var navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> openFragment(FragmentHome())
            R.id.navigation_fav -> openFragment(FragmentFavorito())
            R.id.navigation_mensajes -> openFragment(FragmentChats())
            R.id.navigation_perfil -> openFragment(FragmentPerfil())
        }
        true
    }

    fun openFragment(fragment: Fragment?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.auth.currentUser != null) {
            comprobarDatos()
            ViewedMessageHelper.updateOnline(true, this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (mAuth.auth.currentUser != null) {
            ViewedMessageHelper.updateOnline(false, this)
        }
    }

    override fun onBackPressed() {
        val mBottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        if (mBottomNavigationView.selectedItemId == R.id.navigation_home) {
            super.onBackPressed()
            finish()
        } else {
            mBottomNavigationView.selectedItemId = R.id.navigation_home
        }
    }

    fun botonPublicar(view: View?) {
        if (mAuth.auth.currentUser != null) {
            val publicar = Intent(this, ActivityCrearPublicacion::class.java)
            startActivity(publicar)
        } else {
            val items = Intent(this, IniciarSesion::class.java)
            startActivity(items)
        }
    }

    private fun contadorMensajes() {
        if (mAuth.auth.currentUser != null) {
            mMensajeProvider.getMensajesNoLeidosUsuario(mAuth.uid).addSnapshotListener { value, error ->
                if (value!!.size()>0) {
                    mBadge.isVisible = true
                    mBadge.number = value.size()
                } else {
                    mBadge.isVisible = false
                }

            }
        }
    }
}