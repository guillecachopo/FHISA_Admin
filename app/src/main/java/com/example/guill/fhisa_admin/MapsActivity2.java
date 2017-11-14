package com.example.guill.fhisa_admin;

/**
 * Created by guill on 14/11/2017.
 */

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Area;
import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class MapsActivity2 extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    View mView;
    MapView mMapView;

    String id;

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

    ArrayList<Area> areasList;
    ArrayList<String> IDsAreas;
    String idArea;
    ArrayList<Circle> circleList;

    String idIrMarcador;
    boolean ir;
    HashMap<Marker, Camion > markerCamionMap;
    ArrayList<Marker> markerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        Button btnArea = (Button) mView.findViewById(R.id.btnMarcarArea);
        btnArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSetAreaInfoDialog();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latlng) {
                        Log.d("arg0", latlng.latitude + "," + latlng.longitude);
                        AreaSegura(latlng);
                        mMap.setOnMapClickListener(null); //Para que no salga continuamente el dialogo para definir una zona
                    }
                });
            }
        });

        Button btnBorrarArea = (Button) mView.findViewById(R.id.btnBorrarArea);
        btnBorrarArea.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDeleteAreaInfoDialog();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Log.i("PRUEBA", "Click borra: " + latLng.latitude);
                        BorrarAreaSegura(latLng);
                        mMap.setOnMapClickListener(null);
                    }
                });
            }
        });

        ir = false;
        if (getArguments()!=null) {
            idIrMarcador = getArguments().getString("idIrMarcador");
            ir = getArguments().getBoolean("ir");
        }

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

        markerList = new ArrayList<>();

        //final LatLng oviedo = new LatLng(43.3579649511212,-5.8733862770);
        final LatLng oviedo = new LatLng(43.458979, -5.850589);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(oviedo)); //Ponemos el mapa inicialmente centrado en el centro de asturias
        CameraUpdate cuOviedo = CameraUpdateFactory.newLatLngZoom(oviedo, 10); //Que el mapa no empiece con asturias muy lejos
        mMap.animateCamera(cuOviedo);

        areasList = new ArrayList<>();
        IDsAreas = new ArrayList<>();
        circleList = new ArrayList<>();
        //recuperarAreas();

        final FirebaseDatabase database = FirebaseDatabase.getInstance(); //Cualquier referencia tiene que ser igual al mismo tipo pero cogiendo la instancia

        DatabaseReference areasRef = database.getReference(FirebaseReferences.AREAS_REFERENCE);
        final DatabaseReference pintadasRef = database.getReference("pintadas");
        final DatabaseReference opcionesRef = database.getReference("opciones");


        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    idArea = snapshot.getValue(Area.class).getIdentificador();
                    Area area = null;
                    if(!IDsAreas.contains(idArea)) {
                        area = snapshot.getValue(Area.class);
                        IDsAreas.add(idArea);
                        areasList.add(area);
                        Log.i("Areas", "Area: " + area.getDistancia());

                    }
                    //LatLng latLng = new LatLng(area.getLatitud(), area.getLongitud());
                }

                //Dibujamos todos las areas que tenemos en firebase
                for (int i=0; i<areasList.size(); i++) {
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(areasList.get(i).getLatitud(), areasList.get(i).getLongitud()))
                            .radius(areasList.get(i).getDistancia())
                            .strokeColor(0x70FE2E2E)
                            .fillColor(0x552E86C1));
                    circleList.add(circle);
                }
                Log.i("Areas y Circulos", "Areas: " + areasList.size() + ", Circulos: " +circleList.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);

        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        final SharedPreferences.Editor editor = pref.edit();

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


                    if (!IDs.contains(id)) { //Si la ID no está en la lista añadimos el camion
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
                                markerList.clear();
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

                    Marker marker = placeMarker(camion);
                    markerList.add(marker);

                } //for snapshot (Iterador donde estan las IDs)


                mMap.clear(); //Limpiamos el mapa cada vez que llega una posicion para que se actualice el marcador

                markerCamionMap = new HashMap<Marker, Camion>();
                for (int i=0; i<camionesList.size(); i++){
                    markerCamionMap.put(markerList.get(i), camionesList.get(i));
                }
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Camion camion = markerCamionMap.get(marker);
                        Log.i("InfoWindow", camion.getId());
                    }
                });

                //Si el mapa se actualiza, hay que volver a pintar las areas. Además, este mMap.clear() se ejecuta al iniciarse la app, por lo que
                //los circulos se borrarían
                for (int i=0; i<areasList.size(); i++) {
                    Area area = areasList.get(i);
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(area.getLatitud(), area.getLongitud()))
                            .radius(area.getDistancia())
                            .strokeColor(0x70FE2E2E)
                            .fillColor(0x552E86C1)); //55 es el % de transparencia
                    circleList.set(i, circle);
                }

                for (int i=0; i<camionesList.size(); i++){ //Recorremos la lista de camiones para pintar sus rutas,
                    final Camion pintado = camionesList.get(i);
                    // Integer color = coloresLista.get(i); //Cojo un color de la lista de colores random

                    if (dataSnapshot.exists()) { //Sin este if, cuando se borra la DB crashea la app
                        markerOptions = new MarkerOptions()
                                .position(new LatLng(pintado.getUltimaPosicion().getLatitude(), pintado.getUltimaPosicion().getLongitude()))
                                .snippet(geolocalizacionInversa(pintado))
                                .title(camionesList.get(i).getId());

                        /*
                        marcador = mMap.addMarker(markerOptions);

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                if (pintado.getId().equals(marcador.getTitle())) {
                                    Log.i("InfoWindow", pintado.getId());
                                    ArrayList<String> posicionesString = new ArrayList<String>();
                                    for (int i = 0; i< pintado.getPosicionesList().size(); i++) {
                                        posicionesString.add(String.valueOf(pintado.getPosicionesList().get(i).getLatitude()) + "," + pintado.getPosicionesList().get(i).getLongitude());
                                    }
                                    Intent i = new Intent(getContext(), OpcionesCamionActivity.class);
                                    i.putExtra("id", marcador.getTitle());
                                    i.putStringArrayListExtra("posiciones", posicionesString);
                                    startActivity(i);
                                }
                            }
                        });
                        */

                        //PARA QUE SE ABRA LA ACTIVIDAD EN EL MARCADOR DESEADO EN OpcionesCamionActivity CUANDO EL USUARIO PULSA EN VER EN MAPA
                        opcionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //IMEI
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) { //"ir" (variable booleana)
                                        Log.i("IR", String.valueOf(snapshot1.getValue()));
                                        Log.i("IRid", String.valueOf(snapshot.getValue()));
                                        if (snapshot1.getValue().equals(true) && snapshot.getKey().equals(pintado.getId())) {
                                            LatLng irLatLng = new LatLng(pintado.getUltimaPosicion().getLatitude(), pintado.getUltimaPosicion().getLongitude());
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(irLatLng, 14)); //Ponemos el mapa inicialmente centrado en el centro de asturias
                                            //CameraUpdate cuIr = CameraUpdateFactory.newLatLngZoom(irLatLng, 13); //Que el mapa no empiece con asturias muy lejos
                                            //mMap.animateCamera(cuIr);
                                            opcionesRef.child(pintado.getId()).child("ir").setValue(false);
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                        //  dibujaRuta(pintado, color); //Dibujo la ruta del camión con un color

                        String prefColor = pref.getString("lpColorTrazo", "random");
                        // int prefColor = pref.getInt(id+"-color", Color.WHITE);
                        // Comprobamos si se desea dibujar la ruta, en caso de no
                        // estar definida la propiedad por defecto indicamos true.
                        boolean prefTrazoRuta = pref.getBoolean("cbxDibujarRuta", true);
                        //boolean prefTrazoRuta = pref.getBoolean(id+"-trazar", false);


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


                        /*
                        if (prefTrazoRuta) {
                            Integer color;
                            if (prefColor == Color.WHITE)
                                color = Color.WHITE;
                            else if (prefColor == Color.GREEN)
                                color = Color.GREEN;
                            else if (prefColor == Color.BLUE)
                                color = Color.BLUE;
                            else if (prefColor == Color.YELLOW)
                                color = Color.YELLOW;
                            else if (prefColor == Color.BLACK)
                                color = Color.BLACK;
                            else if (prefColor == Color.GRAY)
                                color = Color.GRAY;
                            else if (prefColor == Color.RED)
                                color = Color.CYAN;
                            else if (prefColor == Color.RED)
                                color = Color.RED;
                            else if (prefColor == Color.DKGRAY)
                                color = Color.DKGRAY;
                            else if (prefColor == Color.LTGRAY)
                                color = Color.LTGRAY;
                            else if (prefColor == Color.MAGENTA)
                                color = Color.MAGENTA;
                            else
                                color = coloresLista.get(i);
                                */

                            //Comprobamos si el camion está dentro del area
                            boolean dentro = camionDentroArea(pintado, circleList);
                            if (!dentro) {
                                dibujaRuta(pintado, color);

                            } else {
                                pintado.clearPosiciones();
                            }


                            Log.i("PINTADO", "Posiciones de " + pintado.getId() + ": " + pintado.getPosicionesList().size());
                        }
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

        for (int k = 0; k < camionPintar.getPosicionesList().size() - 1; k++) {

            mMap.addPolyline(new PolylineOptions().add(
                    new LatLng(camionPintar.getPosicionesList().get(k).getLatitude(), camionPintar.getPosicionesList().get(k).getLongitude()),
                    new LatLng(camionPintar.getPosicionesList().get(k + 1).getLatitude(), camionPintar.getPosicionesList().get(k + 1).getLongitude()))
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
            {"Carretera", "Satélite", "Terreno", "Híbrido"};

    private void showMapTypeSelectorDialog() {

        Log.i("Click", "Dentro de showMapTypeSelectorDialog");
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Selecciona el tipo de mapa";
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




    public void showSetAreaInfoDialog() {

        new AlertDialog.Builder(getContext())
                .setTitle("Creación de zona libre de notificaciones (CANTERA)")
                .setMessage("Está a punto de configurar un area segura libre de notificaciones. Cuando un camión se encuentre dentro del area, no se recibirán alertas. Marque el punto central del area.")
                .setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                }).show();
    }

    public void showDeleteAreaInfoDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Borrado de zona libre de notificaciones (CANTERA)")
                .setMessage("Parar borrar una zona, haga click en ella.")
                .setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                }).show();
    }

    long metrosArea;
    Circle circle;


    public void AreaSegura(final LatLng latlng) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater(getArguments());
        final View dialogView = inflater.inflate(R.layout.dialog_area, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etArea);
        final TextView tvError = (TextView) dialogView.findViewById(R.id.etErrorArea);

        dialogBuilder.setTitle("Selección de area");
        dialogBuilder.setMessage("Elija en metros el radio del area.");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                long distancia = Long.parseLong(edt.getText().toString());
                metrosArea = distancia;
                Log.i("distancia", String.valueOf(metrosArea));
                LatLng randomLatLng = getRandomLocation(latlng, (int) distancia);
                Log.i("PRUEBA", "Click en: " + latlng.latitude + ", " + latlng.longitude + ", Radio: " + distancia + ", Random: " + randomLatLng.latitude + ", " + randomLatLng.longitude);

                Area area = new Area(String.valueOf(latlng.latitude),latlng.latitude, latlng.longitude, (int) distancia);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference areasRef = database.getReference(FirebaseReferences.AREAS_REFERENCE);
                areasRef.push().setValue(area);
                areasList.add(area);

                Circle circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(area.getLatitud(), area.getLongitud()))
                        .radius(area.getDistancia())
                        .strokeColor(0x70FE2E2E)
                        .fillColor(0x552E86C1));
                circleList.add(circle);

                Log.i("AREAS Y CIRCULOS", "Areas: " + areasList.size() + ", Circulos: " + circleList.size());


            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void BorrarAreaSegura(LatLng latitudlongitud) {
        Log.i("PRUEBA", "circleList: " + circleList.size());
        for (int i=0; i<circleList.size(); i++) {

            LatLng center = circleList.get(i).getCenter();
            double radius = circleList.get(i).getRadius();
            final Area areaBorrar =  new Area(center.latitude, center.longitude, (int) radius);
            float[] distance = new float[1];
            //Location.distanceBetween(camionesList.get(i).getUltimaPosicion().getLatitude(), camionesList.get(i).getUltimaPosicion().getLongitude(), circle.getCenter().latitude,circle.getCenter().longitude,distance);
            Location.distanceBetween(latitudlongitud.latitude, latitudlongitud.longitude, areaBorrar.getLatitud(), areaBorrar.getLongitud(), distance);
            boolean clicked = distance[0] < radius;
            if (clicked) {
                circleList.get(i).remove();
                circleList.remove(i);
                areasList.remove(i);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference areasRef = database.getReference(FirebaseReferences.AREAS_REFERENCE);
                areasRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.i("Firebase", snapshot.getValue().toString());
                            snapshot.getValue().getClass();
                            Area areaFirebase = snapshot.getValue(Area.class);
                            if (areaFirebase.getLatitud() == areaBorrar.getLatitud() && areaFirebase.getLongitud() == areaBorrar.getLongitud() && areaFirebase.getDistancia() == areaBorrar.getDistancia()) snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        } //for
        Log.i("AREAS Y CIRCULOS", "Areas: " + areasList.size() + ", Circulos: " + circleList.size());
    }



    public boolean camionDentroArea(Camion camionComprobar, ArrayList<Circle> listaCirculos) {
        ArrayList<Integer> d = new ArrayList<>();
        boolean dentro = false;

        for (int i=0; i<listaCirculos.size(); i++) {
            float[] distance = new float[2];
            //Con esta linea guardamos en distance el valor de la distancia entre la ultima posicion de cada camion y el circulo
            Location.distanceBetween(camionComprobar.getUltimaPosicion().getLatitude(), camionComprobar.getUltimaPosicion().getLongitude(),
                    listaCirculos.get(i).getCenter().latitude, listaCirculos.get(i).getCenter().longitude, distance);

            if (distance[0] <= listaCirculos.get(i).getRadius()) { //Camion dentro del circulo
                // Inside The Circle
                //Log.i("Areas camion", "Camion " + camionesList.get(i).getId() + " dentro del rango");

                dentro = true;
                d.add(1);
            } else {
                // Outside The Circle
                //Log.i("PRUEBA", "Camion " + camionesList.get(i).getId() + " fuera del rango");

                dentro = false;
                d.add(0);
            }
        }
        for (int i=0; i<d.size(); i++) {
            if (d.get(i) == 1) {
                dentro = true;
            }
        }
        return dentro;
    }

    //Geocoder para hacer geolocalización inversa
    public String geolocalizacionInversa(Camion camionGeo){
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        Address direccion = null;
        try {
            List<Address> list = geocoder.getFromLocation(camionGeo.getUltimaPosicion().getLatitude(), camionGeo.getUltimaPosicion().getLongitude(), 1);
            if (!list.isEmpty()) {
                direccion = list.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return direccion.getAddressLine(0);
    }

    public LatLng getRandomLocation(LatLng point, int radius) {

        List<LatLng> randomPoints = new ArrayList<>();
        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate 10 random points
        for(int i = 0; i<10; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            randomPoints.add(randomLatLng);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre
        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
        return randomPoints.get(indexOfNearestPointToCentre);
    }

    public Marker placeMarker(Camion camion) {

        Marker m  = mMap.addMarker(new MarkerOptions()

                .position(new LatLng(camion.getUltimaPosicion().getLatitude(), camion.getUltimaPosicion().getLongitude()))
                .title(camion.getId()));

        return m;

    }



}
