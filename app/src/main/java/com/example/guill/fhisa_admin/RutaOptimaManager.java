package com.example.guill.fhisa_admin;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Socket.PeticionUltimoAlbaran;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by guill on 21/01/2018.
 */

public class RutaOptimaManager {

    /**
     * Obtiene el destino del ultimo albaran y pinta la ruta
     * @param camion
     */
    public void obtenerRutaOptimaDestino(MapsActivity mapsActivity, Camion camion) {
        final LatLng latlng = new LatLng(camion.getUltimaPosicion().getLatitude(), camion.getUltimaPosicion().getLongitude());
        final String latitudOrigen = String.valueOf(latlng.latitude);
        final String longitudOrigen = String.valueOf(latlng.longitude);
        final String latitudlongitudOrigen = latitudOrigen + "," + longitudOrigen;
        String latitudLongitudDestino = null;

        try {
            latitudLongitudDestino = new PeticionUltimoAlbaran(mapsActivity.getActivity()).execute(camion.getId()).get();
            Log.i("LatitudLongitud", latitudLongitudDestino);
            if (latitudLongitudDestino.compareTo("0.000000,0.000000") == 0 || latitudLongitudDestino.compareTo(",") == 0) {
                Snackbar.make(mapsActivity.getActivity().findViewById(R.id.map),
                        "No hay coordenadas de destino en el albarán", Snackbar.LENGTH_INDEFINITE)
                        .setDuration(5000)
                        .show();

            } else {
                DateTime now = new DateTime();
                try {
                    DirectionsResult results = DirectionsApi.newRequest(getGeoContext(mapsActivity.getActivity()))
                            .mode(TravelMode.DRIVING)
                            .origin(latitudlongitudOrigen)
                            .destination(latitudLongitudDestino)
                            .departureTime(now)
                            .await();

                    final Marker marcadorDestino = addMarkersToMap(results, mapsActivity.mMap, camion);
                    final Polyline rutaOptima = addPolyline(results, mapsActivity.mMap, camion);
                    mapsActivity.mPolylineMap.put(marcadorDestino.getTag().toString(), rutaOptima);


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
     * GeoContext para la API Google Directions
     * @return
     */
    public GeoApiContext getGeoContext(Activity activity) {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(activity.getString(R.string.directionsApiKey))
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
     * Dibuja la ruta destino de un camión
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




}
