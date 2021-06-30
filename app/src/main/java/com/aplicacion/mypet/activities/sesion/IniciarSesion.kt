package com.aplicacion.mypet.activities.sesion

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.aplicacion.mypet.R
import com.aplicacion.mypet.User
import com.aplicacion.mypet.providers.AuthProvider
import com.aplicacion.mypet.providers.TokenProvider
import com.aplicacion.mypet.providers.UserProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import dmax.dialog.SpotsDialog

class IniciarSesion : AppCompatActivity() {
    private val TAG = "INFO_SING_IN"
    private lateinit var mTextInputEmail: TextInputEditText
    private lateinit var mTextInputPassword: TextInputEditText
    private lateinit var mAuth: AuthProvider
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mUserProvider: UserProvider
    private lateinit var mDialog: AlertDialog
    private lateinit var mTokenProvider: TokenProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        mTextInputEmail = findViewById(R.id.inicio_sesion_email)
        mTextInputPassword = findViewById(R.id.inicio_sesion_password)

        mAuth = AuthProvider()
        mTokenProvider = TokenProvider()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mUserProvider = UserProvider()
        mDialog = SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.cargando_iniciar)
                .setCancelable(false).build()
    }

    fun registrarUsuario(view: View?) {
        val registro = Intent(this, RegistroActivity::class.java)
        startActivity(registro)
    }

    fun pulsarIniciarSesion(view: View) {
        val botonPulsado = view as Button
        if (botonPulsado == findViewById(R.id.boton_inicio_sesion))
            loginNormal()
        else if (botonPulsado == findViewById(R.id.boton_inicio_sesion_google))
            signInGoogle()
    }

    private fun loginNormal() {
        val email: String = mTextInputEmail.getText().toString()
        val password: String = mTextInputPassword.getText().toString()
        if (!email.isEmpty() && !password.isEmpty()) {
            mDialog.show()
            mAuth.login(email, password).addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                mDialog.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(this@IniciarSesion, getString(R.string.inicio_correcto), Toast.LENGTH_LONG).show()
                    mTokenProvider.create(mAuth.uid)
                    finish()
                } else {
                    Toast.makeText(this@IniciarSesion, getString(R.string.inicio_incorrecto), Toast.LENGTH_LONG).show()
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.campos_vacios), Toast.LENGTH_SHORT).show()
        }
    }


    var resultGoogle = registerForActivityResult(
            StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle:" + account!!.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(this@IniciarSesion.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        resultGoogle.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        mDialog.show()
        mAuth.loginGoogle(idToken)
                .addOnCompleteListener(this) { task ->
                    mDialog.dismiss()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        checkUserExist(mAuth.uid)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@IniciarSesion, getString(R.string.inicio_incorrecto_google), Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun checkUserExist(id: String) {
        mUserProvider.getUser(id).addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val email = mAuth.email
                val nombreUsuario = mAuth.nombreUsuario
                val user = User()
                user.id = id
                user.email = email
                user.username = nombreUsuario
                mUserProvider.create(user)
            }
            Toast.makeText(this@IniciarSesion, getString(R.string.inicio_correcto_google), Toast.LENGTH_LONG).show()
            mTokenProvider.create(mAuth.uid)
            finish()
        }
    }

    fun cerrarIniciarSesion(view: View?) {
        finish()
    }
}