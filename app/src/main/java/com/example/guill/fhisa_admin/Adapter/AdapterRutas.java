package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guill.fhisa_admin.OpcionesCamion.MapaRutaElegidaActivity;
import com.example.guill.fhisa_admin.R;

import java.util.List;

/**
 * Created by guill on 14/12/2017.
 */

public class AdapterRutas extends RecyclerView.Adapter<AdapterRutas.RutasViewHolder> {

    List<String> listaRutas;
    Activity activity;
    List<String> listaHorasInicioRuta;
    String imei;

    public AdapterRutas(List<String> listaRutas, List<String> listaHorasInicioRuta, Activity activity
    , String imei) {
        this.listaRutas = listaRutas;
        this.activity = activity;
        this.listaHorasInicioRuta = listaHorasInicioRuta;
        this.imei = imei;
    }

    public static class RutasViewHolder extends RecyclerView.ViewHolder {

        TextView tvRuta;
        TextView tvHoraInicioRuta;
        TextView tvHoraFinRuta;
        LinearLayout cvRuta;

        public RutasViewHolder(View itemView) {
            super(itemView);
            tvRuta = itemView.findViewById(R.id.tvRuta);
            tvHoraInicioRuta = itemView.findViewById(R.id.tvHoraInicioRuta);
            tvHoraFinRuta = itemView.findViewById(R.id.tvHoraFinRuta);
            cvRuta = itemView.findViewById(R.id.cvRuta);
        }
    }

    @Override
    public int getItemCount() {
        return listaRutas.size();
    }

    @Override
    public AdapterRutas.RutasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_rutas_camion, parent, false);
        AdapterRutas.RutasViewHolder holder = new AdapterRutas.RutasViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterRutas.RutasViewHolder holder, int position) {
        final String ruta = listaRutas.get(position);
        String[] parts = ruta.split("_");
        String fechaRuta = parts[1];
        String horaFinRuta = parts[2];

        String diaRuta = fechaRuta.substring(6);
        String mesRuta = fechaRuta.substring(4,6);
        String anioRuta = fechaRuta.substring(0,4);

        String horaFin = horaFinRuta.substring(0,2);
        String minutosFin = horaFinRuta.substring(2);

        holder.tvRuta.setText(diaRuta+"/"+mesRuta+"/"+anioRuta);
        holder.tvHoraInicioRuta.setText(listaHorasInicioRuta.get(position));
        holder.tvHoraFinRuta.setText(horaFin+":"+minutosFin);

        holder.cvRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MapaRutaElegidaActivity.class);
                intent.putExtra("ruta", ruta);
                intent.putExtra("imei", imei);
                activity.startActivity(intent);
            }
        });

    }
}
