package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by guill on 17/10/2017.
 */

public class AdapterPosiciones extends RecyclerView.Adapter<AdapterPosiciones.CamionesViewHolder>{

    List<Camion> camiones;
    Activity activity;
    List<String> posicionesString;
    List<String> horasString;
    String id;

    public AdapterPosiciones(List<String> posicionesString, List<String> horasString, String id, Activity activity) {

        this.activity = activity;
        this.posicionesString = posicionesString;
        this.id = id;
        this.horasString = horasString;
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
        final String posicionString = posicionesString.get(position);
        String[] splitter = posicionString.split(",");
        double latitud = Double.parseDouble(splitter[0]);
        double longitud = Double.parseDouble(splitter[1]);

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final long horalong = Long.parseLong(horasString.get(position));
        Date date = new Date(horalong);
        String hora = format.format(date);

        holder.tvPosiciones.setText("Pos: " + latitud + ", " + longitud);

        //Geocoder para hacer geolocalización inversa
        Geocoder geocoder = new Geocoder(holder.tvPosiciones.getContext(), Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(latitud, longitud, 1);
            if (!list.isEmpty()) {
                Address direccion = list.get(0);
                holder.tvPosiciones.setText("Posicion: " + direccion.getAddressLine(0));
                holder.tvHoras.setText("Hora: " + hora);
            }
         } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return posicionesString.size();
    }

    public static class CamionesViewHolder extends RecyclerView.ViewHolder {

        TextView tvPosiciones, tvHoras;

        public CamionesViewHolder(View itemView) {
            super(itemView);
            tvPosiciones = itemView.findViewById(R.id.tvPosicionesIndividual);
            tvHoras = itemView.findViewById(R.id.tvHorasIndividual);
        }
    }

}