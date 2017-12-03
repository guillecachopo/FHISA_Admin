package com.example.guill.fhisa_admin.Objetos;

/**
 * Created by guill on 03/12/2017.
 */
public class Mantenimiento {
    String id;
    String vehiculo;
    String operacion;
    String ciclo;

    public Mantenimiento(){

    }

    public Mantenimiento(String id, String vehiculo, String operacion, String ciclo) {
        this.id = id;
        this.vehiculo = vehiculo;
        this.operacion = operacion;
        this.ciclo = ciclo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
}
