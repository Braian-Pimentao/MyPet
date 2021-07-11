package com.aplicacion.mypet.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import com.google.android.gms.ads.MobileAds
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_UBICATION = 102
    private lateinit var mBottomNavigation: BottomNavigationView
    private lateinit var mAuth: AuthProvider
    private lateinit var mUserProvider: UserProvider
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private lateinit var mMensajeProvider: MensajeProvider
    private lateinit var mBadge: BadgeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyPet)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_UBICATION)
        }
        mBottomNavigation = findViewById(R.id.nav_view)
        mBottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        mBadge = mBottomNavigation.getOrCreateBadge(R.id.navigation_mensajes)
        mBadge.backgroundColor = getColor(R.color.secundario_app)
        mBadge.maxCharacterCount = 10


        mAuth = AuthProvider()
        mUserProvider = UserProvider()
        mMensajeProvider = MensajeProvider()

        openFragment(FragmentHome())
        contadorMensajes()
    }

    var navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> openFragment(FragmentHome())
            R.id.navigation_fav -> openFragment(FragmentFavorito())
            R.id.navigation_mensajes -> openFragment(FragmentChats())
            R.id.navigation_perfil ->  openFragment(FragmentPerfil())
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
            mMensajeProvider.getMensajesNoLeidosUsuario(mAuth.uid).addSnapshotListener {value, error ->
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