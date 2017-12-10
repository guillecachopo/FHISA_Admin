package com.example.guill.fhisa_admin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guill.fhisa_admin.Adapter.Adapter;
import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by guill on 28/11/2017.
 */

public class RecyclerViewFragment2 extends Fragment {

    /**
     * RecyclerView que se va a utilizar
     */
    private RecyclerView rvCamiones;

    /**
     * Lista que contiene los camiones
     */
    ArrayList<Camion> listaCamiones;

    /**
     * Lista que contiene las Ids de los camiones
     */
    ArrayList<String> listaIdsCamiones;

    /**
     * Adaptador del RecyclerView
     */
    Adapter adaptador;

    /**
     * Base de datos Firebase a utilizar
     */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Referencia de los camiones en Firebase
     */
    final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflar layout con el fragment: (asignar la clase de java al layout y ahora toda la clase pertenece al objeto v)
        View v = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        rvCamiones = (RecyclerView) v.findViewById(R.id.rvCamiones);
        setLinearLayout(rvCamiones);
        inicializarListaCamiones(rvCamiones, camionesRef);
        inicializarAdaptador(rvCamiones);
        return v;
    }

    /**
     * Método encargado de crear el LinearLayout para el RecyclerView
     * @param recyclerView
     */
    private void setLinearLayout(RecyclerView recyclerView) {
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm); //Para q el RecyclerView se comporte como un LinearLayout
    }

    /**
     * Método encargfado de inicializar el adaptador del RecyclerView
     * @param recyclerView
     */
    private void inicializarAdaptador(RecyclerView recyclerView) {
        //Crea un objeto de contacto adaptador y le pasa la lista que tenemos para hacer internamente lo configurado en esa activity
        adaptador = new Adapter(listaCamiones, getActivity());
        recyclerView.setAdapter(adaptador);
    }

    /**
     * Método encargado de escuchar a Firebase con las actualizaciones de los camiones
     * @param rvCamiones
     * @param camionesRef
     */
    public void inicializarListaCamiones(RecyclerView rvCamiones, DatabaseReference camionesRef) {
        listaCamiones = new ArrayList<Camion>();
        listaIdsCamiones = new ArrayList<String>();

        camionesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Camion camion = null;
                if (!listaIdsCamiones.contains(id)) { //Si la ID no está en la lista añadimos el camion
                    camion = new Camion(id);
                    listaIdsCamiones.add(id);
                    listaCamiones.add(camion);
                }
                else {
                    for (int i = 0; i < listaCamiones.size(); i++)
                        if (listaCamiones.get(i).getId().compareTo(id) == 0) {
                            camion = listaCamiones.get(i);
                            camion.clearPosiciones();
                            camion.clearHoras();
                        }
                }

                final Camion camionPos = camion;
                actualizarCamion(camionPos, dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Camion camion = null;
                if (!listaIdsCamiones.contains(id)) { //Si la ID no está en la lista añadimos el camion
                    camion = new Camion(id);
                    listaIdsCamiones.add(id);
                    listaCamiones.add(camion);
                }
                else {
                    for (int i = 0; i < listaCamiones.size(); i++)
                        if (listaCamiones.get(i).getId().compareTo(id) == 0) {
                            camion = listaCamiones.get(i);
                        }
                }

                final Camion camionPos = camion;
                actualizarCamion(camionPos, dataSnapshot);
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

    /**
     * Método encargado de actualizar las posiciones de cada camión en el mapa
     * @param camionPos
     * @param dataSnapshot
     */
    private void actualizarCamion(final Camion camionPos, DataSnapshot dataSnapshot) {

        Query q = dataSnapshot.child("rutas").child("ruta_actual").getRef().orderByKey().limitToLast(1);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Posicion posicion = child.getValue(Posicion.class);
                    camionPos.setPosiciones(posicion);
                    long time = camionPos.getUltimaPosicion().getTime();
                    camionPos.setHoras(time);
                }
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


}
