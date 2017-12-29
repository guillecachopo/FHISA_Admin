package com.example.guill.fhisa_admin.Socket;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Adapter.AdapterConsumos;
import com.example.guill.fhisa_admin.Objetos.Consumo;
import com.example.guill.fhisa_admin.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by guill on 30/11/2017.
 */

public class PeticionConsumos extends AsyncTask<String, String, String> {

    public Activity activity;
    public RecyclerView recyclerView;
    public ProgressDialog pDialog;
    public ProgressBar progressBar;

    public PeticionConsumos(Activity activity, RecyclerView recyclerView, ProgressBar progressBar) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
    }

    /**
     * Before starting background thread
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(activity, "Se están descargando los datos del Servidor..." , Toast.LENGTH_SHORT).show();
        /*
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Descargando... Espere, por favor...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show(); */

    }

    @Override
    protected String doInBackground(String... imei) {
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
        do {
            request = imei[0] + ", consumo, ";
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

        return json;
    }

    /**
     *After completing background task
     * **/
    @Override
    protected void onPostExecute(String json) {
        //pDialog.dismiss();

        if (json.compareTo("error 401") == 0) {
            TextView tvNoVelneo = (TextView) activity.findViewById(R.id.tvConsumosNoVelneo);
            tvNoVelneo.setText("El vehículo no está registrado en Velneo");
        } else {
            final Type tipoConsumos = new TypeToken<List<Consumo>>() {
            }.getType();
            final List<Consumo> consumos = new Gson().fromJson(json, tipoConsumos);

            ArrayList<Consumo> consumos1 = new ArrayList<>();

            for (Consumo consumo : consumos) {
                if (consumo.getKm().compareTo("0") != 0) consumos1.add(consumo);
            }

            Collections.reverse(consumos1);

            float suma = 0;
            float consumoMedio = 0;
            for (Consumo c : consumos1) {
                suma += Float.parseFloat(c.getConsumo());
            }
            consumoMedio = (suma / consumos1.size()) * 100;

            TextView tvConsumoMedio = (TextView) activity.findViewById(R.id.tvMediaConsumo);
            tvConsumoMedio.setText(String.format("%.2f", consumoMedio) + " L/100Km");

            AdapterConsumos adapterConsumos = new AdapterConsumos(activity, consumos1);
            recyclerView.setAdapter(adapterConsumos);
        }

        progressBar.setVisibility(View.GONE);
    }
}
