package com.example.guill.fhisa_admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by guill on 12/12/2017.
 */

public class OpcionesMenuFragment extends PreferenceFragment {

    Preference mPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.opciones);

        mPassword = (Preference) findPreference("etPassword");

        mPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                changePassword();
                return false;
            }
        });
    }

    private void changePassword(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_password, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.etPassword);

        dialogBuilder.setTitle("Contraseña para finalizar FHISA Servicio");
        dialogBuilder.setMessage("Introduce la nueva contraseña");
        dialogBuilder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String password=edt.getText().toString();
                Log.i("Password", password);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference passwordRef = database.getReference("password_servicio");
                passwordRef.setValue(password);
                Toast.makeText(getActivity(), "Contraseña modificada correctamente", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
        //mService.removeLocationUpdates();
    }

}
