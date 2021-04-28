package com.aplicacion.mypet.activities.sesion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class IniciarSesion extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "INFO_SING_IN";
    private TextInputEditText textInputEmail;
    private TextInputEditText textInputPassword;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        textInputEmail = findViewById(R.id.inicio_sesion_email);
        textInputPassword = findViewById(R.id.inicio_sesion_password);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        firestore = FirebaseFirestore.getInstance();
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
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(IniciarSesion.this, getString(R.string.inicio_correcto), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(IniciarSesion.this, getString(R.string.inicio_incorrecto), Toast.LENGTH_LONG).show();
                }
            }
        });
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
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            checkUserExist(auth.getCurrentUser().getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(IniciarSesion.this, getString(R.string.inicio_incorrecto_google), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void checkUserExist(String id) {
        firestore.collection("Users").document("id").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    String email = auth.getCurrentUser().getEmail();
                    String nombreUsuario = auth.getCurrentUser().getDisplayName();
                    Map<String,Object> datosUsuario = new HashMap<>();
                    datosUsuario.put("email", email);
                    datosUsuario.put("nombreUsuario",nombreUsuario);
                    firestore.collection("Users").document(id).set(datosUsuario);
                }

                Toast.makeText(IniciarSesion.this, getString(R.string.inicio_correcto_google), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}