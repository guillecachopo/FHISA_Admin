package com.example.guill.fhisa_admin.OpcionesCamion;

import android.content.DialogInterface;
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

import com.example.guill.fhisa_admin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FrecuenciasEnvioActivity extends AppCompatActivity {

    private Toolbar toolbar;
    String imei;

    /**
     * Base de datos Firebase a utilizar
     */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Preferencias compartidas
     */
    SharedPreferences preferences;

    /**
     * Editor para escribir en las preferencias compartidas
     */
    SharedPreferences.Editor editor;

    /**
     * Referencia de las frecuencias en Firebase
     */
    final DatabaseReference frecuenciasRef = database.getReference("frecuencias");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frecuencias_envio);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        imei = extras.getString("id");
        TextView tvCamionId = (TextView) findViewById(R.id.tvImeiFrecuencias);
        tvCamionId.setText("Camion: " + imei);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String frecuenciaEnvioPosiciones = preferences.getString(imei + "-frecuenciaPosiciones", "1");
        TextView tvFrecuenciaEnvioPosiciones = (TextView) findViewById(R.id.tvFrecuenciaActualPosiciones);
        tvFrecuenciaEnvioPosiciones.setText(frecuenciaEnvioPosiciones);

        String frecuenciaEnvioErrores = preferences.getString(imei + "-frecuenciaErrores", "10");
        TextView tvFrecuenciaEnvioErrores = (TextView) findViewById(R.id.tvFrecuenciaActualErrores);
        tvFrecuenciaEnvioErrores.setText(frecuenciaEnvioErrores);


    }

    public void frecuenciaPosiciones(final View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_frecuencia_posiciones, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etAlertDialogFrecuenciaPosiciones);
        dialogBuilder.setTitle("Frecuencia de envío de posiciones");
        dialogBuilder.setMessage("Nueva frecuencia de envío (en minutos)");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                String introducido = edt.getText().toString();
                if (introducido.equals("")) {
                    Toast.makeText(getApplicationContext(), "No se ha introducido un valor válido", Toast.LENGTH_SHORT).show();
                    frecuenciaPosiciones(view);
                } else {
                    //Cambiar frecuencia de envios
                    String frecuenciaEnvio = edt.getText().toString();
                    Toast.makeText(getApplicationContext(), "La frecuencia de envío actual será de " + frecuenciaEnvio + " minutos.", Toast.LENGTH_SHORT).show();
                    frecuenciasRef.child(imei).child("posiciones").setValue(frecuenciaEnvio);

                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = preferences.edit();
                    editor.putString(imei + "-frecuenciaPosiciones", frecuenciaEnvio);
                    editor.apply();

                    TextView tvFrecuenciaEnvioPosiciones = (TextView) findViewById(R.id.tvFrecuenciaActualPosiciones);
                    tvFrecuenciaEnvioPosiciones.setText(frecuenciaEnvio);
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

    public void frecuenciaErrores(final View view) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_frecuencia_errores, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etAlertDialogFrecuenciaErrores);
        dialogBuilder.setTitle("Frecuencia de envío de notificaciones de error");
        dialogBuilder.setMessage("Nueva frecuencia de envío (en minutos)");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                String introducido = edt.getText().toString();
                if (introducido.equals("")) {
                    Toast.makeText(getApplicationContext(), "No se ha introducido un valor válido", Toast.LENGTH_SHORT).show();
                    frecuenciaErrores(view);
                } else {
                    //Cambiar frecuencia de envios
                    String frecuenciaEnvio = edt.getText().toString();
                    Toast.makeText(getApplicationContext(), "La frecuencia de envío actual será de " + frecuenciaEnvio + " minutos.", Toast.LENGTH_SHORT).show();
                    frecuenciasRef.child(imei).child("notificaciones").setValue(frecuenciaEnvio);

                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = preferences.edit();
                    editor.putString(imei + "-frecuenciaErrores", frecuenciaEnvio);
                    editor.apply();

                    TextView tvFrecuenciaEnvioErrores = (TextView) findViewById(R.id.tvFrecuenciaActualErrores);
                    tvFrecuenciaEnvioErrores.setText(frecuenciaEnvio);
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
