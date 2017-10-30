package com.example.guill.fhisa_admin;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    View mView;
    MapView mMapView;

    Posicion location;
    String id;
    LatLng latlng;

    double altitude;
    double latitude;
    double longitude;
    float speed;
    long time;
    List<Camion> camionesList;
    List<Integer> coloresLista;
    List<String> IDs;
    long numCamiones;
    MarkerOptions markerOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //        .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.activity_maps, container, false);

        ImageView button = (ImageView) mView.findViewById(R.id.btnTipoMapa);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showMapTypeSelectorDialog();
            }
        });


        return mView;
    }

    @Nullable
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    //AQUI ES DONDE PODEMOS HACER COSAS
    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        mMap = googleMap;

        camionesList = new ArrayList<>();
        IDs = new ArrayList<>();
        coloresLista = new ArrayList<>();

        final LatLng oviedo = new LatLng(43.3579649511212,-5.8733862770);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(oviedo)); //Ponemos el mapa inicialmente centrado en el centro de asturias
        CameraUpdate cuOviedo = CameraUpdateFactory.newLatLngZoom(oviedo, 9); //Que el mapa no empiece con asturias muy lejos
        mMap.animateCamera(cuOviedo);

        FirebaseDatabase database = FirebaseDatabase.getInstance(); //Cualquier referencia tiene que ser igual al mismo tipo pero cogiendo la instancia

        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);

        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        final SharedPreferences settings = getContext().getSharedPreferences(id, 0);

        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("CAMIONES dataSnapshot", String.valueOf(dataSnapshot.getChildrenCount()));
                numCamiones = dataSnapshot.getChildrenCount();

                //AQUI ESTAN LAS ID
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {

                    id = snapshot.getKey();
                    Camion camion=null;

                    if (!IDs.contains(id)) {
                        camion = new Camion(id);
                        IDs.add(id);
                        camionesList.add(camion);

                        int randomColor = generaColorRandom(); //Genero un color aleatorio para cada camion
                        coloresLista.add(randomColor); //Añado el color aleatorio a una lista
                    }
                    else {
                         for (int i=0; i<camionesList.size(); i++)
                             if (camionesList.get(i).getId().compareTo(id)==0) {
                                 camion = camionesList.get(i);
                                 camion.clearPosiciones();
                             }
                    }
                    Log.i("CAMIONES", String.valueOf(camionesList.size()));

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

                } //for snapshot (Iterador donde estan las IDs)




                mMap.clear(); //Limpiamos el mapa cada vez que llega una posicion para que se actualice el marcador
                for (int i=0; i<camionesList.size(); i++){
                    Camion pintado = camionesList.get(i);
                   // Integer color = coloresLista.get(i); //Cojo un color de la lista de colores random

                    markerOptions = new MarkerOptions()
                            .position(new LatLng(pintado.getUltimaPosicion().getLatitude(), pintado.getUltimaPosicion().getLongitude()))
                            .title(camionesList.get(i).getId());

                    marcador = mMap.addMarker(markerOptions);

                  //  dibujaRuta(pintado, color); //Dibujo la ruta del camión con un color

                    String prefColor = pref.getString("lpColorTrazo", "random");
                    // Comprobamos si se desea dibujar la ruta, en caso de no
                    // estar definida la propiedad por defecto indicamos true.
                    boolean prefTrazoRuta = pref.getBoolean("cbxDibujarRuta", true);

                    if (prefTrazoRuta) {
                        Integer color;
                        if (prefColor.equals("white"))
                            color = Color.WHITE;
                        else if (prefColor.equals("green"))
                            color = Color.GREEN;
                        else if (prefColor.equals("blue"))
                            color = Color.BLUE;
                        else if (prefColor.equals("yellow"))
                            color = Color.YELLOW;
                        else if (prefColor.equals("black"))
                            color = Color.BLACK;
                        else if (prefColor.equals("grey"))
                            color = Color.GRAY;
                        else if (prefColor.equals("cyan"))
                            color = Color.CYAN;
                        else if (prefColor.equals("red"))
                            color = Color.RED;
                        else if (prefColor.equals("dkgray"))
                            color = Color.DKGRAY;
                        else if (prefColor.equals("ltgray"))
                            color = Color.LTGRAY;
                        else if (prefColor.equals("magenta"))
                            color = Color.MAGENTA;
                        else if (prefColor.equals("aleatorio"))
                            color = coloresLista.get(i);
                        else
                            color = coloresLista.get(i);

                        dibujaRuta(pintado, color);

                    }

                }




            } //onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //ValueEventListener


    }

    //Dibujamos la ruta del camion pasandole un color aleatorio que cambiará en funcion del camion
    public void dibujaRuta(Camion camionPintar, int randomColor) {

        for (int k=0; k<camionPintar.getPosicionesList().size()-1; k++) {

            mMap.addPolyline(new PolylineOptions().add(
                    new LatLng(camionPintar.getPosicionesList().get(k).getLatitude(), camionPintar.getPosicionesList().get(k).getLongitude()),
                    new LatLng(camionPintar.getPosicionesList().get(k+1).getLatitude(), camionPintar.getPosicionesList().get(k+1).getLongitude()))
                    .width(10)
                    .color(randomColor)
            );
        }
    }

    public int generaColorRandom(){
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        int randomColor = Color.rgb(r,g,b);
        return randomColor;
    }


    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};

    private void showMapTypeSelectorDialog() {

        Log.i("Click", "Dentro de showMapTypeSelectorDialog");
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(fDialogTitle);
        builder.create();
        Log.i("Click", "Despues de crear builder");

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {

                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }


}
