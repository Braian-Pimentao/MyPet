package com.aplicacion.mypet.activities.sesion;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {
    private TextInputEditText textNombre;
    private TextInputEditText textEmail;
    private TextInputEditText textPassword;
    private TextInputEditText textConfirmPassword;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        textNombre = findViewById(R.id.campo_nombre);
        textEmail = findViewById(R.id.campo_email);
        textPassword = findViewById(R.id.campo_password);
        textConfirmPassword = findViewById(R.id.campo_confirmar_password);

        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
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
                        createUser(nombreUsuario,email,password);
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

    private void createUser(final String nombreUsuario,final String email, String password) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    addDataUser(email,nombreUsuario);
                }else{
                    Toast.makeText(RegistroActivity.this, getString(R.string.register_incomplete), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addDataUser(String email,String nombreUsuario){
        String id = auth.getCurrentUser().getUid();
        Map<String,Object> datosUsuario = new HashMap<>();
        datosUsuario.put("email",email);
        datosUsuario.put("nombreUsuario",nombreUsuario);
        firestore.collection("Users").document(id).set(datosUsuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RegistroActivity.this, getString(R.string.register_complete), Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                    Toast.makeText(RegistroActivity.this, getString(R.string.register_incomplete), Toast.LENGTH_LONG).show();
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