package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Consumo;
import com.example.guill.fhisa_admin.R;

import java.util.List;

/**
 * Created by guill on 30/11/2017.
 */

public class AdapterConsumos extends RecyclerView.Adapter<AdapterConsumos.ConsumosViewHolder> {
    List<Consumo> consumos;
    Activity activity;

    public static class ConsumosViewHolder extends RecyclerView.ViewHolder {

        TextView tvFechaConsumo, tvKmConsumo, tvLitrosRepostadosConsumo, tvConsumo;
        LinearLayout cvConsumo;

        public ConsumosViewHolder(View itemView) {
            super(itemView);
            tvFechaConsumo = itemView.findViewById(R.id.tvFechaConsumo);
            tvKmConsumo = itemView.findViewById(R.id.tvKmConsumo);
            tvLitrosRepostadosConsumo = itemView.findViewById(R.id.tvLitrosRepostadosConsumo);
            tvConsumo = itemView.findViewById(R.id.tvConsumo);
            cvConsumo = itemView.findViewById(R.id.cvConsumo);
        }
    }

    public AdapterConsumos(Activity activity, List<Consumo> consumos) {
        this.activity = activity;
        this.consumos = consumos;
    }

    @Override
    public int getItemCount() {
        return consumos.size();
    }

    @Override
    public AdapterConsumos.ConsumosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_consumo, parent, false);
        ConsumosViewHolder holder = new ConsumosViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterConsumos.ConsumosViewHolder holder, int position) {
        Consumo consumo = consumos.get(position);

        String fechaRepostaje = consumo.getFecha();
        String kmVehiculo = consumo.getKm();
        String litros = String.format("%.2f", Float.parseFloat(consumo.getUnidades()));
        float consumoVehiculoFloat = Float.parseFloat(consumo.getConsumo())*100;
        String consumoVehiculo = String.format("%.2f", consumoVehiculoFloat);
       // String consumoVehiculo = String.format("%.2f", Float.toString(Float.parseFloat(consumo.getConsumo())));

        holder.tvFechaConsumo.setText(fechaRepostaje);
        holder.tvLitrosRepostadosConsumo.setText(litros);
        holder.tvKmConsumo.setText(kmVehiculo);
        holder.tvConsumo.setText(consumoVehiculo);

    }



}
