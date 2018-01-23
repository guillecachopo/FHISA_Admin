package com.example.guill.fhisa_admin.PreferencesCamion;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.guill.fhisa_admin.R;

import java.util.Random;

public class CamionPreferencesActivity extends AppCompatActivity {
    String id;
    private Toolbar toolbar;
    private static final CharSequence[] COLORES_ITEMS =
            {"white", "green", "blue", "yellow", "black", "grey", "cyan", "red", "dkgray", "ltgray", "magenta"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camion_preferences);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");

    }

    public void trazarRuta(View view) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(id+"-trazar", true);
        editor.apply();
        editor.commit();
    }

    public void elegirColor(View view) {
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

                            case 1:
                                editor.putInt(id+"color", Color.WHITE );
                                editor.apply();
                                break;
                            case 2:
                                editor.putInt(id+"color", Color.GREEN );
                                editor.apply();
                                break;
                            case 3:
                                editor.putInt(id+"color", Color.BLUE );
                                editor.apply();
                                break;
                            case 4:
                                editor.putInt(id+"color", Color.YELLOW );
                                editor.apply();
                                break;
                            case 5:
                                editor.putInt(id+"color", Color.BLACK );
                                editor.apply();
                                break;
                            case 6:
                                editor.putInt(id+"color", Color.GRAY );
                                editor.apply();
                                break;
                            case 7:
                                editor.putInt(id+"color", Color.CYAN );
                                editor.apply();
                                break;
                            case 8:
                                editor.putInt(id+"color", Color.RED );
                                editor.apply();
                                break;
                            case 9:
                                editor.putInt(id+"color", Color.DKGRAY );
                                editor.apply();
                                break;
                            case 10:
                                editor.putInt(id+"color", Color.LTGRAY);
                                editor.apply();
                                break;
                            case 11:
                                editor.putInt(id+"color", Color.MAGENTA );
                                editor.apply();
                                break;
                        }
                    }
                }
        );
        builder.setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    public int generaColorRandom(){
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        int randomColor = Color.rgb(r,g,b);
        return randomColor;
    }

}
