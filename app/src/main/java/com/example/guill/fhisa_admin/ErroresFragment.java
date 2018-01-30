package com.example.guill.fhisa_admin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guill.fhisa_admin.Adapter.AdapterErrores;
import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.ErrorNotificacion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guill on 21/11/2017.
 */

public class ErroresFragment extends Fragment {

    ArrayList<Camion> camiones;
    private RecyclerView rvListaErrores;

    String imei;
    List<String> IDs;
    ArrayList<ErrorNotificacion> errorNotificacions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflar layout con el fragment: (asignar la clase de java al layout y ahora toda la clase pertenece al objeto v)
        View v = inflater.inflate(R.layout.fragment_errores, container, false);

        rvListaErrores = (RecyclerView) v.findViewById(R.id.rvErrores);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvListaErrores.setLayoutManager(llm);
        obtenerErrores();
        inicializarAdaptador();

        return v;
    }

    public void obtenerErrores(){

       // camiones = new ArrayList<Camion>();
        errorNotificacions = new ArrayList<ErrorNotificacion>();
        //IDs = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance(); //Cualquier referencia tiene que ser igual al mismo tipo pero cogiendo la instancia
        final DatabaseReference erroresRef = database.getReference(FirebaseReferences.ERRORES_REFERENCE);

        erroresRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("ERRORES", dataSnapshot.getKey());
                ErrorNotificacion error = dataSnapshot.getValue(ErrorNotificacion.class);
                Log.i("ERRORES", error.getImei());
                errorNotificacions.add(error);
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                adaptador.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                adaptador.notifyDataSetChanged();
            }
        });

        /*
        erroresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {

                    imei = snapshot.getKey();
                    //Camion camion=null;

                    if (!IDs.contains(imei)) {
                      //  camion = new Camion(imei);
                        IDs.add(imei);
                      //  camiones.add(camion);
                    }
                    else {
                        for (int i=0; i<camiones.size(); i++)
                            if (camiones.get(i).getId().compareTo(imei)==0) {
                        //        camion = camiones.get(i);
                            }
                    }

                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {

                            ErrorNotificacion error = snapshot2.getValue(ErrorNotificacion.class);
                            errorNotificacions.add(error);

                        } //for snapshot2 (Iterador donde estan los errores)
                    }

                } //for snapshot (Iterador donde estan las IDs)
                adaptador.notifyDataSetChanged();
            } //onDataChange
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //ValueEventListener
        */

    }

    public AdapterErrores adaptador;
    public void inicializarAdaptador(){
        adaptador = new AdapterErrores(errorNotificacions, getActivity());
        rvListaErrores.setAdapter(adaptador);
    }

}
