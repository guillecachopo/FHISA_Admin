package com.example.guill.fhisa_admin.Opciones;

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
import com.example.guill.fhisa_admin.Socket.PeticionLlamar;
import com.example.guill.fhisa_admin.Socket.PeticionVehiculo;

public class VehiculoActivity extends AppCompatActivity {

    public TextView tvAliasVehiculo, tvIdVehiculo, tvImeiVehiculo, tvMatriculaVehiculo, tvTlfVehiculo;
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        setToolbar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String imei = getImei();
        String alias = preferences.getString(imei+"-nombreCamion", imei);

        tvAliasVehiculo = (TextView) findViewById(R.id.tvAliasVehiculo);
        tvIdVehiculo = (TextView) findViewById(R.id.tvIdVehiculo);
        tvImeiVehiculo = (TextView) findViewById(R.id.tvImeiVehiculo);
        tvMatriculaVehiculo = (TextView) findViewById(R.id.tvMatriculaVehiculo);
        tvTlfVehiculo = (TextView) findViewById(R.id.tvTlfVehiculo);

        tvAliasVehiculo.setText(alias);

        PeticionVehiculo peticionVehiculo = new PeticionVehiculo(this);
        peticionVehiculo.execute(imei);



    }

    private String getImei() {
        Bundle extras = getIntent().getExtras();
        String imei = extras.getString("id");
        return imei;
    }

    private void setToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void llamarConductor(View view) {
        String imei = getImei();

        PeticionLlamar llamarTask = new PeticionLlamar(this);
        llamarTask.execute(imei);
    }

    public void cambiarAlias(final View view) {
        Bundle extras = getIntent().getExtras();
        final String imei = extras.getString("id");

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
                    cambiarAlias(view);
                } else if (introducido.equals("IMEI") || introducido.equals("imei")) {
                    Toast.makeText(getApplicationContext(), "Se ha reestablecido el IMEI como identificador", Toast.LENGTH_SHORT).show();
                    String identificador = imei;
                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = preferences.edit();
                    editor.putString(imei + "-nombreCamion", identificador);
                    editor.apply();
                } else {
                    String identificador = edt.getText().toString();
                    Toast.makeText(getApplicationContext(), "El identificador personalizado del camión es: " + identificador, Toast.LENGTH_SHORT).show();
                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = preferences.edit();
                    editor.putString(imei + "-nombreCamion", identificador);
                    editor.apply();
                    tvAliasVehiculo.setText(identificador);
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



    /**
     * Método empleado para volver al fragment anterior cuando se pulsa atrás
     * @param item
     * @return
     */
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
