package com.example.guill.fhisa_admin.Objetos;

import java.util.ArrayList;

/**
 * Created by guill on 03/12/2017.
 */

public class ItvMantenimiento {
    String ITV;
    ArrayList<Mantenimiento> mantenimiento;

    public ItvMantenimiento(){

    }

    public ItvMantenimiento(String ITV, ArrayList<Mantenimiento> mantenimiento) {
        this.ITV = ITV;
        this.mantenimiento = mantenimiento;
    }

    public String getITV() {
        return ITV;
    }

    public void setITV(String ITV) {
        this.ITV = ITV;
    }

    public ArrayList<Mantenimiento> getMantenimiento() {
        return mantenimiento;
    }

    public void setMantenimiento(ArrayList<Mantenimiento> mantenimiento) {
        this.mantenimiento = mantenimiento;
    }
}
