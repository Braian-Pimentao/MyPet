package com.aplicacion.mypet.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aplicacion.mypet.R;
import com.aplicacion.mypet.activities.publicar.ActivityCrearPublicacion;
import com.aplicacion.mypet.activities.sesion.IniciarSesion;
import com.aplicacion.mypet.fragments.FragmentChats;
import com.aplicacion.mypet.fragments.FragmentFavorito;
import com.aplicacion.mypet.fragments.FragmentHome;
import com.aplicacion.mypet.fragments.FragmentPerfil;
import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.TokenProvider;
import com.aplicacion.mypet.providers.UserProvider;
import com.aplicacion.mypet.utils.ViewedMessageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{
    private final int REQUEST_PERMISSION_UBICATION = 102;
    private BottomNavigationView bottomNavigation;
    private AuthProvider auth;
    private UserProvider userProvider;
    TokenProvider tokenProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MyPet);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_UBICATION);
        }
        bottomNavigation = findViewById(R.id.nav_view);

        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        auth = new AuthProvider();
        tokenProvider = new TokenProvider();
        userProvider = new UserProvider();

        openFragment(new FragmentHome());
        createToken();
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getAuth().getCurrentUser() != null) {
            ViewedMessageHelper.updateOnline(true, this);
        }
    }

    private void updateOnline(boolean estado) {
        ViewedMessageHelper.updateOnline(false, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (auth.getAuth().getCurrentUser() != null) {
            ViewedMessageHelper.updateOnline(false, this);
        }
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView mBottomNavigationView = findViewById(R.id.nav_view);
        if (mBottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
            super.onBackPressed();
            finish();
        }else {
            mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case  R.id.navigation_home:
                            openFragment(new FragmentHome());
                            return true;
                        case R.id.navigation_fav:
                            openFragment(new FragmentFavorito());
                            return true;
                        case R.id.navigation_mensajes:
                            openFragment(new FragmentChats());
                            return true;
                        case R.id.navigation_perfil:
                            openFragment(new FragmentPerfil());
                            return true;
                    }

                    return true;
                }
            };

    private void createToken() {
        if (auth.getAuth().getCurrentUser()!=null) {
            tokenProvider.create(auth.getUid());
        }
    }

    public void botonPublicar(View v){
        if (auth.getAuth().getCurrentUser()!=null) {
            System.out.println(auth.getEmail());
            System.out.println(auth.getNombreUsuario());
            Intent publicar = new Intent(this, ActivityCrearPublicacion.class);
            startActivity(publicar);
        } else {
            Intent items = new Intent(this, IniciarSesion.class);
            startActivity(items);
        }
    }

}

