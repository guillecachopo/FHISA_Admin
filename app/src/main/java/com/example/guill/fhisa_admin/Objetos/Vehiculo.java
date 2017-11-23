package com.example.guill.fhisa_admin.Objetos;

/**
 * Created by guill on 22/11/2017.
 */

public class Vehiculo {
    String id;
    String imei;
    String num_tlf;
    String name;

    public Vehiculo() {
    }

    public Vehiculo(String id, String imei, String num_tlf, String name) {
        this.id = id;
        this.imei = imei;
        this.num_tlf = num_tlf;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTlf() {
        return num_tlf;
    }

    public void setTlf(String num_tlf) {
        this.num_tlf = num_tlf;
    }

    public String getMatricula() {
        return name;
    }

    public void setMatricula(String name) {
        this.name = name;
    }
}
