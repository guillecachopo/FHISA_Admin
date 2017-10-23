package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.example.guill.fhisa_admin.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by guill on 17/10/2017.
 */

public class AdapterPosiciones extends RecyclerView.Adapter<AdapterPosiciones.CamionesViewHolder>{

    List<Camion> camiones;
    Activity activity;
    List<Posicion> posiciones;
    String id;

    public AdapterPosiciones(List<Posicion> posiciones, String id, Activity activity) {


        this.activity = activity;
        this.posiciones = posiciones;
        this.id = id;
    }

    //Va a inflar el layout y lo pasara al viewholder para que el obtenga los views
    @Override
    public CamionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_camion_individual, parent, false);
        CamionesViewHolder holder = new CamionesViewHolder(v); //CamionesViewHolder es el metodo constructor y recibe un view (le pasamos el contacto inflado como un view)
        //para q empiece a tomarlos como elementos
        return holder;
    }

    //Asciar cada elemento de la lista con cada view
    @Override
    public void onBindViewHolder(CamionesViewHolder holder, int position) {
        final Camion camion = camiones.get(position);
        final Posicion posicion = posiciones.get(position);

        Log.i("POSICIONESCAMION", String.valueOf(camion.getPosicionesList().size()));


        holder.tvId.setText("ID: " + id);

        //Geocoder para hacer geolocalización inversa
        for (int i=0; i<posiciones.size(); i++) {

            Geocoder geocoder = new Geocoder(holder.tvId.getContext(), Locale.getDefault());
            try {
                List<Address> list = geocoder.getFromLocation(posiciones.get(i).getLatitude(), posiciones.get(i).getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address direccion = list.get(0);
                    holder.tvPosiciones.setText("Pos.: " + direccion.getAddressLine(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        Log.i("Posiciones", String.valueOf(posiciones.size()));
        return posiciones.size();
    }

    public static class CamionesViewHolder extends RecyclerView.ViewHolder {

        TextView tvPosiciones, tvId;

        public CamionesViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvIDIndividual);
            tvPosiciones = itemView.findViewById(R.id.tvPosicionesIndividual);
        }
    }

}