package com.example.guill.fhisa_admin.Opciones;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Globals;
import com.example.guill.fhisa_admin.MainActivity;
import com.example.guill.fhisa_admin.Preferences.CamionPreferences;
import com.example.guill.fhisa_admin.R;

import java.util.ArrayList;

public class OpcionesCamionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    ArrayList<String> posicionesString;
    ArrayList<String> horasString;
    String id;
    TextView tvCamionId;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


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
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        Globals globals = (Globals) getApplicationContext();
        globals.setId(id);
        globals.setIr(true);

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

    public void irCamionNombre(final View view){
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_nombre_camion, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etAlertDialogCamionNombre);

        dialogBuilder.setTitle("Selección de id");
        dialogBuilder.setMessage("Nuevo identificador del camión: ");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                String introducido = edt.getText().toString();
                if (introducido.equals("")) {
                    Toast.makeText(getApplicationContext(), "No se ha introducido un valor válido", Toast.LENGTH_SHORT).show();
                    irCamionNombre(view);
                } else if (introducido.equals("IMEI") || introducido.equals("imei")) {
                    Toast.makeText(getApplicationContext(), "Se ha reestablecido el IMEI como identificador", Toast.LENGTH_SHORT).show();
                    String identificador = id;
                    pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = pref.edit();
                    editor.putString(id+"-nombreCamion", identificador);
                    editor.apply();
                } else {
                    String identificador = edt.getText().toString();
                    Toast.makeText(getApplicationContext(), "El identificador personalizado del camión es: " +identificador , Toast.LENGTH_SHORT).show();
                    pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = pref.edit();
                    editor.putString(id+"-nombreCamion", identificador);
                    editor.apply();
                }


            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
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
