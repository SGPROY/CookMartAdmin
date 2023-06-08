package com.cookmartadmin.models;

public class modeloPedidos {
    private String estado, fecha, tipoPago, index, precio;

    public modeloPedidos(String estado, String fecha, String tipoPago, String index, String precio) {
        this.estado = estado;
        this.fecha = fecha;
        this.tipoPago = tipoPago;
        this.index = index;
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
