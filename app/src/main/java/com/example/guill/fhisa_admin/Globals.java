package com.example.guill.fhisa_admin;

import android.app.Application;

/**
 * Created by guill on 07/11/2017.
 */

public class Globals extends Application {

    //PARA IR A UN CAMION EN PARTICULAR CUANDO SE BUSCA EN EL MAPA
    boolean ir;

    public boolean isIr() {
        return ir;
    }

    public void setIr(boolean ir) {
        this.ir = ir;
    }

    //
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    long numCamiones;

    public long getNumCamiones() {
        return numCamiones;
    }

    public void setNumCamiones(long numCamiones) {
        this.numCamiones = numCamiones;
    }

}
