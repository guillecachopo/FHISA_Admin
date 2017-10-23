package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.R;

import java.util.ArrayList;

/**
 * Created by guill on 16/10/2017.
 */

//Tiene q heredar de RecyclerView.Adapter pq necesitamos adaptador q recibe una colecc de contactos ViewHolder
public class CamionAdapter extends RecyclerView.Adapter<CamionAdapter.CamionViewHolder>{

    ArrayList<Camion> camiones;
    Activity activity;

    public CamionAdapter(ArrayList<Camion> camiones, Activity activity){ //Contruye la lista de contactos
        this.camiones = camiones;
        this.activity = activity;
    }

    //Va a inflar el layout y lo pasara al viewholder para que el obtenga los views
    @Override
    public CamionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_camiones, parent, false);
        //La linea de codigo anterior es para asociar el layout al RecyclerView
        return new CamionViewHolder(v); //ContactoViewHolder es el metodo constructor y recibe un view (le pasamos el contacto inflado como un view)
        //para q empiece a tomarlos como elementos
    }

    //Asocia cada elemento de la lista con cada view
    @Override
    public void onBindViewHolder(CamionViewHolder contactoViewHolder, int position) {
        //Aqui pasamos la lista de contactos a cada elemento imgFoto, tvNombreCV y tvTelefonoCV
        //Le damos valor a los valores de la lista contactos
        final Camion camion = camiones.get(position); //Extrae los elementos segun la posicion
        Log.i("NUMEROCAMIONES", String.valueOf(camiones.size()));
        contactoViewHolder.tvNombreCV.setText("ID: " + camion.getId());

    }

    @Override
    public int getItemCount() {
        //return (camiones == null) ? 0 : camiones.size(); //A veces dataSnapshot.etValue() es null y da un error. Para evitarlo esta es la solucion
        return camiones.size();
    }

    //Tendra una clase estatica para poder usar sus atributos dentro de esta misma clase
    //Una clase ViewHolder nos ayuda a asociar nuestros Views a un objeto
    //Necesitamos herencia de RecyclerView.ViewHolder
    public static class CamionViewHolder extends RecyclerView.ViewHolder{

        //Aqui estaran todos mis Views
        private TextView tvNombreCV;

        public CamionViewHolder(View itemView) {
            super(itemView);
            //Asocio objetos
            tvNombreCV = (TextView) itemView.findViewById(R.id.tvNombreCV);
        }
    }

}