package com.example.guill.fhisa_admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Adapter.PageAdapter;
import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
                Camion camion = dataSnapshot.getValue(Camion.class);
                Log.i("CAMION", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void irMapa(View view){
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
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

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_mapa);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_truck);
    }

    /*
    public void irCamiones(View view){
        Intent i = new Intent(this, RecyclerActivity.class);
        startActivity(i);
    }
    */

}
