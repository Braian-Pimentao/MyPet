package com.aplicacion.mypet.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthProvider {
    private FirebaseAuth auth;

    public AuthProvider() {
        auth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email,password);
    }

    public Task<AuthResult> loginGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return auth.signInWithCredential(credential);
    }

    public String getUid() {
        if (auth.getCurrentUser()!=null)
            return  auth.getCurrentUser().getUid();
        else
            return null;
    }

    public String getEmail() {
        if (auth.getCurrentUser()!=null)
            return  auth.getCurrentUser().getEmail();
        else
            return null;
    }

    public String getNombreUsuario() {
        if (auth.getCurrentUser()!=null)
            return  auth.getCurrentUser().getDisplayName();
        else
            return null;
    }

    public Task<AuthResult> registerUser(String email, String password){
        return auth.createUserWithEmailAndPassword(email,password);
    }
}
