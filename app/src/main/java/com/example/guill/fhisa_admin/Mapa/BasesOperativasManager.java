package com.example.guill.fhisa_admin.Mapa;

import android.app.Activity;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Objetos.Area;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by guill on 21/01/2018.
 */

public class BasesOperativasManager {


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
     * Método al que se entrará cuando se haga click en Borrar Area
     */
    public void accionBorrarBaseOperativa(final MapsActivity mapsActivity) {
        infoDialogBorrarBaseOperativa(mapsActivity);
        mapsActivity.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                borrarBaseOperativa(latLng, mapsActivity);
                mapsActivity.mMap.setOnMapClickListener(null);
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
     * Método que muestra que se eliminará una Base Operativa
     */
    public void infoDialogBorrarBaseOperativa(MapsActivity mapsActivity) {
        new AlertDialog.Builder(mapsActivity.getContext())
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
    public void crearBaseOperativa(final LatLng latlng, final MapsActivity mapsActivity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mapsActivity.getContext());
        LayoutInflater inflater = mapsActivity.getLayoutInflater(mapsActivity.getArguments());
        final View dialogView = inflater.inflate(R.layout.dialog_area, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etArea);
        final EditText nombreArea = (EditText) dialogView.findViewById(R.id.etNombreArea);

        dialogBuilder.setTitle("Selección de area");
        //dialogBuilder.setMessage("Elija en metros el radio del area.");
        dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nombreIntroducido = nombreArea.getText().toString();
                String radioIntroducido = edt.getText().toString();
                if (radioIntroducido.equals("") || nombreIntroducido.equals("")) {
                    Toast.makeText(mapsActivity.getContext(), "No se ha introducido un valor válido",
                            Toast.LENGTH_SHORT).show();
                    crearBaseOperativa(latlng, mapsActivity);
                }
                else {
                    long distancia = Long.parseLong(edt.getText().toString());
                    Area area = new Area(String.valueOf(latlng.latitude), latlng.latitude,
                            latlng.longitude, (int) distancia);

                    area.setIdentificador(nombreIntroducido);

                    mapsActivity.areasRef.child(area.getIdentificador()).setValue(area);
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
     * Método encargado de borrar una Base Operativa. Esta se borrará de Firebase y eliminará
     * su circunferencia asociada.
     * @param latitudlongitud
     */
    public void borrarBaseOperativa(LatLng latitudlongitud, MapsActivity mapsActivity) {
        for (int i = 0; i < mapsActivity.listaCirculos.size(); i++) {

            LatLng center = mapsActivity.listaCirculos.get(i).getCenter();
            double radius = mapsActivity.listaCirculos.get(i).getRadius();
            final Area areaBorrar = new Area(center.latitude, center.longitude, (int) radius);
            float[] distance = new float[1];
            Location.distanceBetween(latitudlongitud.latitude, latitudlongitud.longitude,
                    areaBorrar.getLatitud(), areaBorrar.getLongitud(), distance);
            boolean clicked = distance[0] < radius;

            if (clicked) {
                mapsActivity.listaCirculos.get(i).remove();
                mapsActivity.listaCirculos.remove(i);
                mapsActivity.listaAreas.remove(i);

                mapsActivity.areasRef.addValueEventListener(new ValueEventListener() {

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
     * Método encargado de mostrar en el mapa las areas existentes
     */
    public void inicializarBasesOperativas(final MapsActivity mapsActivity) {
        mapsActivity.areasRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String idArea = snapshot.getValue(Area.class).getIdentificador();
                    Area area = null;
                    if(!mapsActivity.listaIdsAreas.contains(idArea)) {
                        area = snapshot.getValue(Area.class);
                        mapsActivity.listaIdsAreas.add(idArea);
                        mapsActivity.listaAreas.add(area);
                    }
                    //LatLng latLng = new LatLng(area.getLatitud(), area.getLongitud());
                }

                //Dibujamos todos las areas que tenemos en firebase
                for (int i=0; i<mapsActivity.listaAreas.size(); i++) {
                    Circle circle = dibujarCirculo(mapsActivity.listaAreas.get(i), mapsActivity.mMap);
                    mapsActivity.listaCirculos.add(circle);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Método encargado de mostrar un AlertDialog para la modificación de la Base Operativa. Guarda la
     * base operativa en Firebase.
     */
    public void modificarBaseOperativa(final Area area, final Activity activity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity.getApplicationContext());
        final View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_area, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etArea);
        final EditText nombreArea = (EditText) dialogView.findViewById(R.id.etNombreArea);

        dialogBuilder.setTitle("Modificación de base operativa");
        //dialogBuilder.setMessage("Elija en metros el radio del area.");
        dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nombreIntroducido = nombreArea.getText().toString();
                String radioIntroducido = edt.getText().toString();
                if (radioIntroducido.equals("") || nombreIntroducido.equals("")) {
                    Toast.makeText(activity.getApplicationContext(), "No se ha introducido un valor válido",
                            Toast.LENGTH_SHORT).show();
                }
                else {

                    area.setIdentificador(nombreIntroducido);
                    area.setDistancia(Integer.parseInt(radioIntroducido));

                    DatabaseReference areasRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.AREAS_REFERENCE);
                    areasRef.child(area.getIdentificador()).setValue(area);

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
