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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by guill on 08/01/2018.
 */

public class VelocidadActualActivity extends AppCompatActivity {

    private Toolbar toolbar;
    SpeedView speedometer;
    TextView tvVelocidadActual;

    /**
     * Lista que contiene los camiones
     */
    ArrayList<Camion> listaCamiones;

    /**
     * Lista que contiene las Ids de los camiones
     */
    ArrayList<String> listaIdsCamiones;

    ArrayList<Posicion> listaPosiciones;

    /**
     * Base de datos Firebase a utilizar
     */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Referencia de los camiones en Firebase
     */
    final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);

    String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_velocidad_actual);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imei = getImei();

        speedometer = (SpeedView) findViewById(R.id.speedView);
        tvVelocidadActual = (TextView) findViewById(R.id.tvVelocidadActual);
        inicializarListaCamiones(camionesRef);
    }


    /**
     * Método encargado de escuchar a Firebase con las actualizaciones de los camiones
     * @param camionesRef
     */
    public void inicializarListaCamiones(DatabaseReference camionesRef) {

        camionesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();

                if (id.compareTo(imei) == 0) {
                    Camion camionPos = new Camion(id);
                    actualizarCamion(camionPos, dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();

                if (id.compareTo(imei) == 0) {
                    Camion camionPos = new Camion(id);
                    actualizarCamion(camionPos, dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Método encargado de actualizar las posiciones de cada camión en el mapa
     * @param camion
     * @param snapshot
     */
    private void actualizarCamion(final Camion camion, final DataSnapshot snapshot) {

        Query q = snapshot.child("rutas").child("ruta_actual").getRef().orderByKey().limitToLast(2);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("DataSnapshot", String.valueOf(dataSnapshot.exists()));
                Log.i("DataSnapshot", String.valueOf(dataSnapshot.getValue()));

                if (!dataSnapshot.exists()) { //Si no está en ruta

                    speedometer.setSpeedAt(0);
                    tvVelocidadActual.setText("El camión no está en ruta.");

                } else { //Si está en ruta
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Posicion posicion = child.getValue(Posicion.class);
                        camion.setPosiciones(posicion);
                        long time = camion.getUltimaPosicion().getTime();
                        camion.setHoras(time);
                    }
                    Log.i("DataSnapshot", String.valueOf(camion.getPosicionesList().size()));
                    actualizaVelocidad(camion);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void actualizaVelocidad(Camion camion) {
        double lat2, lat1, lng2, lng1;
        long time2, time1;
        float speed;
        Posicion pos1, pos2;
        pos2 = camion.getUltimaPosicion();
        pos1 = camion.getPenultimaPosicion();
        lat2 = pos2.getLatitude();
        lng2 = pos2.getLongitude();
        lat1 = pos1.getLatitude();
        lng1 = pos1.getLongitude();
        time2 = pos2.getTime();
        time1 = pos1.getTime();
        float distance = calculateDistance(lat1,lng1,lat2,lng2);
        float timeDistance = (time2-time1)/1000; //Pasamos de ms a segundos
        speed = (distance)/(timeDistance);

        if (speed < 150) {
            speedometer.setSpeedAt(speed);
            tvVelocidadActual.setText("Velocidad: "
                    + String.format("%.1f", speed).replace(",",".") + "Km/h");
        }

        camion.clearHoras();
        camion.clearPosiciones();

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

    /**
     * Método para obtener el imei del camión obtenido desde otra Activity
     * @return imei
     */
    private String getImei() {
        Bundle extras = getIntent().getExtras();
        String imei = extras.getString("id");
        return imei;
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
