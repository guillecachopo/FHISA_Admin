package com.example.guill.fhisa_admin.Mapa;

import android.app.Activity;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Objetos.BaseOperativa;
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
     * Método al que se entrará cuando se haga click en Marcar BaseOperativa
     */
    public void accionBaseOperativa(final MapsFragment mapsFragment) {
        infoDialogMarcarBaseOperativa(mapsFragment);
        mapsFragment.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latlng) {
                crearBaseOperativa(latlng, mapsFragment);
                mapsFragment.mMap.setOnMapClickListener(null); //Para que no salga continuamente el dialogo para definir una zona
            }
        });
    }

    /**
     * Método al que se entrará cuando se haga click en Borrar BaseOperativa
     */
    public void accionBorrarBaseOperativa(final MapsFragment mapsFragment) {
        infoDialogBorrarBaseOperativa(mapsFragment);
        mapsFragment.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                borrarBaseOperativa(latLng, mapsFragment);
                mapsFragment.mMap.setOnMapClickListener(null);
            }
        });
    }

    /**
     * Método que muestra que se entrará a configurar una Base Operativa
     */
    public void infoDialogMarcarBaseOperativa(MapsFragment mapsFragment) {

        new AlertDialog.Builder(mapsFragment.getContext())
                .setTitle("Creación de base operativa(CANTERA)")
                .setMessage("Está a punto de configurar una base operativa. " +
                        "Cuando un camión se encuentre dentro de la base operativa, no se recibirán alertas. " +
                        "Marque el punto central de la base.")
                .setPositiveButton("ENTENDIDO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                }).show();
    }

    /**
     * Método que muestra que se eliminará una Base Operativa
     */
    public void infoDialogBorrarBaseOperativa(MapsFragment mapsFragment) {
        new AlertDialog.Builder(mapsFragment.getContext())
                .setTitle("Borrado de una base operativa")
                .setMessage("Parar borrar una base operativa, haga click en ella.")
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
    public void crearBaseOperativa(final LatLng latlng, final MapsFragment mapsFragment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mapsFragment.getContext());
        LayoutInflater inflater = mapsFragment.getLayoutInflater(mapsFragment.getArguments());
        final View dialogView = inflater.inflate(R.layout.dialog_area, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etArea);
        final EditText nombreArea = (EditText) dialogView.findViewById(R.id.etNombreArea);

        dialogBuilder.setTitle("Selección del area de la base operativa");
        dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nombreIntroducido = nombreArea.getText().toString();
                String radioIntroducido = edt.getText().toString();
                if (radioIntroducido.equals("") || nombreIntroducido.equals("")) {
                    Toast.makeText(mapsFragment.getContext(), "No se ha introducido un valor válido",
                            Toast.LENGTH_SHORT).show();
                    crearBaseOperativa(latlng, mapsFragment);
                }
                else {
                    long distancia = Long.parseLong(edt.getText().toString());
                    BaseOperativa baseOperativa = new BaseOperativa(String.valueOf(latlng.latitude), latlng.latitude,
                            latlng.longitude, (int) distancia);

                    baseOperativa.setIdentificador(nombreIntroducido);

                    mapsFragment.areasRef.child(baseOperativa.getIdentificador()).setValue(baseOperativa);
                    mapsFragment.listaBasesOperativas.add(baseOperativa);

                    Circle circle = dibujarCirculo(baseOperativa, mapsFragment.mMap);
                    mapsFragment.listaCirculos.add(circle);
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
    public void borrarBaseOperativa(LatLng latitudlongitud, MapsFragment mapsFragment) {
        for (int i = 0; i < mapsFragment.listaCirculos.size(); i++) {

            LatLng center = mapsFragment.listaCirculos.get(i).getCenter();
            double radius = mapsFragment.listaCirculos.get(i).getRadius();
            final BaseOperativa baseOperativaBorrar = new BaseOperativa(center.latitude, center.longitude, (int) radius);
            float[] distance = new float[1];
            Location.distanceBetween(latitudlongitud.latitude, latitudlongitud.longitude,
                    baseOperativaBorrar.getLatitud(), baseOperativaBorrar.getLongitud(), distance);
            boolean clicked = distance[0] < radius;

            if (clicked) {
                mapsFragment.listaCirculos.get(i).remove();
                mapsFragment.listaCirculos.remove(i);
                mapsFragment.listaBasesOperativas.remove(i);

                mapsFragment.areasRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getValue().getClass();
                            BaseOperativa baseOperativaFirebase = snapshot.getValue(BaseOperativa.class);
                            if (baseOperativaFirebase.getLatitud() == baseOperativaBorrar.getLatitud() &&
                                    baseOperativaFirebase.getLongitud() == baseOperativaBorrar.getLongitud() &&
                                    baseOperativaFirebase.getDistancia() == baseOperativaBorrar.getDistancia())
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
    public void inicializarBasesOperativas(final MapsFragment mapsFragment) {
        mapsFragment.areasRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String idArea = snapshot.getValue(BaseOperativa.class).getIdentificador();
                    BaseOperativa baseOperativa = null;
                    if(!mapsFragment.listaIdsAreas.contains(idArea)) {
                        baseOperativa = snapshot.getValue(BaseOperativa.class);
                        mapsFragment.listaIdsAreas.add(idArea);
                        mapsFragment.listaBasesOperativas.add(baseOperativa);
                    }
                }

                //Dibujamos todos las areas que tenemos en firebase
                for (int i = 0; i< mapsFragment.listaBasesOperativas.size(); i++) {
                    Circle circle = dibujarCirculo(mapsFragment.listaBasesOperativas.get(i), mapsFragment.mMap);
                    mapsFragment.listaCirculos.add(circle);
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
    public void modificarBaseOperativa(final BaseOperativa baseOperativa, final Activity activity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity.getApplicationContext());
        final View dialogView = activity.getLayoutInflater().inflate(R.layout.dialog_area, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.etArea);
        final EditText nombreArea = (EditText) dialogView.findViewById(R.id.etNombreArea);

        dialogBuilder.setTitle("Modificación de base operativa");
        dialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nombreIntroducido = nombreArea.getText().toString();
                String radioIntroducido = edt.getText().toString();
                if (radioIntroducido.equals("") || nombreIntroducido.equals("")) {
                    Toast.makeText(activity.getApplicationContext(), "No se ha introducido un valor válido",
                            Toast.LENGTH_SHORT).show();
                }
                else {

                    baseOperativa.setIdentificador(nombreIntroducido);
                    baseOperativa.setDistancia(Integer.parseInt(radioIntroducido));

                    DatabaseReference areasRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.AREAS_REFERENCE);
                    areasRef.child(baseOperativa.getIdentificador()).setValue(baseOperativa);

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
     * @param baseOperativa
     * @return
     */
    private Circle dibujarCirculo(BaseOperativa baseOperativa, GoogleMap mMap) {
        Circle circulo = mMap.addCircle(new CircleOptions()
                .center(new LatLng(baseOperativa.getLatitud(), baseOperativa.getLongitud()))
                .radius(baseOperativa.getDistancia())
                .strokeColor(0x70FE2E2E)
                .fillColor(0x552E86C1));
        return circulo;
    }

}
