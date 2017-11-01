package com.example.guill.fhisa_admin.Preferences;

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
        //getFragmentManager().beginTransaction()
         //       .replace(android.R.id.content, new SettingsFragment()).commit();

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment()).commit();

        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");

       // SharedPreferences sharedPref = this.getSharedPreferences(id, 0);
       // SharedPreferences.Editor editor = sharedPref.edit();

    }
}
