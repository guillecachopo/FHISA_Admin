package com.example.guill.fhisa_admin.Objetos;

/**
 * Created by guill on 21/11/2017.
 */


public class ErrorNotificacion {
    String imei;
    long diferencia;
    long horaActual;
    long horaPosicion;

    public ErrorNotificacion() {
    }

    /**
     * Constructor ErrorNotificacion
     * @param imei
     * @param diferencia
     * @param horaActual
     */
    public ErrorNotificacion(String imei, long diferencia, long horaActual) {
        this.imei = imei;
        this.diferencia = diferencia;
        this.horaActual = horaActual;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public long getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(long diferencia) {
        this.diferencia = diferencia;
    }

    public long getHoraActual() {
        return horaActual;
    }

    public void setHoraActual(long horaActual) {
        this.horaActual = horaActual;
    }

    public void setHoraPosicion (long horaPosicion ) { this.horaPosicion = (horaActual - diferencia); }

    public long getHoraPosicion() { return (horaActual - diferencia); }
}
