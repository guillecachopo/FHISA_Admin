package com.example.guill.fhisa_admin.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.BaseOperativa;
import com.example.guill.fhisa_admin.OpcionesMenu.ModificarBasesOperativasActivity;
import com.example.guill.fhisa_admin.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by guill on 23/01/2018.
 */

public class AdapterBasesOperativas extends RecyclerView.Adapter<AdapterBasesOperativas.BasesOperativasViewHolder> {
    List<BaseOperativa> listaBasesOperativas;
    //Activity activity;
    ModificarBasesOperativasActivity modificarBasesOperativasActivity;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static class BasesOperativasViewHolder extends RecyclerView.ViewHolder {

        TextView tvIdentificadorBaseOperativa, tvRadioBaseOperativa;
        LinearLayout cvBaseOperativa;

        public BasesOperativasViewHolder(View itemView) {
            super(itemView);
            tvIdentificadorBaseOperativa = itemView.findViewById(R.id.tvIdentificadorBaseOperativa);
            tvRadioBaseOperativa = itemView.findViewById(R.id.tvRadioBaseOperativa);
            cvBaseOperativa = itemView.findViewById(R.id.cvBaseOperativa);
        }
    }

    public AdapterBasesOperativas(ModificarBasesOperativasActivity modificarBasesOperativasActivity, List<BaseOperativa> listaBasesOperativas) {
        this.modificarBasesOperativasActivity = modificarBasesOperativasActivity;
        this.listaBasesOperativas = listaBasesOperativas;
    }

    @Override
    public int getItemCount() {
        return listaBasesOperativas.size();
    }

    @Override
    public AdapterBasesOperativas.BasesOperativasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_bases_operativas, parent, false);
        AdapterBasesOperativas.BasesOperativasViewHolder holder = new AdapterBasesOperativas.BasesOperativasViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AdapterBasesOperativas.BasesOperativasViewHolder holder, int position) {
        final BaseOperativa baseOperativa = listaBasesOperativas.get(position);

        holder.tvIdentificadorBaseOperativa.setText(baseOperativa.getIdentificador());
        holder.tvRadioBaseOperativa.setText(String.valueOf(baseOperativa.getDistancia()));

        holder.cvBaseOperativa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarBasesOperativasActivity.modificarBaseOperativa(baseOperativa, holder.tvRadioBaseOperativa);

            }
        });

    }

}
