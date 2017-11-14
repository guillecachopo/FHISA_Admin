package com.example.guill.fhisa_admin.Opciones;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.guill.fhisa_admin.MainActivity;
import com.example.guill.fhisa_admin.Preferences.CamionPreferences;
import com.example.guill.fhisa_admin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class OpcionesCamionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    ArrayList<String> posicionesString;
    ArrayList<String> horasString;
    String id;
    TextView tvCamionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_camion);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        tvCamionId = (TextView) findViewById(R.id.tvCamionOpcionesId);
        tvCamionId.setText("Camion: " + id);


    }

    public void irMapa(View view) {
        //Booleano en firebase que servirá para iniciar la activity con el mapa en la posición indicada
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference opcionesRef = database.getReference("opciones");
        opcionesRef.child(id).child("ir").setValue(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void irPosiciones(View view) {

        posicionesString = new ArrayList<>();
        horasString = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        posicionesString = (ArrayList<String>) getIntent().getSerializableExtra("posiciones");
        horasString = (ArrayList<String>) getIntent().getSerializableExtra("horas");

        Intent intent = new Intent(view.getContext(), DetallePosicionesCamion.class);
        intent.putExtra("id", id);
        intent.putStringArrayListExtra("posiciones", posicionesString);
        intent.putStringArrayListExtra("horas", horasString);
        startActivity(intent);
    }

    public void irConfiguracion(View view) {

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        //Intent intent = new Intent(view.getContext(), CamionPreferences.class);
        Intent intent = new Intent(view.getContext(), CamionPreferences.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void irVelocidadActual(View view) {

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        Intent intent = new Intent(view.getContext(), VelocidadActualActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);

    }

    public void irAlbaran(View view) {
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        Intent intent = new Intent(view.getContext(), AlbaranActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
    //Para volver al fragment anterior cuando hacemos click y no al activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}
