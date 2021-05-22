package com.aplicacion.mypet.models;

public class SliderItem {
    private String urlImagen;
    private Long timestamp;

    public SliderItem() {
    }

    public SliderItem(String urlImagen, Long timestamp) {
        this.urlImagen = urlImagen;
        this.timestamp = timestamp;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
