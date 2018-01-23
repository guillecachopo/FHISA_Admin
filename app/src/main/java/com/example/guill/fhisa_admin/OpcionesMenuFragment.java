package com.example.guill.fhisa_admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by guill on 12/12/2017.
 */

public class OpcionesMenuFragment extends PreferenceFragment {

    Preference mPassword;
    Preference tvModificarBasesOperativas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.opciones);

        mPassword = (Preference) findPreference("etPassword");
        tvModificarBasesOperativas = (Preference) findPreference("tvModificarBO");

        mPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                changePassword();
                return false;
            }
        });

        tvModificarBasesOperativas.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ModificarBasesOperativasActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    private void changePassword(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_password, null);
        dialogBuilder.setView(dialogView);
        final EditText etPasswordOld = (EditText) dialogView.findViewById(R.id.etPasswordOld);
        final EditText etPasswordNew = (EditText) dialogView.findViewById(R.id.etPassword);

        dialogBuilder.setTitle("Contraseña para finalizar FHISA Servicio");
        //dialogBuilder.setMessage("Introduce la nueva contraseña");
        dialogBuilder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {

                final String passwordOld = etPasswordOld.getText().toString();
                final String passwordNew=etPasswordNew.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference passwordRef = database.getReference("password_servicio");

                passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String passwordActual = String.valueOf(dataSnapshot.getValue());
                        if (passwordActual.compareTo(passwordOld)!=0) {
                            Toast.makeText(getActivity(), "La contraseña actual introducida no es correcta.", Toast.LENGTH_SHORT).show();
                            changePassword();
                        } else {
                            passwordRef.setValue(passwordNew);
                            Toast.makeText(getActivity(), "Contraseña modificada correctamente", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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
