package com.example.guill.fhisa_admin.Opciones;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Globals;
import com.example.guill.fhisa_admin.MainActivity;
import com.example.guill.fhisa_admin.R;
import com.example.guill.fhisa_admin.Socket.PeticionLlamar;

import java.util.ArrayList;

public class OpcionesCamionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    ArrayList<String> posicionesString;
    ArrayList<String> horasString;
    String id;
    TextView tvCamionId;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final CharSequence[] COLORES_ITEMS =
            {"Blanco", "Verde", "Azul", "Amarillo", "Negro", "Gris", "Cyan", "Rojo", "Gris oscuro", "Gris claro", "Morado"};

    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_camion);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        tvCamionId = (TextView) findViewById(R.id.tvCamionOpcionesId);
        tvCamionId.setText("Camion: " + id);


    }

    /*
    public void verDatosCamion(View view) {
        Bundle extras = getIntent().getExtras();
        String imei = extras.getString("id");
        Intent intent = new Intent(this, VehiculoActivity.class);
        intent.putExtra("id", imei);
        startActivity(intent);
    } */

    public void llamarConductor(View view) {
        Bundle extras = getIntent().getExtras();
        String imei = extras.getString("id");

        PeticionLlamar llamarTask = new PeticionLlamar(this);
        llamarTask.execute(imei);
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

/*
        //Intent intent = new Intent(view.getContext(), CamionPreferences.class);
        Intent intent = new Intent(view.getContext(), CamionPreferences.class);
        intent.putExtra("id", id);
        startActivity(intent); */


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = pref.edit();
        final String fDialogTitle = "Seleccione un color";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);
        builder.create();
        builder.setSingleChoiceItems(
                COLORES_ITEMS,
                11,
                new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {

                            case 0:
                                Log.i("Color", id);
                                editor.putInt(id+"-color", Color.WHITE );
                                editor.apply();
                                dialog.dismiss();
                                break;
                            case 1:
                                editor.putInt(id+"-color", Color.GREEN );
                                dialog.dismiss();
                                editor.apply();
                                break;
                            case 2:
                                editor.putInt(id+"-color", Color.BLUE );
                                editor.apply();
                                dialog.dismiss();
                                break;
                            case 3:
                                editor.putInt(id+"-color", Color.YELLOW );
                                editor.apply();
                                dialog.dismiss();
                                break;
                            case 4:
                                editor.putInt(id+"-color", Color.BLACK );
                                editor.apply();
                                dialog.dismiss();
                                break;
                            case 5:
                                editor.putInt(id+"-color", Color.GRAY );
                                editor.apply();
                                dialog.dismiss();
                                break;
                            case 6:
                                editor.putInt(id+"-color", Color.CYAN );
                                editor.apply();
                                dialog.dismiss();
                                break;
                            case 7:
                                editor.putInt(id+"-color", Color.RED );
                                editor.apply();
                                dialog.dismiss();
                                break;
                            case 8:
                                editor.putInt(id+"-color", Color.DKGRAY );
                                editor.apply();
                                dialog.dismiss();
                                break;
                            case 9:
                                editor.putInt(id+"-color", Color.LTGRAY);
                                editor.apply();
                                dialog.dismiss();
                                break;
                            case 10:
                                editor.putInt(id+"-color", Color.MAGENTA );
                                editor.apply();
                                dialog.dismiss();
                                break;
                        }
                    }
                }
        );
        builder.show();
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

        Intent intent = new Intent(view.getContext(), ListaAlbaranesActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void irConsumos(View view) {
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        Intent intent = new Intent(view.getContext(), ListaConsumosActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void irMantenimiento(View view) {
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

        Intent intent = new Intent(view.getContext(), MantenimientoActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void irCamionNombre(final View view) {
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
                    editor.putString(id + "-nombreCamion", identificador);
                    editor.apply();
                } else {
                    String identificador = edt.getText().toString();
                    Toast.makeText(getApplicationContext(), "El identificador personalizado del camión es: " + identificador, Toast.LENGTH_SHORT).show();
                    pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = pref.edit();
                    editor.putString(id + "-nombreCamion", identificador);
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
