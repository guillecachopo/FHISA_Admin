package com.example.guill.fhisa_admin.OpcionesCamion;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.R;
import com.example.guill.fhisa_admin.Socket.PeticionLlamar;
import com.example.guill.fhisa_admin.Socket.PeticionVehiculo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetallesVehiculoActivity extends AppCompatActivity {

    /**
     * Declaración de TextViews de la actividad
    */
    public TextView tvAliasVehiculo, tvIdVehiculo, tvImeiVehiculo, tvMatriculaVehiculo, tvTlfVehiculo,
    tvBateriaVehiculo;

    /**
     * Declaración de las preferencias compartidas de la aplicación
     */
    public SharedPreferences preferences;
    /**
     * Declaración del editor de las preferencias compartidas de la aplicación
     */
    public SharedPreferences.Editor editor;
    /**
     * Declaración de la Toolbar de la aplicación
     */
    private Toolbar toolbar;

    public ProgressBar progressBar;

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
        tvBateriaVehiculo = (TextView) findViewById(R.id.tvBateriaVehiculo);

        tvAliasVehiculo.setText(alias);
        getBateria(imei);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        PeticionVehiculo peticionVehiculo = new PeticionVehiculo(this, progressBar);
        peticionVehiculo.execute(imei);
    }

    /**
     * Retorna el porcentaje de batería del teléfono
     * @param imei
     */
    private void getBateria(String imei) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);
        camionesRef.child(imei).child("bateria").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long bateria = (long) dataSnapshot.getValue();
                    tvBateriaVehiculo.setText(bateria + "%");
                } else {
                    tvBateriaVehiculo.setText("No hay información de batería.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Método para obtener el imei del camión obtenido desde otra Activity
     * @return imei
     */
    private String getImei() {
        Bundle extras = getIntent().getExtras();
        String imei = extras.getString("imei");
        return imei;
    }

    /**
     * Método para activar la toolbar
     * @param toolbar
     */
    private void setToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Método que llama a la clase PeticionLlamar para llamar al número de teléfono
    */
    public void llamarConductor(View view) {
        String imei = getImei();

        PeticionLlamar llamarTask = new PeticionLlamar(this);
        llamarTask.execute(imei);
    }

    /**
     * Método para cambiar el alias personalizado del camión
     */
    public void cambiarAlias(final View view) {
        Bundle extras = getIntent().getExtras();
        final String imei = extras.getString("imei");

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_nombre_camion, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etAlertDialogCamionNombre);

        dialogBuilder.setTitle("Selección de Alias");
        dialogBuilder.setMessage("Nuevo alias personalizado del camión: ");
        dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String introducido = edt.getText().toString();
                if (introducido.equals("")) {
                    Toast.makeText(getApplicationContext(), "No se ha introducido un valor válido", Toast.LENGTH_SHORT).show();
                    cambiarAlias(view);
                } else if (introducido.equals("IMEI") || introducido.equals("imei")) {
                    Toast.makeText(getApplicationContext(), "Se ha reestablecido el IMEI como alias", Toast.LENGTH_SHORT).show();
                    String identificador = imei;
                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = preferences.edit();
                    editor.putString(imei + "-nombreCamion", identificador);
                    editor.apply();
                } else {
                    String identificador = edt.getText().toString();
                    Toast.makeText(getApplicationContext(), "El alias personalizado del camión es: " + identificador, Toast.LENGTH_SHORT).show();
                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = preferences.edit();
                    editor.putString(imei + "-nombreCamion", identificador);
                    editor.apply();
                    tvAliasVehiculo.setText(identificador);
                }


            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();


    }

    public void esId(View view) {
        Toast.makeText(this, "ID FHISA", Toast.LENGTH_SHORT).show();
    }

    public void esImei(View view) {
        Toast.makeText(this, "IMEI", Toast.LENGTH_SHORT).show();
    }

    public void esMatricula(View view) {
        Toast.makeText(this, "MATRICULA", Toast.LENGTH_SHORT).show();
    }

    public void esBateria(View view) {
        Toast.makeText(this, "BATERÍA", Toast.LENGTH_SHORT).show();
    }

    public void irOpcionesCamion(View view) {
        Intent intent = new Intent(this, OpcionesCamionActivity.class);
        intent.putExtra("imei", getImei());
        startActivity(intent);
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
