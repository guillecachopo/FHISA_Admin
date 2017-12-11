package com.example.guill.fhisa_admin.Antiguo;

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
import com.example.guill.fhisa_admin.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guill on 16/10/2017.
 */

public class RecyclerViewFragment extends Fragment {

    ArrayList<Camion> camiones;
    private RecyclerView listaCamiones;

    String id;
    double altitude;
    double latitude;
    double longitude;
    float speed;
    long time;
    List<String> IDs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        //Inflar layout con el fragment: (asignar la clase de java al layout y ahora toda la clase pertenece al objeto v)
        View v = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        listaCamiones = (RecyclerView) v.findViewById(R.id.rvCamiones);
        //Hay q definir de que forma quiero mostrar el RecyclerView -> queremos como una lista

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        //  GridLayoutManager glm = new GridLayoutManager(this, 2); //2 es el num de columnas
        listaCamiones.setLayoutManager(llm); //Para q el recycleview se comporte como un LinearLayout
        inicializarListaCamiones();
        inicializarAdaptador();

        return v;
    }

    public void inicializarListaCamiones(){

        camiones = new ArrayList<Camion>();
        IDs = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance(); //Cualquier referencia tiene que ser igual al mismo tipo pero cogiendo la instancia
        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);
        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {

                    id = snapshot.getKey();
                    Camion camion=null;

                    if (!IDs.contains(id)) {
                        camion = new Camion(id);
                        IDs.add(id);
                        camiones.add(camion);
                    }
                    else {
                        for (int i=0; i<camiones.size(); i++)
                            if (camiones.get(i).getId().compareTo(id)==0) {
                                camion = camiones.get(i);
                                camion.clearPosiciones();

                                camion.clearHoras();
                            }
                    }

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {

                            Posicion posicion = snapshot2.getValue(Posicion.class);
                            camion.setPosiciones(posicion);

                            altitude = camion.getUltimaPosicion().getAltitude();
                            latitude = camion.getUltimaPosicion().getLatitude();
                            longitude = camion.getUltimaPosicion().getLongitude();
                            speed = camion.getUltimaPosicion().getSpeed();
                            time = camion.getUltimaPosicion().getTime();
                            LatLng latlng = new LatLng(latitude, longitude);

                            camion.setHoras(time);

                        } //for snapshot2 (Iterador donde estan las posiciones)
                    } //for snapshot1 (Iterador donde esta la cadena "posiciones")

                } //for snapshot (Iterador donde estan las IDs)
                adaptador.notifyDataSetChanged();
            } //onDataChange
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //ValueEventListener

    }

    public Adapter adaptador;
    public void inicializarAdaptador(){
        //Crea un objeto de contacto adaptador y le pasa la lista que tenemos para hacer internamente lo configurado en esa activity
        adaptador = new Adapter(camiones, getActivity());
        listaCamiones.setAdapter(adaptador);
    }


/*

    @Override
    public void onResume() {
        // Recogemos las preferencias del sistema.
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

    }

    */

}