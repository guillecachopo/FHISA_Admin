package com.example.guill.fhisa_admin.Opciones;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.guill.fhisa_admin.Adapter.PageAdapter;
import com.example.guill.fhisa_admin.R;
import com.example.guill.fhisa_admin.Socket.PeticionAlbaran;

import java.util.ArrayList;

public class AlbaranActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albaran);

        toolbar = (Toolbar) findViewById(R.id.actionBar);
        setToolbar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setUpViewPager();

        String idAlbaran = getIdAlbaran();
        String imei = getImei();
        String imeiIdAlbaran = imei + "---" + idAlbaran;

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        PeticionAlbaran peticionAlbaran = new PeticionAlbaran(this, progressBar);
        peticionAlbaran.execute(imeiIdAlbaran);


    }


    //Para poner en orbita los fragments
    private ArrayList<Fragment> agregarFragment(){
        ArrayList<Fragment> fragments = new ArrayList<>();

        AlbaranFragmentInfo albaranFragmentInfo = new AlbaranFragmentInfo();
        AlbaranFragmentCarga albaranFragmentCarga = new AlbaranFragmentCarga();

        fragments.add(albaranFragmentInfo);
        fragments.add(albaranFragmentCarga);

        return fragments; //Ya tenemos los fragments en un arraylist
    }

    private void setUpViewPager() {
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), agregarFragment()));
        //Con la linea anterior pasamos soporte fragment manager y la lista de fragments q queremos agregar al page adapter (viewpager)
        tabLayout.setupWithViewPager(viewPager); //Lo agregamos al tabLayout

        tabLayout.getTabAt(0).setText("Info");
        tabLayout.getTabAt(1).setText("Carga");
    }

    private String getIdAlbaran() {
        Bundle extras = getIntent().getExtras();
        String idAlbaran = extras.getString("idAlbaran");
        return idAlbaran;
    }

    private String getImei() {
        Bundle extras = getIntent().getExtras();
        String imei = extras.getString("imei");
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
