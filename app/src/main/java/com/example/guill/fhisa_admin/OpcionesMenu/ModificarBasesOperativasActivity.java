package com.example.guill.fhisa_admin.OpcionesMenu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Adapter.AdapterBasesOperativas;
import com.example.guill.fhisa_admin.Objetos.BaseOperativa;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ModificarBasesOperativasActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public RecyclerView rvBasesOperativas;
    AdapterBasesOperativas adaptador;

    /**
     * Lista que contiene las bases operativas
     */
    ArrayList<BaseOperativa> listaBasesOperativas;

    /**
     * Base de datos Firebase a utilizar
     */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Referencia de los camiones en Firebase
     */
    final DatabaseReference areasRef = database.getReference(FirebaseReferences.AREAS_REFERENCE);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_bases_operativas);

        toolbar = (Toolbar) findViewById(R.id.actionBar);
        setToolbar(toolbar);

        rvBasesOperativas = (RecyclerView) findViewById(R.id.rvBasesOperativas);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvBasesOperativas.setLayoutManager(llm);
        rvBasesOperativas.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //Linea

        inicializarListaBasesOperativas(areasRef);
        inicializarAdaptador(rvBasesOperativas);

    }

    /**
     * Método encargfado de inicializar el adaptador del RecyclerView
     * @param recyclerView
     */
    private void inicializarAdaptador(RecyclerView recyclerView) {
        //Crea un objeto de contacto adaptador y le pasa la lista que tenemos para hacer internamente lo configurado en esa activity
        adaptador = new AdapterBasesOperativas(this, listaBasesOperativas);
        recyclerView.setAdapter(adaptador);
    }


    public void inicializarListaBasesOperativas(DatabaseReference areasRef) {
        listaBasesOperativas = new ArrayList<>();

        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    BaseOperativa baseOperativa = areaSnapshot.getValue(BaseOperativa.class);
                    listaBasesOperativas.add(baseOperativa);
                }
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Método encargado de mostrar un AlertDialog para la modificación de la Base Operativa. Guarda la
     * base operativa en Firebase.
     */
    public void modificarBaseOperativa(final BaseOperativa baseOperativa, final TextView tvRadio) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_modificar_area, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etRadioArea);

        dialogBuilder.setTitle("Modificación de base operativa");
        dialogBuilder.setMessage("Elija en metros el radio del baseOperativa.");
        dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String radioIntroducido = edt.getText().toString();
                if (radioIntroducido.equals("")) {
                    Toast.makeText(getApplicationContext(), "No se ha introducido un valor válido",
                            Toast.LENGTH_SHORT).show();
                }
                else {

                    baseOperativa.setDistancia(Integer.parseInt(radioIntroducido));
                    tvRadio.setText(String.valueOf(baseOperativa.getDistancia()));

                    DatabaseReference areasRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.AREAS_REFERENCE);
                    areasRef.child(baseOperativa.getIdentificador()).setValue(baseOperativa);

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
