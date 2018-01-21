package com.example.guill.fhisa_admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Objetos.Area;
import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.example.guill.fhisa_admin.Opciones.VehiculoActivity;
import com.example.guill.fhisa_admin.Socket.PeticionEstado;
import com.example.guill.fhisa_admin.Socket.PeticionUltimoAlbaran;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by guill on 27/11/2017.
 */

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    /**
     * Vistas del fragment
     */
    View mView;

    /**
     * Vistas del mapa
     */
    MapView mMapView;

    /**
     * Objeto mapa
     */
    GoogleMap mMap;

    /**
     * Base de datos Firebase a utilizar
     */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Referencia de las areas en Firebase
     */
    final DatabaseReference areasRef = database.getReference(FirebaseReferences.AREAS_REFERENCE);

    /**
     * Referencia de los camiones en Firebase
     */
    final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);


    /**
     * Latitud y Longitud cercana a Oviedo
     */
    final LatLng OVIEDO_LATLNG = new LatLng(43.458979, -5.850589);

    /**
     * Preferencias compartidas
     */
    SharedPreferences preferences;

    /**
     * Editor para escribir en las preferencias compartidas
     */
    SharedPreferences.Editor editor;

    /**
     * Tipos de mapas
     */
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Carretera", "Satélite", "Terreno", "Híbrido"};

    /**
     * Lista en la que se almacenan las areas
     */
    ArrayList<Area> listaAreas;

    /**
     * Lista en la que se almacenan los circulos
     */
    ArrayList<Circle> listaCirculos;

    /**
     * Lista en la que se almacenan los camiones
     */
    ArrayList<Camion> listaCamiones = new ArrayList<Camion>();
    /**
     * Lista en la que se almacenan las ID de los camiones (imeis)
     */
    ArrayList<String> listaIdsCamiones = new ArrayList<>();

    /**
     * Lista en la que se almacenan las ID de las areas
     */
    ArrayList<String> listaIdsAreas;

    /**
     * Lista en la que se almacenan colores aleatorios
     */
    ArrayList<Integer> listaColores = new ArrayList<>();

    /**
     * Booleano que indica si ir o no ir a un marcador especifico. Lo recogemos de otra activity
     */
    boolean irMarcador;

    /**
     * Almacena la id del marcador a ir. Lo recogemos de otra activity
     */
    String idIrMarcador;

    /**
     * HashMap que maneja los marcadores de un camión
     */
    public Map<String, Marker> mMarkerMap = new HashMap<>();

    /**
     * HashMap que maneja las polilineas de destino de un camión
     */
    public Map<String, Polyline> mPolylineMap = new HashMap<>();

    /**
     * HashMap que maneja las polilineas de ruta de un camión
     */
    public Map<String, Polyline> mPolylineRutaMap = new HashMap<>();

    /**
     * ProgressBar que se mostrará cuando se actualicen los estados de los camiones
     */
    public ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        listaAreas = new ArrayList<>();
        listaCirculos = new ArrayList<>();
        listaIdsAreas = new ArrayList<String>();

        irMarcador = false;
        if (getArguments()!=null) {
            idIrMarcador = getArguments().getString("idIrMarcador");
            irMarcador = getArguments().getBoolean("ir");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.activity_maps, container, false);

        ImageView btnTipoMapa = (ImageView) mView.findViewById(R.id.btnTipoMapa);
        Button btnArea = (Button) mView.findViewById(R.id.btnMarcarArea);
        Button btnBorrarArea = (Button) mView.findViewById(R.id.btnBorrarArea);
        ImageView btnRefresh = (ImageView) mView.findViewById(R.id.btnRefresh);

        progressBar = (ProgressBar) mView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Actualizando estados de los camiones....", Toast.LENGTH_SHORT).show();
                for (Camion camion : listaCamiones) {
                    Marker marcador = mMarkerMap.get(camion.getId());
                    if (marcador!=null)
                    new PeticionEstado(getActivity(), marcador, progressBar).execute(camion.getId());
                }

            }
        });

        btnTipoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialogSetTipoMapa();
            }
        });

        btnArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accionBaseOperativa();
            }
        });

        btnBorrarArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accionBorrarArea();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        inicializarMapa(mMap);
        setTipoMapaInicial(mMap);

        inicializarBasesOperativas(areasRef);

        cargarCamiones(camionesRef);
        escucharMarkerClick(mMap);

        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                addMarcadoresCamiones(mMarkerMap ,listaCamiones);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Escucha los eventos en marcadores para crear rutas a destinos
     * @param mMap
     */
    private void escucharMarkerClick(GoogleMap mMap) {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTag().toString().startsWith("destino")) {
                    mPolylineMap.get(marker.getTag().toString()).remove();
                    marker.remove();
                } else {
                    for (Camion camion : listaCamiones) {
                        String imei = camion.getId();
                        if (imei.compareTo(marker.getTag().toString()) == 0) {
                            obtenerRutaOptimaDestino(camion);
                        }
                    }
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               // marker.showInfoWindow();
                String imei = marker.getTag().toString();
                new PeticionEstado(getActivity(), marker, progressBar).execute(imei);
                return false;
            }
        });

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                if (marker.getTag().toString().startsWith("destino")) {
                    mPolylineMap.get(marker.getTag().toString()).remove();
                    marker.remove();
                } else {
                    for (Camion camion : listaCamiones) {
                        String imei = camion.getId();
                        if (imei.compareTo(marker.getTag().toString()) == 0) {
                            Intent intent = new Intent(getContext(), VehiculoActivity.class);
                            intent.putExtra("id", imei);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

    }

    /**
     * Modifica el HashMap para crear y actualizar los marcadores de los camiones
     * @param mMarkerMap
     */
    private void addMarcadoresCamiones(Map<String, Marker> mMarkerMap, ArrayList<Camion> listaCamiones) {
        for (final Camion camion : listaCamiones) {

            Log.i("DataSnapshot", camion.getId() + " posiciones: " + camion.getPosicionesList().size());
            if (camion.getPosicionesList().size() > 0) { // Todos los camiones en ruta tendrán minimo 2 posiciones
                final LatLng latlng = new LatLng(camion.getUltimaPosicion().getLatitude(), camion.getUltimaPosicion().getLongitude());
                Marker previousMarker = mMarkerMap.get(camion.getId());

                if (previousMarker != null) {
                    //previous marker exists, update position:
                    previousMarker.setPosition(latlng);
                    previousMarker.setSnippet(ultimaHoraCamion(camion));


                } else {
                    String alias = preferences.getString(camion.getId() + "-nombreCamion", camion.getId());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latlng)
                            .snippet(ultimaHoraCamion(camion))
                            .title(alias);
                    final Marker marcador = mMap.addMarker(markerOptions);
                    marcador.setTag(camion.getId());
                    //put this new marker in the HashMap:
                    mMarkerMap.put(camion.getId(), marcador);

                    buscadoEnMenu(camion); //Se entra aquí si se ha buscado el camión desde las opciones

                }

                Polyline previousPolyline = mPolylineRutaMap.get(camion.getId());
                if (previousPolyline != null) { //Existe polyline
                    actualizarTrazoRuta(camion);
                } else { //No hay polyline
                    trazarRuta(camion);
                }
            }

            if (camion.getPosicionesList().size() == 0) borrarTrazoRuta(camion);
        }
    }

    private void borrarTrazoRuta(Camion camionBorrar) {
        Polyline polylineBorrar = mPolylineRutaMap.get(camionBorrar.getId());
        if (polylineBorrar != null) {
            polylineBorrar.remove();
            mPolylineRutaMap.put(camionBorrar.getId(), null);
        }
    }

    private void actualizarTrazoRuta(Camion camionPintar) {
        List<LatLng> latlngs = new ArrayList<>();
        int colorRuta = preferences.getInt(camionPintar.getId()+"-color", Color.RED);
        for (Posicion posicion :camionPintar.getPosicionesList()) {
            LatLng latlng = new LatLng(posicion.getLatitude(), posicion.getLongitude());
            latlngs.add(latlng);
        }
        Polyline previousPolyline = mPolylineRutaMap.get(camionPintar.getId());
        previousPolyline.setPoints(latlngs);
        previousPolyline.setColor(colorRuta);
    }

    private void trazarRuta(Camion camionPintar) {
        boolean dibujar = preferences.getBoolean("cbxAddPolylines", false);
        int colorRuta = preferences.getInt(camionPintar.getId()+"-color", Color.RED);

        if (dibujar) {

            List<LatLng> latlngs = new ArrayList<>();
            for (Posicion posicion :camionPintar.getPosicionesList()) {
                LatLng latlng = new LatLng(posicion.getLatitude(), posicion.getLongitude());
                latlngs.add(latlng);
            }
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(colorRuta)
                    .width(10);
            Polyline polyline = mMap.addPolyline(polylineOptions);
            polyline.setPoints(latlngs);
            mPolylineRutaMap.put(camionPintar.getId(), polyline);
        }

    }


    /**
     * Método encargado de mostrar los camiones en el mapa
     * @param camionesRef
     */
    public void cargarCamiones(DatabaseReference camionesRef) {

        camionesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Camion camion = crearCamion(id);
                addPosicionesCamion(camion, dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Camion camion = crearCamion(id);
                final Camion camionPos = camion;
                actualizarCamion(camionPos, dataSnapshot);
            } //Id


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
     * Actualiza las posiciones de un camión ya existente
     * @param camionPos
     * @param dataSnapshot
     */
    private void actualizarCamion(final Camion camionPos, DataSnapshot dataSnapshot) {
        //camionPos no tiene ninguna posición. Pero tiene un ID que nos sirve para actualizarlo en la listaCamiones

        //Un camion que estaba en ruta y ya no entrará en onChildChanged una vez
        int childNumber = (int) dataSnapshot.child("rutas").getChildrenCount(); //ruta_actual + rutas_completadas (?)
        Log.i("DataSnapshot", camionPos.getId() +": " + childNumber);

        if (dataSnapshot.child("rutas").hasChild("ruta_actual")) {
            //Está en ruta
            Query q = dataSnapshot.child("rutas").child("ruta_actual").getRef().orderByKey().limitToLast(1);

            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Posicion posicion = child.getValue(Posicion.class);

                        for (Camion camionRefresh : listaCamiones) {
                            if (camionRefresh.getId().compareTo(camionPos.getId()) == 0) {
                                camionRefresh.setPosiciones(posicion); //setPosiciones añade una posición.
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            //No está en ruta
            for (Camion camionBorrar : listaCamiones) {
                if (camionBorrar.getId().compareTo(camionPos.getId()) == 0) {
                    camionBorrar.clearPosiciones();
                }
            }
        }

    }

    /**
     * Método empleado para la creación del camion en childAdded y childChanged
     * @param id
     * @return
     */
    private Camion crearCamion(String id) {
        Camion camion = null;
        if (!listaIdsCamiones.contains(id)) { //Si la ID no está en la lista añadimos el camion
            camion = new Camion(id);
            listaIdsCamiones.add(id);
            listaCamiones.add(camion);
            int randomColor = generaColorRandom(); //Genero un color aleatorio para cada camion
            listaColores.add(randomColor); //Añado el color aleatorio a una lista

        }
        else {
            for (int i = 0; i < listaCamiones.size(); i++)
                if (listaCamiones.get(i).getId().compareTo(id) == 0) {
                    camion = listaCamiones.get(i);
                    //camion.clearPosiciones();
                }
        }
        return camion;
    }

    /**
     * Método encargado de actualizar las posiciones de cada camión en el mapa
     * @param camionPos
     * @param dataSnapshot
     */
    private void addPosicionesCamion(final Camion camionPos, final DataSnapshot dataSnapshot) {
        final ArrayList<Posicion> listaPosiciones = new ArrayList<>();

        boolean existeRutaActual = false;

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                if (snapshot2.getKey().equals("ruta_actual")) {
                    existeRutaActual = true;
                    for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                        Posicion posicion = snapshot3.getValue(Posicion.class);
                        listaPosiciones.add(posicion);
                    }
                }
                if (!existeRutaActual) { //El camion no tiene ruta actual, pero hay que mostrarlo tambien en el mapa

                    dataSnapshot.child("rutas").child("rutas_completadas").getRef().limitToLast(1)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long ultimoValor = dataSnapshot.getChildrenCount();
                            ArrayList<Posicion> ultimaPosicion = new ArrayList<Posicion>();
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                Posicion posicion = snapshot1.getValue(Posicion.class);
                                ultimaPosicion.add(posicion); //antes listaPosiciones.add(posicion);
                            }
                            //Coge todas las de su ultima ruta completada, le seteamos solo la última
                            listaPosiciones.add(ultimaPosicion.get(ultimaPosicion.size()-1));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    Query queryRuta_completada = dataSnapshot.child("rutas")
                            .child("rutas_completadas").getRef().orderByKey().limitToLast(1);
                    queryRuta_completada.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            long ultimoValor = dataSnapshot.getChildrenCount();
                            ArrayList<Posicion> ultimaPosicion = new ArrayList<Posicion>();
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                Posicion posicion = snapshot1.getValue(Posicion.class);
                                ultimaPosicion.add(posicion); //antes listaPosiciones.add(posicion);
                            }
                            //Coge todas las de su ultima ruta completada, le seteamos solo la última
                            listaPosiciones.add(ultimaPosicion.get(ultimaPosicion.size()-1));
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
            }
        }


        camionPos.setPosicionesList(listaPosiciones);
        //Ahora tendria que actualizar listaCamiones:
        for (Camion camionRefresh : listaCamiones) {
            if (camionRefresh.getId().compareTo(camionPos.getId())==0) {
                camionRefresh.setPosicionesList(camionPos.getPosicionesList()); //o camionRefresh = camion;
            }
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
     * Método para inicializar el mapa en una posicion predeterminada
     * @param mMap
     */
    private void inicializarMapa(GoogleMap mMap) {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(this.OVIEDO_LATLNG)); //Ponemos el mapa inicialmente centrado en el centro de asturias
        CameraUpdate cuOviedo = CameraUpdateFactory.newLatLngZoom(this.OVIEDO_LATLNG, 10); //Que el mapa no empiece con asturias muy lejos
        mMap.animateCamera(cuOviedo);
    }

    /**
     * Método para inicializar el tipo de mapa
     * @param mMap
     */
    private void setTipoMapaInicial(GoogleMap mMap) {
        int tipomapa = this.preferences.getInt("tipomapa", 2);
        this.mMap.setMapType(tipomapa);
    }

    /**
     * Método al que se entrará cuando se haga click en Marcar Area
     */
    public void accionBaseOperativa() {
        infoDialogMarcarBaseOperativa();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latlng) {
                crearBaseOperativa(latlng);
                mMap.setOnMapClickListener(null); //Para que no salga continuamente el dialogo para definir una zona
            }
        });
    }

    /**
     * Método al que se entrará cuando se haga click en Borrar Area
     */
    public void accionBorrarArea() {
        infoDialogBorrarArea();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                borrarBaseOperativa(latLng);
                mMap.setOnMapClickListener(null);
            }
        });
    }


    /**
     * AlertDialog para la elección del tipo de mapa. También guarda la elección en SharedPreferences
     */
    public void infoDialogSetTipoMapa() {

        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Selecciona el tipo de mapa";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(fDialogTitle);
        builder.create();

        final float[] tipoMapa = new float[1];

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
                                editor.putInt("tipomapa", 2);
                                editor.apply();
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                editor.putInt("tipomapa", 3);
                                editor.apply();
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                editor.putInt("tipomapa", 4);
                                editor.apply();
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                editor.putInt("tipomapa", 1);
                                editor.apply();
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
    public void infoDialogMarcarBaseOperativa() {

        new AlertDialog.Builder(getContext())
                .setTitle("Creación de zona libre de notificaciones (CANTERA)")
                .setMessage("Está a punto de configurar un area segura libre de notificaciones. " +
                        "Cuando un camión se encuentre dentro del area, no se recibirán alertas. " +
                        "Marque el punto central del area.")
                .setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * Método que muestra que se eliminará una Base Operativa
     */
    public void infoDialogBorrarArea() {
        new AlertDialog.Builder(getContext())
                .setTitle("Borrado de zona libre de notificaciones (CANTERA)")
                .setMessage("Parar borrar una zona, haga click en ella.")
                .setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * Método encargado de mostrar un AlertDialog para la elección de la Base Operativa. Guarda el
     * area operativa en Firebase y genera un círculo en el area elegida.
     * @param latlng
     */
    public void crearBaseOperativa(final LatLng latlng) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater(getArguments());
        final View dialogView = inflater.inflate(R.layout.dialog_area, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etArea);

        dialogBuilder.setTitle("Selección de area");
        dialogBuilder.setMessage("Elija en metros el radio del area.");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String introducido = edt.getText().toString();
                if (introducido.equals("")) {
                    Toast.makeText(getContext(), "No se ha introducido un valor válido",
                            Toast.LENGTH_SHORT).show();
                    crearBaseOperativa(latlng);
                }
                else {
                    long distancia = Long.parseLong(edt.getText().toString());
                    Area area = new Area(String.valueOf(latlng.latitude), latlng.latitude,
                            latlng.longitude, (int) distancia);

                    areasRef.push().setValue(area);
                    listaAreas.add(area);

                    Circle circle = dibujarCirculo(area);
                    listaCirculos.add(circle);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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
    public void borrarBaseOperativa(LatLng latitudlongitud) {
        for (int i = 0; i < listaCirculos.size(); i++) {

            LatLng center = listaCirculos.get(i).getCenter();
            double radius = listaCirculos.get(i).getRadius();
            final Area areaBorrar = new Area(center.latitude, center.longitude, (int) radius);
            float[] distance = new float[1];
            Location.distanceBetween(latitudlongitud.latitude, latitudlongitud.longitude,
                    areaBorrar.getLatitud(), areaBorrar.getLongitud(), distance);
            boolean clicked = distance[0] < radius;

            if (clicked) {
                listaCirculos.get(i).remove();
                listaCirculos.remove(i);
                listaAreas.remove(i);

                areasRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getValue().getClass();
                            Area areaFirebase = snapshot.getValue(Area.class);
                            if (areaFirebase.getLatitud() == areaBorrar.getLatitud() &&
                                    areaFirebase.getLongitud() == areaBorrar.getLongitud() &&
                                    areaFirebase.getDistancia() == areaBorrar.getDistancia())
                                snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }


    /**
     * Método encargado de dibujar un circulo
     * @param area
     * @return
     */
    private Circle dibujarCirculo(Area area) {
        Circle circulo = mMap.addCircle(new CircleOptions()
                .center(new LatLng(area.getLatitud(), area.getLongitud()))
                .radius(area.getDistancia())
                .strokeColor(0x70FE2E2E)
                .fillColor(0x552E86C1));
        return circulo;
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

    /**
     * Método encargado de mostrar en el mapa las areas existentes
     * @param areasRef
     */
    public void inicializarBasesOperativas(DatabaseReference areasRef) {
        areasRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String idArea = snapshot.getValue(Area.class).getIdentificador();
                    Area area = null;
                    if(!listaIdsAreas.contains(idArea)) {
                        area = snapshot.getValue(Area.class);
                        listaIdsAreas.add(idArea);
                        listaAreas.add(area);
                    }
                    //LatLng latLng = new LatLng(area.getLatitud(), area.getLongitud());
                }

                //Dibujamos todos las areas que tenemos en firebase
                for (int i=0; i<listaAreas.size(); i++) {
                    Circle circle = dibujarCirculo(listaAreas.get(i));
                    listaCirculos.add(circle);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * GeoContext para la API Google Directions
     * @return
     */
    public GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(5, TimeUnit.SECONDS)
                .setReadTimeout(5, TimeUnit.SECONDS)
                .setWriteTimeout(5, TimeUnit.SECONDS);
    }

    /**
     * Crea el marcador destino de un camión
     * @param results
     * @param mMap
     * @param camion
     * @return
     */
    public Marker addMarkersToMap(DirectionsResult results, GoogleMap mMap, Camion camion) {
        BitmapDescriptor icon2 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(icon2)
                .position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                .title(results.routes[0].legs[0].endAddress)
                .snippet(getEndLocationTitle(results));

        Marker marcadorDestino = mMap.addMarker(markerOptions);
        marcadorDestino.setTag("destino-"+camion.getId());
        return marcadorDestino;
    }

    /**
     * Snippet del marcador de la ruta destino del camion
     * @param results
     * @return
     */
    public String getEndLocationTitle(DirectionsResult results){
        return  "Tiempo estimado: "+ results.routes[0].legs[0].duration.humanReadable +
                " Distancia: " + results.routes[0].legs[0].distance.humanReadable;
    }

    /**
     * Dibuja el la ruta destino de un camión
     * @param results
     * @param mMap
     * @param camion
     * @return
     */
    public Polyline addPolyline(DirectionsResult results, GoogleMap mMap, Camion camion) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .addAll(decodedPath)
                .color(Color.BLUE));
        polyline.setWidth(12);
        polyline.setTag("destino-"+camion.getId());
        return polyline;
    }

    /**
     * Obtiene el destino del ultimo albaran y pinta la ruta
     * @param camion
     */
    public void obtenerRutaOptimaDestino(Camion camion) {
        final LatLng latlng = new LatLng(camion.getUltimaPosicion().getLatitude(), camion.getUltimaPosicion().getLongitude());
        final String latitudOrigen = String.valueOf(latlng.latitude);
        final String longitudOrigen = String.valueOf(latlng.longitude);
        final String latitudlongitudOrigen = latitudOrigen + "," + longitudOrigen;
        String latitudLongitudDestino = null;

        try {
            latitudLongitudDestino = new PeticionUltimoAlbaran(this.getActivity()).execute(camion.getId()).get();
            Log.i("LatitudLongitud", latitudLongitudDestino);
            if (latitudLongitudDestino.compareTo("0.000000,0.000000") == 0 || latitudLongitudDestino.compareTo(",") == 0) {
                    Snackbar.make(getActivity().findViewById(R.id.map),
                            "No hay coordenadas de destino en el albarán", Snackbar.LENGTH_INDEFINITE)
                            .setDuration(5000)
                            .show();

                } else {
                    DateTime now = new DateTime();
                    try {
                        DirectionsResult results = DirectionsApi.newRequest(getGeoContext())
                                .mode(TravelMode.DRIVING)
                                .origin(latitudlongitudOrigen)
                                .destination(latitudLongitudDestino)
                                .departureTime(now)
                                .await();

                        final Marker marcadorDestino = addMarkersToMap(results, mMap, camion);
                        final Polyline rutaOptima = addPolyline(results, mMap, camion);
                        mPolylineMap.put(marcadorDestino.getTag().toString(), rutaOptima);


                    } catch (ApiException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //latitudLongitudDestino = "43.463632, -5.053424";

    }

    /**
     * Se comprueba si el camión se ha buscado. Si se ha buscado, se accederá a él
     * @param camionBuscado
     */
    public void buscadoEnMenu(Camion camionBuscado) {
        if (isAdded()) {
            Globals globals = (Globals) getActivity().getApplicationContext();
            if (globals.isIr() && globals.getId().equals(camionBuscado.getId())) {
                LatLng irLatLng = new LatLng(camionBuscado.getUltimaPosicion().getLatitude(), camionBuscado.getUltimaPosicion().getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(irLatLng, 14)); //Ponemos el mapa inicialmente centrado en el centro de asturias
                globals.setIr(false);
            }
        }
    }

}