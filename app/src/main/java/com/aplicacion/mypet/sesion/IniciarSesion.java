package com.aplicacion.mypet.sesion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aplicacion.mypet.R;
import com.google.android.material.textfield.TextInputEditText;

public class IniciarSesion extends AppCompatActivity {
    TextInputEditText textInputEmail;
    TextInputEditText textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        textInputEmail = findViewById(R.id.inicio_sesion_email);
        textInputPassword = findViewById(R.id.inicio_sesion_password);

    }

    public void registrarUsuario(View view) {
        Intent registro = new Intent(this, RegistroActivity.class);
        startActivity(registro);
    }

    public void pulsarIniciarSesion(View view) {
        loginNormal();
    }

    private void loginNormal(){
        String email = textInputEmail.getText().toString();
        String password = textInputPassword.getText().toString();

        Log.d("CAMPO/EMAIL: ",email);
        Log.d("CAMPO/PASS: ",password);
    }
}