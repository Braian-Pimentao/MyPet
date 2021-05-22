package com.aplicacion.mypet.models;

import java.util.ArrayList;

public class Publicacion {
    private String idUser;
    private String id;
    private String nombre;
    private String edad;
    private String tipo;
    private String raza;
    private String descripcion;
    private String sexo;
    private ArrayList<String> imagenes;
    private long fechaPublicacion;


    public Publicacion() {

    }

    public Publicacion(String idUser, String id, String nombre, String edad, String tipo, String raza, String descripcion, String sexo, ArrayList<String> imagenes, long fechaPublicacion) {
        this.idUser = idUser;
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.tipo = tipo;
        this.raza = raza;
        this.descripcion = descripcion;
        this.sexo = sexo;
        this.imagenes = imagenes;
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public ArrayList<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(ArrayList<String> imagenes) {
        this.imagenes = imagenes;
    }

    public long getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(long fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}
