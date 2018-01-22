package com.example.guill.fhisa_admin;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.guill.fhisa_admin.Mail.EnviarEmailPassword;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by guill on 12/12/2017.
 */

public class OpcionesMenuActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones_menu);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_opciones, new OpcionesMenuFragment()).commit();
    }

    public void passwordOlvidada(View view) {
        OpcionesMenuFragment opcionesMenuFragment = new OpcionesMenuFragment();

        AlertDialog alertDialog = new AlertDialog.Builder(OpcionesMenuActivity.this).create();
        alertDialog.setTitle("Envío de contraseña por correo electrónico");
        alertDialog.setMessage("Se enviará la contraseña actual por correo electrónico");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ENVIAR",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        enviarPassword(dialog);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCELAR",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void enviarPassword(final DialogInterface dialog) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference passwordRef = database.getReference("password_servicio");
        passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String passwordActual = String.valueOf(dataSnapshot.getValue());
                progressBar.setVisibility(View.VISIBLE);
                EnviarEmailPassword enviarEmailPassword = new EnviarEmailPassword(getApplicationContext(), preferences, progressBar);
                enviarEmailPassword.execute(passwordActual);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
