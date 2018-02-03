package com.example.guill.fhisa_admin.Objetos;

import java.util.ArrayList;

/**
 * Created by guill on 18/09/2017.
 */

public class Camion {

    String id;
    Posicion posicion;
    ArrayList<Posicion> posiciones;
    ArrayList<Long> horas;
    long frecuencia_posiciones;
    long frecuencia_errores;

    public Camion() {
    }


    public Camion(String id, Posicion posicion){
        this.id = id;
        this.posiciones.add(posicion);
    }

    public Camion(String id, ArrayList<Posicion> posicionesList) {
        this.id = id;
        this.posiciones = posicionesList;
    }

    public Camion(String id, long frecuencia_errores) {
        this.id = id;
        this.frecuencia_errores = frecuencia_errores;
    }

    public Camion(Posicion posicion){
        this.posiciones.add(posicion);
    }


    public Camion(String id){
        this.id = id;
        posiciones = new ArrayList();
        horas = new ArrayList<>();
        }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Posicion getUltimaPosicion() {
        return posiciones.get(posiciones.size()-1);
    }

    public Posicion getPenultimaPosicion() {
        return posiciones.get(posiciones.size()-2);
    }

    public void setPosicion(Posicion posicion) {
        posiciones.add(posicion);
    }

    public void clearPosiciones() {
        posiciones.clear();
    }

    public ArrayList<Posicion> getPosicionesList() {
        return posiciones;
    }

    public void setPosicionesList(ArrayList<Posicion> posicionesList) {
        this.posiciones = posicionesList;
    }


    public long getFrecuenciaPosiciones() {
        return frecuencia_posiciones;
    }

    public void setFrecuenciaPosiciones(long frecuenciaPosiciones) {
        this.frecuencia_posiciones = frecuencia_posiciones;
    }

    public long getFrecuenciaErrores() {
        return frecuencia_errores;
    }

    public void setFrecuenciaErrores(long frecuencia_errores) {
        this.frecuencia_errores = frecuencia_errores;
    }

    //Lo usaremos más que nada en DetallePosicionesCamion para ver las horas de cada posicion

    public void setHoras(Long hora) {
        horas.add(hora);
    }

    public void clearHoras() { horas.clear(); }

    public ArrayList<Long> getHorasList() {
        return horas;
    }

    public void setHorasList (ArrayList<Long> horasList) {
        this.horas = horasList;
    }
}
