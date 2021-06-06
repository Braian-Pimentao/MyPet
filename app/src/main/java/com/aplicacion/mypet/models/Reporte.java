package com.aplicacion.mypet.models;

public class Reporte {
    private String idUser;
    private String idPublicacion;
    private String tipoReporte;

    public Reporte() {
    }

    public Reporte(String idUser, String idPublicacion, String tipoReporte) {
        this.idUser = idUser;
        this.idPublicacion = idPublicacion;
        this.tipoReporte = tipoReporte;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(String idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }
}
