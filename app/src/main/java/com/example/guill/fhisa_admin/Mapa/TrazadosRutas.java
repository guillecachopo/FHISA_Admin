package com.example.guill.fhisa_admin.Mapa;

import android.graphics.Color;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guill on 21/01/2018.
 */

public class TrazadosRutas {

    /**
     * Método que traza la ruta actual de un camión.
     * @param mapsActivity
     * @param camionPintar
     */
    public void trazarRuta(MapsActivity mapsActivity, Camion camionPintar) {
        boolean dibujar = mapsActivity.preferences.getBoolean("cbxAddPolylines", false);
        int colorRuta = mapsActivity.preferences.getInt(camionPintar.getId()+"-color", Color.RED);

        if (dibujar) {

            List<LatLng> latlngs = new ArrayList<>();
            for (Posicion posicion :camionPintar.getPosicionesList()) {
                LatLng latlng = new LatLng(posicion.getLatitude(), posicion.getLongitude());
                latlngs.add(latlng);
            }
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(colorRuta)
                    .width(10);
            Polyline polyline = mapsActivity.mMap.addPolyline(polylineOptions);
            polyline.setPoints(latlngs);
            mapsActivity.mPolylineRutaMap.put(camionPintar.getId(), polyline);
        }
    }

    /**
     * Método que actualiza el trazado de una ruta actual de un camión.
     * @param mapsActivity
     * @param camionPintar
     */
    public void actualizarTrazoRuta(MapsActivity mapsActivity, Camion camionPintar) {
        List<LatLng> latlngs = new ArrayList<>();
        int colorRuta = mapsActivity.preferences.getInt(camionPintar.getId()+"-color", Color.RED);
        for (Posicion posicion :camionPintar.getPosicionesList()) {
            LatLng latlng = new LatLng(posicion.getLatitude(), posicion.getLongitude());
            latlngs.add(latlng);
        }
        Polyline previousPolyline = mapsActivity.mPolylineRutaMap.get(camionPintar.getId());
        previousPolyline.setPoints(latlngs);
        previousPolyline.setColor(colorRuta);
    }

    /**
     * Método que borra el trazado de una ruta actual de un camión.
     * @param camionBorrar
     */
    public void borrarTrazoRuta(MapsActivity mapsActivity, Camion camionBorrar) {
        Polyline polylineBorrar = mapsActivity.mPolylineRutaMap.get(camionBorrar.getId());
        if (polylineBorrar != null) {
            polylineBorrar.remove();
            mapsActivity.mPolylineRutaMap.put(camionBorrar.getId(), null);
        }
    }


}
