package com.example.guill.fhisa_admin.OpcionesCamion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.guill.fhisa_admin.R;
import com.example.guill.fhisa_admin.Socket.PeticionAlbaranes;

public class ListaAlbaranesActivity extends AppCompatActivity {

    RecyclerView rvListaAlbaranes;
    private Toolbar toolbar;
    public ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_albaranes);

        toolbar = (Toolbar) findViewById(R.id.actionBar);
        setToolbar(toolbar);

        rvListaAlbaranes = (RecyclerView) findViewById(R.id.rvAlbaranes);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvListaAlbaranes.setLayoutManager(llm);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String imei = getImei();
        PeticionAlbaranes peticionAlbaranes = new PeticionAlbaranes(this, rvListaAlbaranes, progressBar);
        peticionAlbaranes.execute(imei);
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
