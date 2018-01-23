package com.example.guill.fhisa_admin.PreferencesCamion;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.guill.fhisa_admin.R;

/**
 * Created by guill on 30/10/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.camion_preferences);

       // Bundle extras = this.getArguments();
       // String id = extras.getString("id");

       // CheckBoxPreference checkbox = (CheckBoxPreference) findPreference("lpColorTrazo");
       // checkbox.setKey(id);

    }
}