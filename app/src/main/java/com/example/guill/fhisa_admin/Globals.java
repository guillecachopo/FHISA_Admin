package com.example.guill.fhisa_admin;

import android.app.Application;

/**
 * Created by guill on 07/11/2017.
 */

public class Globals extends Application {
    boolean ir;

    public boolean isIr() {
        return ir;
    }

    public void setIr(boolean ir) {
        this.ir = ir;
    }

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
