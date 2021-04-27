package com.aplicacion.mypet.sesion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aplicacion.mypet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {
    private TextInputEditText textNombre;
    private TextInputEditText textEmail;
    private TextInputEditText textPassword;
    private TextInputEditText textConfirmPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        textNombre = findViewById(R.id.campo_nombre);
        textEmail = findViewById(R.id.campo_email);
        textPassword = findViewById(R.id.campo_password);
        textConfirmPassword = findViewById(R.id.campo_confirmar_password);

        auth=FirebaseAuth.getInstance();
    }

    public void irAtras(View view) {
        finish();
    }

    public void registrarUsuario(View view) {
        String nombreUsuario = textNombre.getText().toString();
        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();
        String confirmPassword = textConfirmPassword.getText().toString();

        if(!nombreUsuario.isEmpty()
                && !email.isEmpty()
                && !password.isEmpty()
                && !confirmPassword.isEmpty()) {
            if (isEmailValid(email)) {
                if (password.equals(confirmPassword)) {
                    if (password.length()>=6){
                        createUser(email,password);
                    } else {
                    Toast.makeText(this, getString(R.string.password_corta), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.password_diferente), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.email_no_valido), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.campos_vacios), Toast.LENGTH_LONG).show();
        }
    }

    private void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegistroActivity.this, getString(R.string.register_complete), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(RegistroActivity.this, getString(R.string.register_incomplete), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}