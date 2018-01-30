package com.example.guill.fhisa_admin.Objetos;

import com.google.android.gms.maps.model.CircleOptions;

/**
 * Created by guill on 01/11/2017.
 */

public class BaseOperativa {
    String identificador;
    double latitud;
    double longitud;
    int distancia;
    CircleOptions circle;


    public BaseOperativa() {
    }

    public BaseOperativa(String identificador, double latitud, double longitud, int distancia, CircleOptions circle) {
        this.identificador = identificador;
        this.latitud = latitud;
        this.longitud = longitud;
        this.distancia = distancia;
        this.circle = circle;
    }

    public BaseOperativa(String identificador, double latitud, double longitud, int distancia) {
        this.identificador = identificador;
        this.latitud = latitud;
        this.longitud = longitud;
        this.distancia = distancia;
    }

    public BaseOperativa(double latitud, double longitud, int distancia) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.distancia = distancia;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public CircleOptions getCircleOptions() {
        return circle;
    }

    public void setCircleOptions(CircleOptions circle) {
        this.circle = circle;
    }
}
