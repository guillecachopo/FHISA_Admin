package com.example.guill.fhisa_admin.Socket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Adapter.AdapterMantenimiento;
import com.example.guill.fhisa_admin.Objetos.ItvMantenimiento;
import com.example.guill.fhisa_admin.Objetos.Mantenimiento;
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
import java.util.ArrayList;

/**
 * Created by guill on 03/12/2017.
 */

public class PeticionMantenimiento extends AsyncTask<String, String, String> {

    public Activity activity;
    public RecyclerView recyclerView;
    public ProgressBar progressBar;

    public PeticionMantenimiento(Activity activity, RecyclerView recyclerView, ProgressBar progressBar) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.progressBar = progressBar;
    }

    /**
     * Before starting background thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("Starting download");
    }

    @Override
    protected String doInBackground(String... imei) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String ip = preferences.getString("etIP", "89.17.197.73");
        String puertoString = preferences.getString("etPuerto","6905");
        int puerto = Integer.parseInt(puertoString);

        Socket socketCliente = null;
        BufferedReader entrada = null;
        PrintWriter salida = null;
        try {
            socketCliente = new Socket(ip, puerto); //"89.17.197.73", 6905
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
            request = imei[0] + ", mantenimiento, ";
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
     * After completing background task
     **/
    @Override
    protected void onPostExecute(String json) {
        Gson gson = new GsonBuilder().create();

        if (json.compareTo("error 401") == 0) {
            TextView tvNoVelneo = (TextView) activity.findViewById(R.id.tvMantenimientoNoVelneo);
            tvNoVelneo.setText("El vehículo no está registrado en Velneo");
        } else {
            ItvMantenimiento itvMantenimiento = gson.fromJson(json, ItvMantenimiento.class);
            ArrayList<Mantenimiento> mantenimiento = itvMantenimiento.getMantenimiento();

            TextView tvItvMantenimiento = (TextView) activity.findViewById(R.id.tvItvMantenimiento);
            TextView tvMatriculaMantenimiento = (TextView) activity.findViewById(R.id.tvMatriculaMantenimiento);
            TextView tvPrecisaMantenimiento = (TextView) activity.findViewById(R.id.tvPrecisaMantenimiento);

            tvItvMantenimiento.setText(itvMantenimiento.getITV());

            Log.i("Mantenimiento", String.valueOf(mantenimiento));

            if (String.valueOf(mantenimiento).compareTo("[]") != 0) {
                tvMatriculaMantenimiento.setText(mantenimiento.get(0).getVehiculo());
                AdapterMantenimiento adapterMantenimiento = new AdapterMantenimiento(activity, mantenimiento);
                recyclerView.setAdapter(adapterMantenimiento);
            } else {
                tvPrecisaMantenimiento.setText("El vehículo no requiere mantenimiento actualmente");
            }
        }

        progressBar.setVisibility(View.GONE);

    }
}
