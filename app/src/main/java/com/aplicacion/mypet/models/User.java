package com.aplicacion.mypet.models;

import java.util.ArrayList;

public class User {
    private String id;
    private  String email;
    private String username;
    private ArrayList<Double> ubicacion;
    private String urlPerfil;
    private boolean ocultarUbicacion;
    private boolean online;
    private long ultimaConexion;

    public User() {
    }

    public User(String id, String email, String username, ArrayList<Double> ubicacion, String urlPerfil, boolean ocultarUbicacion, boolean online, long ultimaConexion) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.ubicacion = ubicacion;
        this.urlPerfil = urlPerfil;
        this.ocultarUbicacion = ocultarUbicacion;
        this.online = online;
        this.ultimaConexion = ultimaConexion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Double> getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(ArrayList<Double> ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
    }

    public boolean isOcultarUbicacion() {
        return ocultarUbicacion;
    }

    public void setOcultarUbicacion(boolean ocultarUbicacion) {
        this.ocultarUbicacion = ocultarUbicacion;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getUltimaConexion() {
        return ultimaConexion;
    }

    public void setUltimaConexion(long ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }
}
