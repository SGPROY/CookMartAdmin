package com.cookmartadmin.models;

public class modeloCodPostal {
    private String codPostal, efectivoActivo, cargoEnvio;

    public modeloCodPostal(String codPostal, String efectivoActivo, String cargoEnvio) {
        this.codPostal = codPostal;
        this.efectivoActivo = efectivoActivo;
        this.cargoEnvio = cargoEnvio;
    }

    public String getcodPostal() {
        return codPostal;
    }

    public String getefectivoActivo() {
        return efectivoActivo;
    }

    public String getcargoEnvio() {
        return cargoEnvio;
    }
}
