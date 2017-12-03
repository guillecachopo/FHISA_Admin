package com.example.guill.fhisa_admin.Objetos;

/**
 * Created by guill on 30/11/2017.
 */

public class Consumo {
    String id;
    String vehiculo;
    String articulo;
    String unidades;
    String consumo;
    String fecha;
    String  km;

    public Consumo() {
    }

    public Consumo(String id, String vehiculo, String articulo, String  unidades, String consumo, String fecha, String km) {
        this.id = id;
        this.vehiculo = vehiculo;
        this.articulo = articulo;
        this.unidades = unidades;
        this.consumo = consumo;
        this.fecha = fecha;
        this.km = km;
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

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getUnidades() {
        return unidades;
    }

    public void setUnidades(String unidades) {
        this.unidades = unidades;
    }

    public String getConsumo() {
        return consumo;
    }

    public void setConsumo(String consumo) {
        this.consumo = consumo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }
}
