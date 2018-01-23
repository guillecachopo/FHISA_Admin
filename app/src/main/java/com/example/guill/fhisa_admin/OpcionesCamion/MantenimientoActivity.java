package com.example.guill.fhisa_admin.OpcionesCamion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.guill.fhisa_admin.R;
import com.example.guill.fhisa_admin.Socket.PeticionMantenimiento;

public class MantenimientoActivity extends AppCompatActivity {

    public TextView tvItvMantenimiento;
    public RecyclerView rvMantenimiento;
    private Toolbar toolbar;
    public ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenimiento);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        setToolbar(toolbar);

        rvMantenimiento = (RecyclerView) findViewById(R.id.rvMantenimiento);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvMantenimiento.setLayoutManager(llm);
        rvMantenimiento.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //Linea

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String imei = getImei();
        PeticionMantenimiento peticionMantenimiento = new PeticionMantenimiento(this, rvMantenimiento, progressBar);
        peticionMantenimiento.execute(imei);

        tvItvMantenimiento = (TextView) findViewById(R.id.tvItvMantenimiento);
    }

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
