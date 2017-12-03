package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Mantenimiento;
import com.example.guill.fhisa_admin.R;

import java.util.List;

/**
 * Created by guill on 03/12/2017.
 */

public class AdapterMantenimiento extends RecyclerView.Adapter<AdapterMantenimiento.MantenimientoViewHolder> {
    List<Mantenimiento> mantenimiento;
    Activity activity;

    public static class MantenimientoViewHolder extends RecyclerView.ViewHolder {

        TextView tvOperacionMantenimiento, tvCicloMantenimiento;
        LinearLayout cvMantenimiento;

        public MantenimientoViewHolder(View itemView) {
            super(itemView);
            tvOperacionMantenimiento = itemView.findViewById(R.id.tvOperacionMantenimiento);
            tvCicloMantenimiento = itemView.findViewById(R.id.tvCicloMantenimiento);
            cvMantenimiento = itemView.findViewById(R.id.cvMantenimiento);
        }
    }

    public AdapterMantenimiento(Activity activity, List<Mantenimiento> mantenimiento) {
        this.activity = activity;
        this.mantenimiento = mantenimiento;
    }

    @Override
    public int getItemCount() {
        return mantenimiento.size();
    }

    @Override
    public AdapterMantenimiento.MantenimientoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mantenimiento, parent, false);
        AdapterMantenimiento.MantenimientoViewHolder holder = new AdapterMantenimiento.MantenimientoViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterMantenimiento.MantenimientoViewHolder holder, int position) {
        Mantenimiento mant = mantenimiento.get(position);

        holder.tvOperacionMantenimiento.setText(mant.getOperacion());
        holder.tvCicloMantenimiento.setText(mant.getCiclo());

    }



}