package com.example.guill.fhisa_admin;

import android.util.Log;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Created by guill on 21/01/2018.
 */

public class CamionesMapManager {

    /**
     * Método encargado de mostrar los camiones en el mapa
     * @param camionesRef
     */
    public void cargarCamiones(final MapsActivity mapsActivity, DatabaseReference camionesRef) {

        camionesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Camion camion = crearCamion(mapsActivity, id);
                addPosicionesCamion(mapsActivity, camion, dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Camion camion = crearCamion(mapsActivity, id);
                final Camion camionPos = camion;
                actualizarCamion(mapsActivity, camionPos, dataSnapshot);
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
     * Método empleado para la creación del camion en childAdded y childChanged
     * @param id
     * @return
     */
    public Camion crearCamion(MapsActivity mapsActivity, String id) {
        Camion camion = null;
        if (!mapsActivity.listaIdsCamiones.contains(id)) { //Si la ID no está en la lista añadimos el camion
            camion = new Camion(id);
            mapsActivity.listaIdsCamiones.add(id);
            mapsActivity.listaCamiones.add(camion);
            int randomColor = mapsActivity.generaColorRandom(); //Genero un color aleatorio para cada camion
            mapsActivity.listaColores.add(randomColor); //Añado el color aleatorio a una lista

        }
        else {
            for (int i = 0; i < mapsActivity.listaCamiones.size(); i++)
                if (mapsActivity.listaCamiones.get(i).getId().compareTo(id) == 0) {
                    camion = mapsActivity.listaCamiones.get(i);
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
    public void addPosicionesCamion(MapsActivity mapsActivity, final Camion camionPos, final DataSnapshot dataSnapshot) {
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
        for (Camion camionRefresh : mapsActivity.listaCamiones) {
            if (camionRefresh.getId().compareTo(camionPos.getId())==0) {
                camionRefresh.setPosicionesList(camionPos.getPosicionesList()); //o camionRefresh = camion;
            }
        }


    }

    /**
     * Actualiza las posiciones de un camión ya existente
     * @param camionPos
     * @param dataSnapshot
     */
    public void actualizarCamion(final MapsActivity mapsActivity, final Camion camionPos, DataSnapshot dataSnapshot) {
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

                        for (Camion camionRefresh : mapsActivity.listaCamiones) {
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
            for (Camion camionBorrar : mapsActivity.listaCamiones) {
                if (camionBorrar.getId().compareTo(camionPos.getId()) == 0) {
                    camionBorrar.clearPosiciones();
                }
            }
        }

    }


}
