package com.example.guill.fhisa_admin;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Objetos.Area;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by guill on 21/01/2018.
 */

public class AccionesBasesOperativas {


    /**
     * Método al que se entrará cuando se haga click en Marcar Area
     */
    public void accionBaseOperativa(final MapsActivity mapsActivity) {
        infoDialogMarcarBaseOperativa(mapsActivity);
        mapsActivity.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latlng) {
                crearBaseOperativa(latlng, mapsActivity);
                mapsActivity.mMap.setOnMapClickListener(null); //Para que no salga continuamente el dialogo para definir una zona
            }
        });
    }

    /**
     * Método que muestra que se entrará a configurar una Base Operativa
     */
    public void infoDialogMarcarBaseOperativa(MapsActivity mapsActivity) {

        new AlertDialog.Builder(mapsActivity.getContext())
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
     * Método encargado de mostrar un AlertDialog para la elección de la Base Operativa. Guarda el
     * area operativa en Firebase y genera un círculo en el area elegida.
     * @param latlng
     */
    public void crearBaseOperativa(final LatLng latlng, final MapsActivity mapsActivity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mapsActivity.getContext());
        LayoutInflater inflater = mapsActivity.getLayoutInflater(mapsActivity.getArguments());
        final View dialogView = inflater.inflate(R.layout.dialog_area, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etArea);

        dialogBuilder.setTitle("Selección de area");
        dialogBuilder.setMessage("Elija en metros el radio del area.");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String introducido = edt.getText().toString();
                if (introducido.equals("")) {
                    Toast.makeText(mapsActivity.getContext(), "No se ha introducido un valor válido",
                            Toast.LENGTH_SHORT).show();
                    crearBaseOperativa(latlng, mapsActivity);
                }
                else {
                    long distancia = Long.parseLong(edt.getText().toString());
                    Area area = new Area(String.valueOf(latlng.latitude), latlng.latitude,
                            latlng.longitude, (int) distancia);

                    mapsActivity.areasRef.push().setValue(area);
                    mapsActivity.listaAreas.add(area);

                    Circle circle = dibujarCirculo(area, mapsActivity.mMap);
                    mapsActivity.listaCirculos.add(circle);
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
     * Método encargado de dibujar un circulo
     * @param area
     * @return
     */
    private Circle dibujarCirculo(Area area, GoogleMap mMap) {
        Circle circulo = mMap.addCircle(new CircleOptions()
                .center(new LatLng(area.getLatitud(), area.getLongitud()))
                .radius(area.getDistancia())
                .strokeColor(0x70FE2E2E)
                .fillColor(0x552E86C1));
        return circulo;
    }

}
