package com.example.guill.fhisa_admin.Preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.guill.fhisa_admin.R;

/**
 * Created by guill on 30/10/2017.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.camion_preferences);
    }
}