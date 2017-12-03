package com.example.guill.fhisa_admin.Opciones;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.guill.fhisa_admin.R;
import com.example.guill.fhisa_admin.Socket.PeticionConsumos;

/**
 * Created by guill on 30/11/2017.
 */

public class ListaConsumosActivity extends AppCompatActivity {

    RecyclerView rvListaConsumos;

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_consumos);

        toolbar = (Toolbar) findViewById(R.id.actionBar);
        setToolbar(toolbar);

        rvListaConsumos = (RecyclerView) findViewById(R.id.rvConsumos);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvListaConsumos.setLayoutManager(llm);
        rvListaConsumos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        String imei = getImei();
        PeticionConsumos peticionConsumos = new PeticionConsumos(this, rvListaConsumos);
        peticionConsumos.execute(imei);

    }

    /**
     * Método para obtener el imei del camión obtenido desde otra Activity
     * @return imei
     */
    private String getImei() {
        Bundle extras = getIntent().getExtras();
        String imei = extras.getString("id");
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
