package com.example.guill.fhisa_admin.OpcionesMenu;

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
import com.example.guill.fhisa_admin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        AlertDialog alertDialog = new AlertDialog.Builder(OpcionesMenuActivity.this).create();
        alertDialog.setTitle("Envío de contraseña por correo electrónico");
        alertDialog.setMessage("Se enviará una nueva contraseña aleatoria por correo electrónico");
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
        String newPassword = generarPassword();
        passwordRef.setValue(newPassword);
        progressBar.setVisibility(View.VISIBLE);
        EnviarEmailPassword enviarEmailPassword = new EnviarEmailPassword(getApplicationContext(), preferences, progressBar);
        enviarEmailPassword.execute(newPassword);
        dialog.dismiss();
    }

    private String generarPassword() {
        char[] elementos={'0','1','2','3','4','5','6','7','8','9' ,'a',
                'b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t',
                'u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M',
                'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        char[] conjunto = new char[10];
        String pass;

        for (int i=0; i<10; i++) {
            int el = (int) (Math.random()*62);
            conjunto[i] = (char) elementos[el];
        }
        return pass= new String (conjunto);
    }

}
