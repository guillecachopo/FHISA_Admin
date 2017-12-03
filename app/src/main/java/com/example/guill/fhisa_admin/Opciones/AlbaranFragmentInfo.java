package com.example.guill.fhisa_admin.Opciones;

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

public class AlbaranFragmentInfo extends Fragment {
    public TextView tvIdAlbaran, tvFechaAlbaran, tvHoraCargaAlbaran, tvVehiculoAlbaran,
            tvChoferAlbaran, tvLimUsoAlbaran, tvDestinoClienteAlbaran, tvDestinoCifAlbaran,
            tvDestinoObraAlbaran, tvTransportistaNombreAlbaran, tvTransportistaCifAlbaran,
            tvTransportistaDireccionAlbaran, tvTransportistaPoblacionAlbaran;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_albaran_info, container, false);

        tvIdAlbaran = (TextView) view.findViewById(R.id.tvIdAlbaran);
        tvFechaAlbaran = (TextView) view.findViewById(R.id.tvFechaAlbaran);
        tvHoraCargaAlbaran = (TextView) view.findViewById(R.id.tvHoraCargaAlbaran);
        tvVehiculoAlbaran = (TextView) view.findViewById(R.id.tvVehiculoAlbaran);
        tvChoferAlbaran = (TextView) view.findViewById(R.id.tvChoferAlbaran);
        tvLimUsoAlbaran = (TextView) view.findViewById(R.id.tvLimUsoAlbaran);
        tvDestinoClienteAlbaran = (TextView) view.findViewById(R.id.tvDestinoClienteAlbaran);
        tvDestinoCifAlbaran = (TextView) view.findViewById(R.id.tvDestinoCifAlbaran);
        tvDestinoObraAlbaran = (TextView) view.findViewById(R.id.tvDestinoObraAlbaran);
        tvTransportistaNombreAlbaran = (TextView) view.findViewById(R.id.tvTransportistaNombreAlbaran);
        tvTransportistaCifAlbaran = (TextView) view.findViewById(R.id.tvTransportistaCifAlbaran);
        tvTransportistaDireccionAlbaran = (TextView) view.findViewById(R.id.tvTransportistaDireccionAlbaran);
        tvTransportistaPoblacionAlbaran = (TextView) view.findViewById(R.id.tvTransportistaPoblacionAlbaran);

        return view;
    }

    public void setTextView (String txt) {
        this.tvIdAlbaran.setText(txt);
    }
}
