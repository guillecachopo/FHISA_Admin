package com.example.guill.fhisa_admin;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by guill on 12/12/2017.
 */

public class OpcionesMenuFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.opciones);
    }

}
