package com.cookmartadmin.models;

public class modeloEditarDetalles {
    private String imagen, titulo, unidad, cantidad, precio;

    public modeloEditarDetalles(String imagen, String titulo, String unidad, String cantidad, String precio) {
        this.imagen = imagen;
        this.titulo = titulo;
        this.unidad = unidad;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrice() {
        return precio;
    }

    public void setPrice(String precio) {
        this.precio = precio;
    }
}
