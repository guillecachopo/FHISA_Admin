package com.example.guill.fhisa_admin.Opciones;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.example.guill.fhisa_admin.R;
import com.github.anastr.speedviewlib.SpeedView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VelocidadActualActivity extends AppCompatActivity {

    private Toolbar toolbar;
    ArrayList<Camion> camiones;
    String id;
    double altitude;
    double latitude;
    double longitude;
    float speed;
    long time;
    List<String> IDs;
    SpeedView speedometer;
    TextView tvVelocidadActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_velocidad_actual);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        speedometer = (SpeedView) findViewById(R.id.speedView);
        tvVelocidadActual = (TextView) findViewById(R.id.tvVelocidadActual);
        inicializarListaCamiones();
    }

    public void inicializarListaCamiones(){

        camiones = new ArrayList<Camion>();
        IDs = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance(); //Cualquier referencia tiene que ser igual al mismo tipo pero cogiendo la instancia
        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);
        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {

                    id = snapshot.getKey();
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
                                camion.clearHoras();
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


                    for (int i=0; i<camion.getPosicionesList().size()-2; i++) {
                        double lat2, lat1, lng2, lng1;
                        long time2, time1;
                        float speed;
                        Posicion pos1, pos2;
                        pos2 = camion.getPosicionesList().get(i+1);
                        pos1 = camion.getPosicionesList().get(i);
                        lat2 = pos2.getLatitude();
                        lng2 = pos2.getLongitude();
                        lat1 = pos1.getLatitude();
                        lng1 = pos1.getLongitude();
                        time2 = pos2.getTime();
                        time1 = pos1.getTime();
                        float distance = calculateDistance(lat1,lng1,lat2,lng2);
                        float time = (time2-time1)/1000; //Pasamos de ms a segundos
                        speed = (distance)/(time);


                        camion.getPosicionesList().get(i).setSpeed(speed);
                        Log.i("Cosas", "speed" + String.valueOf(speed));

                        speedometer.setSpeedAt(speed);
                        tvVelocidadActual.setText("Velocidad (última posición): " + String.format("%.1f", speed).replace(",",".") + "Km/h");
                    }


                } //for snapshot (Iterador donde estan las IDs)
            } //onDataChange
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //ValueEventListener

    }

    private static long calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMeters = Math.round(6371000 * c);
        return distanceInMeters;
    }

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
