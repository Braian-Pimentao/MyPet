package com.aplicacion.mypet.models;

public class Favorito {
    private String id;
    private String idPublicacion;
    private String idUser;
    private long timestamp;

    public Favorito() {
    }

    public Favorito(String id, String idPublicacion, String idUser, long timestamp) {
        this.id = id;
        this.idPublicacion = idPublicacion;
        this.idUser = idUser;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(String idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
