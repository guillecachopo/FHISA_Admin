package com.example.guill.fhisa_admin;

import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Adapter.PageAdapter;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

public class MainActivity extends AppCompatActivity {

    TextView tvCamionInfo;
    double lat = 0.0; //Latitud inicial para el marcador de posicion inicial
    double lng = 0.0; //Longitud inicial para el marcador de posicion inicial
    String imei = "";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setUpViewPager();

        //tvCamionInfo = (TextView) findViewById(R.id.tvCamion);

        FirebaseDatabase database = FirebaseDatabase.getInstance(); //Cualquier referencia tiene que ser igual al mismo tipo pero cogiendo la instancia
       // final DatabaseReference camionesRef = database.getReference(FirebaseReferences.FHISA_REFERENCE); //referencia a la bdd de firebase
       // camionesRef.child(FirebaseReferences.CAMION_REFERENCE).addValueEventListener(new ValueEventListener()

        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);
        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //Para poner en orbita los fragments
    private ArrayList<Fragment> agregarFragment(){
        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new MapsActivity());
        fragments.add(new RecyclerViewFragment());

        return fragments; //Ya tenemos los fragments en un arraylist
    }

    private void setUpViewPager(){
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), agregarFragment()));
        //Con la linea anterior pasamos soporte fragment manager y la lista de fragments q queremos agregar al page adapter (viewpager)
        tabLayout.setupWithViewPager(viewPager); //Lo agregamos al tabLayout

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_map_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_truck_white);
    }

    //-----MENU OPTIONS---------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones,menu);
        return true;
    }

    private boolean isStarted = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //El item del menu seleccionado

        switch (item.getItemId()){
            case R.id.menuNotificaciones:


                ComponentName cp = new ComponentName(this, NotificationJobScheduler.class);
                JobInfo jb;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    jb = new JobInfo.Builder(1, cp)
                            .setBackoffCriteria(4000, JobInfo.BACKOFF_POLICY_LINEAR)
                            .setPersisted(true)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setRequiresCharging(false)
                            .setRequiresDeviceIdle(false)
                            .setMinimumLatency(10000) //Setear en milisegundos cada cuánto tiempo queremos que se ejecute el job para comprobar posiciones
                            .build();
                } else {
                    jb = new JobInfo.Builder(1, cp)
                            .setBackoffCriteria(4000, JobInfo.BACKOFF_POLICY_LINEAR)
                            .setPersisted(true)
                            .setPeriodic(10000) //Setear en milisegundos cada cuánto tiempo queremos que se ejecute el job para comprobar posiciones
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setRequiresCharging(false)
                            .setRequiresDeviceIdle(false)
                            .build();
                }

                JobScheduler js = JobScheduler.getInstance(this);
                js.schedule(jb);
                Toast.makeText(this, "Servicio de notificaciones activo", Toast.LENGTH_LONG).show();
                isStarted = true;
                return true;

            case R.id.menuStopNotificaciones:
                JobScheduler js2 = JobScheduler.getInstance(this);
                js2.cancelAll();
                Toast.makeText(this, "Servicio de notificaciones inactivo", Toast.LENGTH_LONG).show();
                isStarted = false;
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menuNotificaciones).setVisible(!isStarted);
        menu.findItem(R.id.menuStopNotificaciones).setVisible(isStarted);
        return true;
    }

    //-------------


}
