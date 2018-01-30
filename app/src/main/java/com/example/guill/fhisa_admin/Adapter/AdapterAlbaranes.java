package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Albaran;
import com.example.guill.fhisa_admin.OpcionesCamion.DetallesAlbaranActivity;
import com.example.guill.fhisa_admin.R;

import java.util.List;

/**
 * Created by guill on 02/12/2017.
 */

public class AdapterAlbaranes extends RecyclerView.Adapter<AdapterAlbaranes.AlbaranesViewHolder> {
    List<Albaran> albaranes;
    String imei;
    Activity activity;


    public static class AlbaranesViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdAlbaran, tvDestinoAlbaran;
        CardView cvAlbaran;

        public AlbaranesViewHolder(View itemView) {
            super(itemView);
            tvIdAlbaran = itemView.findViewById(R.id.tvIdAlbaranLista);
            tvDestinoAlbaran = itemView.findViewById(R.id.tvDestinoAlbaranLista);
            cvAlbaran = itemView.findViewById(R.id.cvAlbaran);
        }
    }

    public AdapterAlbaranes(Activity activity, List<Albaran> albaranes, String imei) {
        this.activity = activity;
        this.albaranes = albaranes;
        this.imei = imei;
    }

    @Override
    public int getItemCount() {
        return albaranes.size();
    }

    @Override
    public AdapterAlbaranes.AlbaranesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_albaran, parent, false);
        AdapterAlbaranes.AlbaranesViewHolder holder = new AdapterAlbaranes.AlbaranesViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterAlbaranes.AlbaranesViewHolder holder, int position) {
        Albaran albaran = albaranes.get(position);

        final String idAlbaran = albaran.getId_albaran();
        String destinoObra = albaran.getDestino_obra();

        holder.tvIdAlbaran.setText(idAlbaran);
        holder.tvDestinoAlbaran.setText(destinoObra);

        holder.cvAlbaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DetallesAlbaranActivity.class);
                intent.putExtra("idAlbaran", idAlbaran);
                intent.putExtra("imei", imei);
                activity.startActivity(intent);
            }
        });

    }



}
