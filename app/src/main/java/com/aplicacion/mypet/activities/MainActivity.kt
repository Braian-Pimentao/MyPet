package com.aplicacion.mypet.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.aplicacion.mypet.R
import com.aplicacion.mypet.activities.perfil.EditarPerfil
import com.aplicacion.mypet.activities.publicar.ActivityCrearPublicacion
import com.aplicacion.mypet.activities.sesion.IniciarSesion
import com.aplicacion.mypet.fragments.FragmentChats
import com.aplicacion.mypet.fragments.FragmentFavorito
import com.aplicacion.mypet.fragments.FragmentHome
import com.aplicacion.mypet.fragments.FragmentPerfil
import com.aplicacion.mypet.providers.AuthProvider
import com.aplicacion.mypet.providers.MensajeProvider
import com.aplicacion.mypet.providers.UserProvider
import com.aplicacion.mypet.utils.AppInfo
import com.aplicacion.mypet.utils.ViewedMessageHelper
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
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
    private lateinit var mSnackbar: Snackbar

    private lateinit var mLayoutInformativo: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyPet)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_PERMISSION_UBICATION)
        }
        mBottomNavigation = findViewById(R.id.nav_view)
        mBadge = mBottomNavigation.getOrCreateBadge(R.id.navigation_mensajes)
        mBadge.backgroundColor = getColor(R.color.secundario_app)
        mBadge.maxCharacterCount = 10
        mBadge.isVisible = false

        mAuth = AuthProvider()
        mUserProvider = UserProvider()
        mMensajeProvider = MensajeProvider()

        mLayoutInformativo = findViewById(R.id.mensaje_informativo_internet)

        if (isNetworkAvailable(this)) {
            mBottomNavigation.setOnItemSelectedListener(navigationItemSelectedListener)
            openFragment(FragmentHome())
        } else {
            mLayoutInformativo.visibility = View.VISIBLE
        }

        contadorMensajes()
    }

    private fun comprobarDatos() {
        if (isNetworkAvailable(this)) {
            mUserProvider.getUser(mAuth.uid).addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("ubicacion") || documentSnapshot.contains("urlPerfil")) {
                        if (documentSnapshot.get("ubicacion") == null || documentSnapshot.get("urlPerfil") == null) {
                            if (!AppInfo.AVISO_REALIZADO)
                                mostrarSnackBar()
                        }
                    }
                }
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw      = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    private fun mostrarSnackBar() {
        if (!AppInfo.AVISO_REALIZADO) {
            val rootView = window.decorView.findViewById<View>(R.id.main_activity)
            mSnackbar = Snackbar.make(this, rootView, getString(R.string.recomendacion), Snackbar.LENGTH_INDEFINITE)
            mSnackbar.anchorView = mBottomNavigation
            val layout = mSnackbar.view as SnackbarLayout
            val objLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val parentParams = layout.layoutParams as CoordinatorLayout.LayoutParams
            layout.setPadding(0, 0, 0, 0)
            layout.layoutParams = parentParams
            val snackView: View = layoutInflater.inflate(R.layout.snackbar, null)

            snackView.findViewById<TextView>(R.id.first_text_view).setOnClickListener {
                mSnackbar.dismiss()
            }

            snackView.findViewById<TextView>(R.id.second_text_view).setOnClickListener {
                val editarPerfil = Intent(this, EditarPerfil::class.java)
                startActivity(editarPerfil)
            }

            layout.addView(snackView, objLayoutParams)
            if (!mSnackbar.isShown) {
                AppInfo.aviso(true)
                mSnackbar.show()
            }
        }
        else
            mSnackbar.dismiss()
    }

    private var navigationItemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->
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
        if (isNetworkAvailable(this)) {
            if (mAuth.auth.currentUser != null) {
                val publicar = Intent(this, ActivityCrearPublicacion::class.java)
                startActivity(publicar)
            } else {
                val items = Intent(this, IniciarSesion::class.java)
                startActivity(items)
            }
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

    fun conectarInternet(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}