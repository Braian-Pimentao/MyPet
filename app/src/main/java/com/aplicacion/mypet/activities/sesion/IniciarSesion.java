package com.aplicacion.mypet.activities.sesion;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.models.User;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import dmax.dialog.SpotsDialog;

public class IniciarSesion extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "INFO_SING_IN";
    private TextInputEditText textInputEmail;
    private TextInputEditText textInputPassword;
    private AuthProvider auth;
    private GoogleSignInClient googleSignInClient;
    private UserProvider userProvider;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        textInputEmail = findViewById(R.id.inicio_sesion_email);
        textInputPassword = findViewById(R.id.inicio_sesion_password);

        auth = new AuthProvider();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        userProvider = new UserProvider();
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.cargando_iniciar)
                .setCancelable(false).build();
    }

    public void registrarUsuario(View view) {
        Intent registro = new Intent(this, RegistroActivity.class);
        startActivity(registro);
    }

    public void pulsarIniciarSesion(View view) {
        Button botonPulsado = (Button) view;

        if (botonPulsado.equals(findViewById(R.id.boton_inicio_sesion)))
            loginNormal();
        else if (botonPulsado.equals(findViewById(R.id.boton_inicio_sesion_google)))
            signInGoogle();
    }

    private void loginNormal(){
        String email = textInputEmail.getText().toString();
        String password = textInputPassword.getText().toString();
        if(!email.isEmpty() && !password.isEmpty()) {
            dialog.show();
            auth.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(IniciarSesion.this, getString(R.string.inicio_correcto), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(IniciarSesion.this, getString(R.string.inicio_incorrecto), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.campos_vacios), Toast.LENGTH_SHORT).show();
        }
    }

    private void signInGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        dialog.show();
        auth.loginGoogle(idToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            checkUserExist(auth.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(IniciarSesion.this, getString(R.string.inicio_incorrecto_google), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void checkUserExist(String id) {
        userProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    String email = auth.getEmail();
                    String nombreUsuario = auth.getNombreUsuario();

                    User user= new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUsername(nombreUsuario);

                    userProvider.create(user);
                }

                Toast.makeText(IniciarSesion.this, getString(R.string.inicio_correcto_google), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}