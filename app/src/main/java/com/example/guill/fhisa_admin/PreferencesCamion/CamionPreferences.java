package com.example.guill.fhisa_admin.PreferencesCamion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.guill.fhisa_admin.R;

public class CamionPreferences extends AppCompatActivity {

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_camion);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Mostramos el contenido de la pantalla de preferencias.
        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");

        /*
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setArguments(bundle); */

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment()).commit();


       // SharedPreferences sharedPref = this.getSharedPreferences(id, 0);
       // SharedPreferences.Editor editor = sharedPref.edit();

    }

}
