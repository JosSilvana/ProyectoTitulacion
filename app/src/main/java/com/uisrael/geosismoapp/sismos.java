package com.uisrael.geosismoapp;

public class sismos {

    //Declaracion de variables
    private String keySismo;
    private String idEvento;
    private String horaLocal;
    private String magnitud;
    private String ciudadMasCercana;
    private String profundidad;

    //Constructor vacio
    public sismos(){ }

    //Constructor con parametros
    public sismos(String keySismo,String idEvento, String horaLocal, String magnitud, String ciudadMasCercana, String profundidad){
        this.keySismo = keySismo;
        this.idEvento = idEvento;
        this.horaLocal = horaLocal;
        this.magnitud = magnitud;
        this.ciudadMasCercana = ciudadMasCercana;
        this.profundidad = profundidad;
    }

    //Getters

    public String getKeySismo() {
        return keySismo;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getHoraLocal() {
        return horaLocal;
    }

    public String getMagnitud() {
        return magnitud;
    }

    public String getCiudadMasCercana() {
        return ciudadMasCercana;
    }

    public String getProfundidad() {
        return profundidad;
    }
}
