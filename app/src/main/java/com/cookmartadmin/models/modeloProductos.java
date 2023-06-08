package com.cookmartadmin.models;

public class modeloProductos {
    private String imgMenor, imgMayor;
    private String titulo, descripcion, precio, unidad, idProducto, categoria;

    public modeloProductos(String categoria, String imgMenor, String imgMayor, String nombre, String descripcion, String precio, String unidad, String idProducto) {
        this.imgMenor = imgMenor;
        this.imgMayor = imgMayor;
        this.categoria = categoria;
        this.titulo = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.unidad = unidad;
        this.idProducto = idProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getImgMenor() {
        return imgMenor;
    }

    public String getImgMayor() {
        return imgMayor;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public String getUnidad() {
        return unidad;
    }

    public String getIdProducto() {
        return idProducto;
    }
}
