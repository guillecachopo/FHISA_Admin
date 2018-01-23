package com.example.guill.fhisa_admin.OpcionesCamion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guill.fhisa_admin.R;

/**
 * Created by guill on 02/12/2017.
 */

public class AlbaranFragmentCarga extends Fragment {

    public TextView tvCantidadAlbaran, tvTipoCargaAlbaran, tvCementoTipoAlbaran,
            tvCementoMarcaAlbaran, tvCementoKgm3Albaran, tvAdiccionesTipoAlbaran, tvAdiccionesMarcaAlbaran,
            tvAdiccionesKgm3Albaran, tvAditivosTipoAlbaran, tvAditivosMarcaAlbaran,
            tvAditivosKgm3Albaran, tvRelacionAcAlbaran, tvPedidoAlbaran, tvSuministradoAlbaran,
            tvPteSumAlbaran;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_albaran_carga, container, false);

        tvCantidadAlbaran = (TextView) view.findViewById(R.id.tvCantidadAlbaran);
        tvTipoCargaAlbaran = (TextView) view.findViewById(R.id.tvTipoCargaAlbaran);
        tvCementoTipoAlbaran = (TextView) view.findViewById(R.id.tvCementoTipoAlbaran);
        tvCementoMarcaAlbaran = (TextView) view.findViewById(R.id.tvCementoMarcaAlbaran);
        tvCementoKgm3Albaran = (TextView) view.findViewById(R.id.tvCementoKgm3Albaran);
        tvAdiccionesTipoAlbaran = (TextView) view.findViewById(R.id.tvAdiccionesTipoAlbaran);
        tvAdiccionesMarcaAlbaran = (TextView) view.findViewById(R.id.tvAdiccionesMarcaAlbaran);
        tvAdiccionesKgm3Albaran = (TextView) view.findViewById(R.id.tvAdiccionesKgm3Albaran);
        tvAditivosTipoAlbaran = (TextView) view.findViewById(R.id.tvAditivosTipoAlbaran);
        tvAditivosMarcaAlbaran = (TextView) view.findViewById(R.id.tvAditivosMarcaAlbaran);
        tvAditivosKgm3Albaran = (TextView) view.findViewById(R.id.tvAditivosKgm3Albaran);
        tvRelacionAcAlbaran = (TextView) view.findViewById(R.id.tvRelacionAcAlbaran);
        tvPedidoAlbaran = (TextView) view.findViewById(R.id.tvPedidoAlbaran);
        tvSuministradoAlbaran = (TextView) view.findViewById(R.id.tvSuministradoAlbaran);
        tvPteSumAlbaran = (TextView) view.findViewById(R.id.tvPteSumAlbaran);

        return view;
    }
}
