package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.OpcionesCamionActivity;
import com.example.guill.fhisa_admin.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by guill on 21/09/2017.
 */


public class Adapter extends RecyclerView.Adapter<Adapter.CamionesViewHolder>{

    List<Camion> camiones;
    Activity activity;

    public Adapter(List<Camion> camiones, Activity activity) {

        this.camiones = camiones;
        this.activity = activity;
    }

    //Va a inflar el layout y lo pasara al viewholder para que el obtenga los views
    @Override
    public CamionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_camiones, parent, false);
        CamionesViewHolder holder = new CamionesViewHolder(v); //CamionesViewHolder es el metodo constructor y recibe un view (le pasamos el contacto inflado como un view)
                                                                //para q empiece a tomarlos como elementos
        return holder;
    }

    //Asciar cada elemento de la lista con cada view
    @Override
    public void onBindViewHolder(CamionesViewHolder holder, int position) {
        final Camion camion = camiones.get(position);


        final String id = camion.getId();
        String latitud = Double.toString(camion.getUltimaPosicion().getLatitude());
        String longitud = Double.toString(camion.getUltimaPosicion().getLongitude());

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(camion.getUltimaPosicion().getTime());
        String hora = format.format(date);

        //Geocoder para hacer geolocalización inversa
        Geocoder geocoder = new Geocoder(holder.tvId.getContext(),Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(camion.getUltimaPosicion().getLatitude(), camion.getUltimaPosicion().getLongitude(), 1);
            if (!list.isEmpty()) {
                Address direccion = list.get(0);
                holder.tvUltimaPosicion.setText("Ultima posicion: " + direccion.getAddressLine(0));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.tvId.setText("Identificador: " + id);
        holder.tvHora.setText("Hora: " + hora);

        final ArrayList<String> posicionesString = new ArrayList<>();
        final ArrayList<String> horasString = new ArrayList<>();

        for (int i = 0; i< camion.getPosicionesList().size(); i++) {
            posicionesString.add(String.valueOf(camion.getPosicionesList().get(i).getLatitude()) + "," + camion.getPosicionesList().get(i).getLongitude());
        }


        for (int i = 0; i < camion.getHorasList().size(); i++) {
            horasString.add(String.valueOf(camion.getPosicionesList().get(i).getTime()));
        }


        holder.cvCamion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(activity, DetallePosicionesCamion.class);
                Intent intent = new Intent(activity, OpcionesCamionActivity.class);
                intent.putExtra("id", id);
                intent.putStringArrayListExtra("posiciones", posicionesString);
                intent.putStringArrayListExtra("horas", horasString);
                activity.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return camiones.size();
    }

    public static class CamionesViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvUltimaPosicion, tvHora;
        CardView cvCamion;

        public CamionesViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvNombreCV);
            tvUltimaPosicion = itemView.findViewById(R.id.tvPosicionCV);
            tvHora = itemView.findViewById(R.id.tvHoraCV);
            cvCamion = itemView.findViewById(R.id.cvCamion);
        }
    }

}


