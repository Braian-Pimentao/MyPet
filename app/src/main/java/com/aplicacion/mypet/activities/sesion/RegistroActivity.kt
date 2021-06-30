package com.aplicacion.mypet.activities.sesion

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aplicacion.mypet.R
import com.aplicacion.mypet.models.User
import com.aplicacion.mypet.providers.AuthProvider
import com.aplicacion.mypet.providers.UserProvider
import com.google.android.material.textfield.TextInputEditText
import dmax.dialog.SpotsDialog
import java.util.regex.Pattern

class RegistroActivity : AppCompatActivity() {
    private lateinit var textNombre: TextInputEditText
    private lateinit var textEmail: TextInputEditText
    private lateinit var textPassword: TextInputEditText
    private lateinit var textConfirmPassword: TextInputEditText
    private lateinit var auth: AuthProvider
    private lateinit var userProvider: UserProvider
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        textNombre = findViewById(R.id.campo_nombre)
        textEmail = findViewById(R.id.campo_email)
        textPassword = findViewById(R.id.campo_password)
        textConfirmPassword = findViewById(R.id.campo_confirmar_password)

        auth = AuthProvider()
        userProvider = UserProvider()

        dialog = SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.cargando_registro)
                .setCancelable(false).build()

    }

    fun irAtras(view: View?) {
        finish()
    }

    fun registrarUsuario(view: View?) {
        val nombreUsuario = textNombre.text.toString()
        val email = textEmail.text.toString()
        val password = textPassword.text.toString()
        val confirmPassword = textConfirmPassword.text.toString()
        if (nombreUsuario.isNotEmpty()
                && email.isNotEmpty()
                && password.isNotEmpty()
                && confirmPassword.isNotEmpty()) {
            if (isEmailValid(email)) {
                if (password == confirmPassword) {
                    if (password.length >= 6) {
                        createUser(nombreUsuario, email, password)
                    } else {
                        Toast.makeText(this, getString(R.string.password_corta), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.password_diferente), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.email_no_valido), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.campos_vacios), Toast.LENGTH_LONG).show()
        }
    }

    private fun createUser(nombreUsuario: String, email: String, password: String) {
        dialog.show()
        auth.registerUser(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                addDataUser(email, nombreUsuario)
            } else {
                Toast.makeText(this@RegistroActivity, getString(R.string.register_incomplete), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addDataUser(email: String, nombreUsuario: String) {
        val id = auth.uid
        val user = User()
        user.id = id
        user.email = email
        user.username = nombreUsuario
        userProvider.create(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                dialog.dismiss()
                Toast.makeText(this@RegistroActivity, getString(R.string.register_complete), Toast.LENGTH_LONG).show()
                finish()
            } else Toast.makeText(this@RegistroActivity, getString(R.string.register_incomplete), Toast.LENGTH_LONG).show()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

}