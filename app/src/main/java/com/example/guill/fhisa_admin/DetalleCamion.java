package com.example.guill.fhisa_admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Adapter.AdapterPosiciones;
import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetalleCamion extends AppCompatActivity {

    ArrayList<Camion> camiones;
    private RecyclerView listaPosiciones;
    ArrayList<String> posicionesString;
    ArrayList<String> posicionesAux;

    ArrayList<String> horasString;
    ArrayList<String> horasAux;

    String id;
    double altitude;
    double latitude;
    double longitude;
    float speed;
    long time;
    List<String> IDs;
    private TextView tvPosiciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listaPosiciones = (RecyclerView) findViewById(R.id.rvCamionIndividual);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listaPosiciones.setLayoutManager(llm); //Para q el recycleview se comporte como un LinearLayout
        inicializarListaPosiciones();
        inicializarAdaptador();

    }

    public void inicializarListaPosiciones(){

        posicionesString = new ArrayList<>();
        posicionesAux = new ArrayList<>();
        camiones = new ArrayList<>();
        IDs = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        posicionesAux = (ArrayList<String>) getIntent().getSerializableExtra("posiciones");

        horasAux = (ArrayList<String>) getIntent().getSerializableExtra("horas");
        horasString = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance(); //Cualquier referencia tiene que ser igual al mismo tipo pero cogiendo la instancia
        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);
        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {

                    Camion camion=null;

                    if (!IDs.contains(id)) {
                        camion = new Camion(id);
                        IDs.add(id);
                        camiones.add(camion);
                    }
                    else {
                        for (int i=0; i<camiones.size(); i++)
                            if (camiones.get(i).getId().compareTo(id)==0) {
                                camion = camiones.get(i);
                                camion.clearPosiciones();
                                posicionesAux.clear();
                                horasAux.clear();
                            }
                    }

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {

                            Posicion posicion = snapshot2.getValue(Posicion.class);
                            camion.setPosiciones(posicion);

                            altitude = camion.getUltimaPosicion().getAltitude();
                            latitude = camion.getUltimaPosicion().getLatitude();
                            longitude = camion.getUltimaPosicion().getLongitude();
                            speed = camion.getUltimaPosicion().getSpeed();
                            time = camion.getUltimaPosicion().getTime();
                            LatLng latlng = new LatLng(latitude, longitude);

                        } //for snapshot2 (Iterador donde estan las posiciones)
                    } //for snapshot1 (Iterador donde esta la cadena "posiciones")
                    for (int i = 0; i< camion.getPosicionesList().size(); i++) {
                        posicionesAux.add(camion.getPosicionesList().get(i).toString());
                    }

                    for (int i = 0; i < camion.getHorasList().size(); i++) {
                        horasAux.add(camion.getHorasList().get(i).toString());
                    }

                    Log.i("HORAS", " " + camion.getHorasList().size());

                    for (int i = 0; i < posicionesAux.size(); i++) {
                        if (!posicionesAux.get(i).startsWith("com")) {
                            posicionesString.add(posicionesAux.get(i));
                            horasString.add(horasAux.get(i));
                        }
                    }

                } //for snapshot (Iterador donde estan las IDs)
                adaptador.notifyDataSetChanged();
            } //onDataChange
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //ValueEventListener

    }

    public AdapterPosiciones adaptador;
    public void inicializarAdaptador(){
        //Crea un objeto de contacto adaptador y le pasa la lista que tenemos para hacer internamente lo configurado en esa activity
        adaptador = new AdapterPosiciones(posicionesString, horasString, id, this);
        listaPosiciones.setAdapter(adaptador);
    }



}
