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
        if (id==null || id.isEmpty() || id.length() == 0) id = "No registrado";
        return id;
    }

    public void setId(String id) {
        if (id==null || id.isEmpty()) id = "No registrado";
        else this.id = id;
    }

    public String getImei() {
        if (imei==null || imei.equals("")) imei = "No registrado";
        return imei;
    }

    public void setImei(String imei) {
        if (imei==null || imei.equals("")) imei = "No registrado";
        else this.imei = imei;
    }

    public String getTlf() {
        if (num_tlf==null || num_tlf.equals("")) num_tlf = "No registrado";
        return num_tlf;
    }

    public void setTlf(String num_tlf) {
        if (num_tlf==null || num_tlf.equals("")) num_tlf = "No registrado";
        else this.num_tlf = num_tlf;
    }

    public String getMatricula() {
        if (name==null || name.equals("")) name = "No registrada";
        return name;
    }

    public void setMatricula(String name) {
        if (name==null || name.equals("")) name = "No registrada";
        else this.name = name;
    }
}
