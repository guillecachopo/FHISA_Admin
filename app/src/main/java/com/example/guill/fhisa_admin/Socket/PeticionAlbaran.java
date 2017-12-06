package com.example.guill.fhisa_admin.Socket;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Albaran;
import com.example.guill.fhisa_admin.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.Normalizer;

/**
 * Created by guill on 02/12/2017.
 */

public class PeticionAlbaran extends AsyncTask<String, String, String> {

    public Activity activity;

    public PeticionAlbaran(Activity activity) {
        this.activity = activity;
    }

    /**
     * Before starting background thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... imeiIdAlbaran) {
        Socket socketCliente = null;
        BufferedReader entrada = null;
        PrintWriter salida = null;
        try {
            socketCliente = new Socket("89.17.197.73", 6905); //"89.17.197.73", 6905
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream(),"ISO-8859-1"));
            salida = new PrintWriter(new BufferedWriter(new
                    OutputStreamWriter(socketCliente.getOutputStream())), true);
        } catch (IOException e) {
            System.err.println("No puede establer canales de E/S para la conexión");
            System.exit(-1);
        }
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        String json, request, respuesta = null;
        json = "";
        int i = 0;

        String parts[] = imeiIdAlbaran[0].split("---");
        String imei = parts[0];
        String idAlbaran = parts[1];

        do {
            request = imei + ", albaran, "+idAlbaran;
            try {
                salida.println(request);
                respuesta = entrada.readLine();
                if (respuesta != null) json += respuesta;
                i++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (respuesta != null);

        try {
            salida.close();
            entrada.close();
            stdIn.close();
            socketCliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("Albaran", json);

        return json;
    }

    /**
     * After completing background task
     **/
    @Override
    protected void onPostExecute(String json) {

        Gson gson = new GsonBuilder().serializeNulls().create();
        Albaran albaran = gson.fromJson(json, Albaran.class);

        setTextViews(albaran);


    }

    public void setTextViews(Albaran albaran) {
        TextView tvIdAlbaran = activity.findViewById(R.id.tvIdAlbaran);
        TextView tvFechaAlbaran = (TextView) activity.findViewById(R.id.tvFechaAlbaran);
        TextView tvHoraCargaAlbaran = (TextView) activity.findViewById(R.id.tvHoraCargaAlbaran);
        TextView tvVehiculoAlbaran = (TextView) activity.findViewById(R.id.tvVehiculoAlbaran);
        TextView tvChoferAlbaran = (TextView) activity.findViewById(R.id.tvChoferAlbaran);
        TextView tvLimUsoAlbaran = (TextView) activity.findViewById(R.id.tvLimUsoAlbaran);
        TextView tvDestinoClienteAlbaran = (TextView) activity.findViewById(R.id.tvDestinoClienteAlbaran);
        TextView tvDestinoCifAlbaran = (TextView) activity.findViewById(R.id.tvDestinoCifAlbaran);
        TextView tvDestinoObraAlbaran = (TextView) activity.findViewById(R.id.tvDestinoObraAlbaran);
        TextView tvTransportistaNombreAlbaran = (TextView) activity.findViewById(R.id.tvTransportistaNombreAlbaran);
        TextView tvTransportistaCifAlbaran = (TextView) activity.findViewById(R.id.tvTransportistaCifAlbaran);
        TextView tvTransportistaDireccionAlbaran = (TextView) activity.findViewById(R.id.tvTransportistaDireccionAlbaran);
        TextView tvTransportistaPoblacionAlbaran = (TextView) activity.findViewById(R.id.tvTransportistaPoblacionAlbaran);
        TextView tvCantidadAlbaran = (TextView) activity.findViewById(R.id.tvCantidadAlbaran);
        TextView tvTipoCargaAlbaran = (TextView) activity.findViewById(R.id.tvTipoCargaAlbaran);
        TextView tvCementoTipoAlbaran = (TextView) activity.findViewById(R.id.tvCementoTipoAlbaran);
        TextView tvCementoMarcaAlbaran = (TextView) activity.findViewById(R.id.tvCementoMarcaAlbaran);
        TextView tvCementoKgm3Albaran = (TextView) activity.findViewById(R.id.tvCementoKgm3Albaran);
        TextView tvAdiccionesTipoAlbaran = (TextView) activity.findViewById(R.id.tvAdiccionesTipoAlbaran);
        TextView tvAdiccionesMarcaAlbaran = (TextView) activity.findViewById(R.id.tvAdiccionesMarcaAlbaran);
        TextView tvAdiccionesKgm3Albaran = (TextView) activity.findViewById(R.id.tvAdiccionesKgm3Albaran);
        TextView tvAditivosTipoAlbaran = (TextView) activity.findViewById(R.id.tvAditivosTipoAlbaran);
        TextView tvAditivosMarcaAlbaran = (TextView) activity.findViewById(R.id.tvAditivosMarcaAlbaran);
        TextView tvAditivosKgm3Albaran = (TextView) activity.findViewById(R.id.tvAditivosKgm3Albaran);
        TextView tvRelacionAcAlbaran = (TextView) activity.findViewById(R.id.tvRelacionAcAlbaran);
        TextView tvPedidoAlbaran = (TextView) activity.findViewById(R.id.tvPedidoAlbaran);
        TextView tvSuministradoAlbaran = (TextView) activity.findViewById(R.id.tvSuministradoAlbaran);
        TextView tvPteSumAlbaran = (TextView) activity.findViewById(R.id.tvPteSumAlbaran);

        tvIdAlbaran.setText(albaran.getId_albaran());
        tvFechaAlbaran.setText(albaran.getFecha());
        tvHoraCargaAlbaran.setText(albaran.getHora_carga());
        tvVehiculoAlbaran.setText(albaran.getVehiculo());
        tvChoferAlbaran.setText(albaran.getChofer());
        tvLimUsoAlbaran.setText(albaran.getLim_uso());
        tvDestinoClienteAlbaran.setText(albaran.getDestino_cliente());
        tvDestinoCifAlbaran.setText(albaran.getDestino_cif());
        tvDestinoObraAlbaran.setText(albaran.getDestino_obra());
        tvTransportistaNombreAlbaran.setText(albaran.getTransp_nombre());
        tvTransportistaCifAlbaran.setText(albaran.getTrans_cif());
        tvTransportistaPoblacionAlbaran.setText(albaran.getTransp_poblacion());
        tvCantidadAlbaran.setText(String.format("%.2f", Float.parseFloat(albaran.getCarga_cantidad())));
        tvTipoCargaAlbaran.setText(albaran.getCarga_tipo());
        tvCementoTipoAlbaran.setText(albaran.getCemento_tipo());
        tvCementoMarcaAlbaran.setText(albaran.getCemento_marca());
        tvCementoKgm3Albaran.setText(albaran.getCemento_kgm3());
        tvAdiccionesTipoAlbaran.setText(albaran.getAdicciones_tipo());
        tvAdiccionesMarcaAlbaran.setText(albaran.getAdicciones_marca());
        tvAdiccionesKgm3Albaran.setText(albaran.getAdicciones_kgm3());
        tvAditivosTipoAlbaran.setText(albaran.getAditivos_tipo());
        tvAditivosKgm3Albaran.setText(albaran.getAditivos_kgm3());
        tvRelacionAcAlbaran.setText(albaran.getCarga_relacion_ac());
        tvPedidoAlbaran.setText(albaran.getCarga_pedido());
        tvSuministradoAlbaran.setText(albaran.getCarga_suministrado());
        tvPteSumAlbaran.setText(albaran.getCarga_pte_sum());
    }

    public static String normalizarTexto(String cadena) {
        String limpio =null;
        if (cadena !=null) {
            String valor = cadena;
            valor = valor.toUpperCase();
            // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
            limpio = Normalizer.normalize(valor, Normalizer.Form.NFD);
            // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
            limpio = limpio.replaceAll("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
            // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
            limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC);
        }
        return limpio;
    }
}
