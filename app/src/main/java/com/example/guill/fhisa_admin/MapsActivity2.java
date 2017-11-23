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
import android.widget.Toast;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class MapsActivity2 extends Fragment implements OnMapReadyCallback {

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

    ArrayList<Area> areasList;
    ArrayList<String> IDsAreas;
    String idArea;
    ArrayList<Circle> circleList;

    String idIrMarcador;
    boolean ir;

    ImageView buttonMapType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.activity_maps, container, false);

        buttonMapType = (ImageView) mView.findViewById(R.id.btnTipoMapa);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        mMap = googleMap;

        camionesList = new ArrayList<>();
        IDs = new ArrayList<>();
        coloresLista = new ArrayList<>();

        buttonMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapTypeSelectorDialog();
            }
        });

        //final LatLng oviedo = new LatLng(43.3579649511212,-5.8733862770);
        final LatLng oviedo = new LatLng(43.458979, -5.850589);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(oviedo)); //Ponemos el mapa inicialmente centrado en el centro de asturias
        CameraUpdate cuOviedo = CameraUpdateFactory.newLatLngZoom(oviedo, 10); //Que el mapa no empiece con asturias muy lejos
        mMap.animateCamera(cuOviedo);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int tipomapa = preferences.getInt("tipomapa", 2);
        mMap.setMapType(tipomapa);

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
                    if (!IDsAreas.contains(idArea)) {
                        area = snapshot.getValue(Area.class);
                        IDsAreas.add(idArea);
                        areasList.add(area);
                        Log.i("Areas", "Area: " + area.getDistancia());

                    }
                    //LatLng latLng = new LatLng(area.getLatitud(), area.getLongitud());
                }

                //Dibujamos todos las areas que tenemos en firebase
                for (int i = 0; i < areasList.size(); i++) {
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(areasList.get(i).getLatitud(), areasList.get(i).getLongitud()))
                            .radius(areasList.get(i).getDistancia())
                            .strokeColor(0x70FE2E2E)
                            .fillColor(0x552E86C1));
                    circleList.add(circle);
                }
                Log.i("Areas y Circulos", "Areas: " + areasList.size() + ", Circulos: " + circleList.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        final SharedPreferences.Editor editor = pref.edit();

        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);
        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) { //AQUI ESTAN LAS ID

                    id = snapshot.getKey();
                    Camion camion=null;
                    Marker marker = null;
                    markerOptions = new MarkerOptions();

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
                                //camion.clearPosiciones();
                            }
                    }
                    Log.i("CAMIONES", String.valueOf(camionesList.size()));

                    Query q = snapshot.child("posiciones").getRef().orderByKey().limitToLast(1);

                    final Camion camionPosiciones = camion;
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Posicion posicion = child.getValue(Posicion.class);
                                camionPosiciones.setPosiciones(posicion);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    camion.setPosicionesList(camionPosiciones.getPosicionesList());
                    Log.i("Posiciones", camion.getId() + ": " + camion.getPosicionesList().size());

                    for (int i=0; i<camionesList.size(); i++) { //Recorremos la lista de camiones para pintar sus rutas,
                        final Camion pintado = camionesList.get(i);
                        // Integer color = coloresLista.get(i); //Cojo un color de la lista de colores random

                        if (pintado.getPosicionesList().size() > 0 ) {
                            String nombre = pref.getString(pintado.getId() + "-nombreCamion", pintado.getId());
                            markerOptions = new MarkerOptions()
                                    .position(new LatLng(pintado.getUltimaPosicion().getLatitude(), pintado.getUltimaPosicion().getLongitude()))
                                    .snippet(ultimaHoraCamion(pintado))
                                    //   .title(camionesList.get(i).getId())
                                    .title(nombre);

                            marcador = mMap.addMarker(markerOptions);
                        }
                    }



                } //for snapshot (Iterador donde estan las IDs)
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }


    /**
     * Método encargado de dibujar una ruta de un camión
     * @param camionPintar
     * @param randomColor
     */
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

    /**
     * Método encargado de generar un color aleatorio
     * @return Color formato int aleatorio
     */
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

    /**
     * AlertDialog para la elección del tipo de mapa. También guarda la elección en SharedPreferences
     */
    private void showMapTypeSelectorDialog() {

        Log.i("Click", "Dentro de showMapTypeSelectorDialog");
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Selecciona el tipo de mapa";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(fDialogTitle);
        builder.create();
        Log.i("Click", "Despues de crear builder");

        final float[] tipoMapa = new float[1];

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor edit = preferences.edit();

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
                                tipoMapa[0] = 2;
                                edit.putInt("tipomapa", 2);
                                edit.apply();
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                edit.putInt("tipomapa", 3);
                                edit.apply();
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                edit.putInt("tipomapa", 4);
                                edit.apply();
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                edit.putInt("tipomapa", 1);
                                edit.apply();
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }



    /**
     * Método que muestra que se entrará a configurar una Base Operativa
     */
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

    /**
     * Método que muestra que se eliminará una Base Operativa
     */
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
    /**
     * Método encargado de mostrar un AlertDialog para la elección de la Base Operativa. Guarda el
     * area operativa en Firebase y genera un círculo en el area elegida.
     * @param latlng
     */
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
                String introducido = edt.getText().toString();
                if (introducido.equals("")) {
                    Toast.makeText(getContext(), "No se ha introducido un valor válido", Toast.LENGTH_SHORT).show();
                    AreaSegura(latlng);
                }
                else {
                    long distancia = Long.parseLong(edt.getText().toString());
                    metrosArea = distancia;
                    Log.i("distancia", String.valueOf(metrosArea));
                    LatLng randomLatLng = getRandomLocation(latlng, (int) distancia);
                    Log.i("PRUEBA", "Click en: " + latlng.latitude + ", " + latlng.longitude + ", Radio: " + distancia + ", Random: " + randomLatLng.latitude + ", " + randomLatLng.longitude);

                    Area area = new Area(String.valueOf(latlng.latitude), latlng.latitude, latlng.longitude, (int) distancia);

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
                }
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


    /**
     * Método encargado de borrar una Base Operativa. Esta se borrará de Firebase y eliminará
     * su circunferencia asociada.
     * @param latitudlongitud
     */
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


    /**
     * Método encargado de comprobar si un camión se encuentra dentro de alguna de las Bases Operativas
     * @param camionComprobar
     * @param listaCirculos
     * @return Retorna true si el camión stá dentro y false si está fuera
     */

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

    /**
     * Método encargado de hacer geolocalización inversa a la última posición recibida de un camión
     * @param camionGeo
     * @return Retorna la dirección de la última posición del camión
     */
    public String geolocalizacionInversa(Camion camionGeo){
        Geocoder geocoder = null;
        Address direccion = null;
        if (getContext()!=null) {
            try {
                geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(camionGeo.getUltimaPosicion().getLatitude(), camionGeo.getUltimaPosicion().getLongitude(), 1);
                if (!list.isEmpty()) {
                    direccion = list.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return direccion.getAddressLine(0);
        }
        else {
            return "Cargando dirección...";
        }
    }

    /**
     * Método encargado de calcular la hora equivalente a la última posición recibida de un camión
     * @param camionH
     * @return Retorna la hora en formato String
     */
    public String ultimaHoraCamion(Camion camionH) {
        long horaLong = camionH.getUltimaPosicion().getTime();
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(horaLong);
        String hora = format.format(date);
        return hora;
    }

    /**
     * Método encargado de calcular una posición aleatoria dentro de la circunferencia de radio indicado
     * @param point
     * @param radius
     * @return Retorna una posicion LatLng aleatoria.
     */
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

    public void buscadoEnMenu(Camion camionBuscado) {
        if (isAdded()) {
            Globals globals = (Globals) getActivity().getApplicationContext();
            if (globals.isIr() && globals.getId().equals(camionBuscado.getId())) {
                LatLng irLatLng = new LatLng(camionBuscado.getUltimaPosicion().getLatitude(), camionBuscado.getUltimaPosicion().getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(irLatLng, 14)); //Ponemos el mapa inicialmente centrado en el centro de asturias
                //CameraUpdate cuIr = CameraUpdateFactory.newLatLngZoom(irLatLng, 13); //Que el mapa no empiece con asturias muy lejos
                //mMap.animateCamera(cuIr);
                globals.setIr(false);
            }
        }
    }
}