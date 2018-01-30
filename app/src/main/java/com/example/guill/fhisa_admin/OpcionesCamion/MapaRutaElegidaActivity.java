package com.example.guill.fhisa_admin.OpcionesCamion;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.guill.fhisa_admin.Objetos.BaseOperativa;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.example.guill.fhisa_admin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapaRutaElegidaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;

    /**
     * Objeto mapa
     */
    GoogleMap mMap;

    /**
     * Base de datos Firebase a utilizar
     */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Preferencias compartidas
     */
    SharedPreferences preferences;

    /**
     * Referencia de las areas en Firebase
     */
    final DatabaseReference areasRef = database.getReference(FirebaseReferences.AREAS_REFERENCE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_ruta_elegida);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        setToolbar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng oviedo = new LatLng(43.458979, -5.850589);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(oviedo));
        inicializarAreas(areasRef);

        Bundle extras = getIntent().getExtras();
        String ruta = extras.getString("ruta");
        final String imei = extras.getString("imei");
        final ArrayList<Posicion> listaPosiciones = new ArrayList<>();

        DatabaseReference rutaRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE)
                .child(imei).child("rutas").child("rutas_completadas").child(ruta);

        rutaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot rutaSnapshot : dataSnapshot.getChildren()) {
                    Posicion posicion = rutaSnapshot.getValue(Posicion.class);
                    listaPosiciones.add(posicion);
                }

                addMarkerInicial(listaPosiciones, imei);
                addMarkerFinal(listaPosiciones, imei);
                dibujarRuta(listaPosiciones, imei);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Método encargado de mostrar en el mapa las areas existentes
     * @param areasRef
     */
    public void inicializarAreas(DatabaseReference areasRef) {

        final ArrayList<BaseOperativa> listaBasesOperativas = new ArrayList<>();
        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idArea = snapshot.getValue(BaseOperativa.class).getIdentificador();
                    BaseOperativa baseOperativa = null;
                    baseOperativa = snapshot.getValue(BaseOperativa.class);
                    listaBasesOperativas.add(baseOperativa);

                }

                //Dibujamos todos las areas que tenemos en firebase
                for (int i = 0; i< listaBasesOperativas.size(); i++) {
                    Circle circle = dibujarCirculo(listaBasesOperativas.get(i));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Dibuja un baseOperativa
     * @param baseOperativa
     * @return
     */
    private Circle dibujarCirculo(BaseOperativa baseOperativa) {
        Circle circulo = mMap.addCircle(new CircleOptions()
                .center(new LatLng(baseOperativa.getLatitud(), baseOperativa.getLongitud()))
                .radius(baseOperativa.getDistancia())
                .strokeColor(0x70FE2E2E)
                .fillColor(0x552E86C1));
        return circulo;
    }

    /**
     * Añade el marcador de la última posición de la ruta
     * @param listaPosiciones
     * @param imei
     */
    private void addMarkerFinal(ArrayList<Posicion> listaPosiciones, String imei) {
        final String alias = preferences.getString(imei + "-nombreCamion", imei);
        int size = listaPosiciones.size()-1;
        LatLng latlngFinal = new LatLng(listaPosiciones.get(size).getLatitude(), listaPosiciones.get(size).getLongitude());
        String ultimaHoraRuta = ultimaHoraRuta(listaPosiciones);

        MarkerOptions markerOptionsFinal = new MarkerOptions()
                .position(latlngFinal)
                .title(alias)
                .snippet("Fin: " + ultimaHoraRuta)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        Marker markerFinal = mMap.addMarker(markerOptionsFinal);
    }

    /**
     * Añade el marcador de la primera posición de la ruta
     * @param listaPosiciones
     * @param imei
     */
    private void addMarkerInicial(ArrayList<Posicion> listaPosiciones, String imei) {
        final String alias = preferences.getString(imei + "-nombreCamion", imei);
        LatLng latlngInicial = new LatLng(listaPosiciones.get(0).getLatitude(), listaPosiciones.get(0).getLongitude());
        String primeraHoraRuta = primeraHoraRuta(listaPosiciones);
        moverCamara(latlngInicial);

        MarkerOptions markerOptionsInicial = new MarkerOptions()
                .position(latlngInicial)
                .title(alias)
                .snippet("Inicio: " + primeraHoraRuta);

        Marker markerInicial = mMap.addMarker(markerOptionsInicial);
    }

    /**
     * Mueve la cámara a una posición pasada por parámetro
     * @param latlngInicial
     */
    private void moverCamara(LatLng latlngInicial) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngInicial, 12));
    }

    /**
     * Obtiene la primera hora de la ruta
     * @param listaPosiciones
     * @return
     */
    private String primeraHoraRuta(ArrayList<Posicion> listaPosiciones) {
        long horaLong = listaPosiciones.get(0).getTime();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(horaLong);
        String hora = format.format(date);
        return hora;
    }

    /**
     * Obtiene la ultima hora de la ruta
     * @param listaPosiciones
     * @return
     */
    private String ultimaHoraRuta(ArrayList<Posicion> listaPosiciones) {
        long horaLong = listaPosiciones.get(listaPosiciones.size()-1).getTime();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(horaLong);
        String hora = format.format(date);
        return hora;
    }

    /**
     * Dibuja una ruta
     * @param listaPosiciones
     * @param imei
     */
    private void dibujarRuta(ArrayList<Posicion> listaPosiciones, String imei) {
        int colorRuta = preferences.getInt(imei+"-color", Color.RED);
        List<LatLng> latlngs = new ArrayList<>();
        for (Posicion posicion : listaPosiciones) {
            LatLng latlng = new LatLng(posicion.getLatitude(), posicion.getLongitude());
            latlngs.add(latlng);
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .color(colorRuta)
                .width(10);
        Polyline polyline = mMap.addPolyline(polylineOptions);
        polyline.setPoints(latlngs);
    }

    /**
     * Método para activar la toolbar
     * @param toolbar
     */
    private void setToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
