package com.example.guill.fhisa_admin.Mapa;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Globals;
import com.example.guill.fhisa_admin.Objetos.BaseOperativa;
import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.OpcionesCamion.DetallesVehiculoActivity;
import com.example.guill.fhisa_admin.R;
import com.example.guill.fhisa_admin.Socket.PeticionEstado;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by guill on 27/11/2017.
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback {

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
    ArrayList<BaseOperativa> listaBasesOperativas;

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

    /**
     * Gestiona las bases operativas
     */
    public BasesOperativasManager basesOperativasManager = new BasesOperativasManager();

    /**
     * Gestiona los trazados de las rutas actuales
     */
    public TrazadosRutas trazadosRutas = new TrazadosRutas();

    /**
     * Gestiona los camiones en el mapa
     */
    public CamionesMapManager camionesMapManager = new CamionesMapManager();

    /**
     * Gestiona las rutas optimas de cada camión en el mapa
     */
    public RutaOptimaManager rutaOptimaManager = new RutaOptimaManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        listaBasesOperativas = new ArrayList<>();
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
                basesOperativasManager.accionBaseOperativa(MapsFragment.this);
            }
        });

        btnBorrarArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basesOperativasManager.accionBorrarBaseOperativa(MapsFragment.this);
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

        basesOperativasManager.inicializarBasesOperativas(this);
        camionesMapManager.cargarCamiones(this, camionesRef);

        escucharMarkerClick(mMap);

        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                addMarcadoresCamiones(mMarkerMap, listaCamiones);
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
                            rutaOptimaManager.obtenerRutaOptimaDestino(MapsFragment.this, camion);
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
                            Intent intent = new Intent(getContext(), DetallesVehiculoActivity.class);
                            intent.putExtra("imei", imei);
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
    public void addMarcadoresCamiones(Map<String, Marker> mMarkerMap, ArrayList<Camion> listaCamiones) {
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
                    trazadosRutas.actualizarTrazoRuta(this, camion);
                } else { //No hay polyline
                    trazadosRutas.trazarRuta(this, camion);
                }
            }

            if (camion.getPosicionesList().size() == 0 || camion.getPosicionesList().size() == 1)
                trazadosRutas.borrarTrazoRuta(this, camion);
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
        mMap.setMapType(tipomapa);
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