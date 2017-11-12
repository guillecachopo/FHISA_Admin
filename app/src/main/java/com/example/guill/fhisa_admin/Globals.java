package com.example.guill.fhisa_admin;

import android.app.Application;

/**
 * Created by guill on 07/11/2017.
 */

public class Globals extends Application {
    boolean ir;

    public boolean getIr(){
        return this.ir;
    }

    public boolean setIr(boolean ir){
        this.ir=ir;
        return ir;
    }

    boolean doubleBackToExitPressedOnce = false;

    public boolean isDoubleBackToExitPressedOnce() {
        return doubleBackToExitPressedOnce;
    }

    public void setDoubleBackToExitPressedOnce(boolean doubleBackToExitPressedOnce) {
        this.doubleBackToExitPressedOnce = doubleBackToExitPressedOnce;
    }
}