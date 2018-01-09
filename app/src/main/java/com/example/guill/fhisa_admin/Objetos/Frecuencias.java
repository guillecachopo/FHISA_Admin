package com.example.guill.fhisa_admin.Objetos;

/**
 * Created by guill on 29/12/2017.
 */

public class Frecuencias {
    String posiciones;
    String notificaciones;

    public Frecuencias() {

    }

    public Frecuencias(String posiciones) {
        this.posiciones = posiciones;
    }

    public Frecuencias(String posiciones, String notificaciones) {
        this.posiciones = posiciones;
        this.notificaciones = notificaciones;
    }

    public String getPosiciones() {
        return posiciones;
    }

    public void setPosiciones(String posiciones) {
        this.posiciones = posiciones;
    }

    public String getNotificaciones() {
        return notificaciones;
    }

    public void setNotificaciones(String notificaciones) {
        this.notificaciones = notificaciones;
    }
}
