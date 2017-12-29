package com.example.guill.fhisa_admin.Socket;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.Vehiculo;
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

/**
 * Created by guill on 29/11/2017.
 */

public class PeticionVehiculo extends AsyncTask<String, String, String> {

    public Activity activity;
    public ProgressBar progressBar;
    public PeticionVehiculo(Activity activity, ProgressBar progressBar) {
        this.activity = activity;
        this.progressBar = progressBar;
    }

    /**
     * Before starting background thread
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("Starting download");
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
            request = imei[0] + ", vehiculo, ";
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
        Gson gson = new GsonBuilder().serializeNulls().create();
        Vehiculo vehiculo = gson.fromJson(json, Vehiculo.class);
        String idFhisa = vehiculo.getId();
        String imei = vehiculo.getImei();
        String telefono = vehiculo.getTlf();
        String matricula = vehiculo.getMatricula();

        TextView tvIdVehiculo = (TextView) activity.findViewById(R.id.tvIdVehiculo);
        TextView tvImeiVehiculo = (TextView) activity.findViewById(R.id.tvImeiVehiculo);
        TextView tvMatriculaVehiculo = (TextView) activity.findViewById(R.id.tvMatriculaVehiculo);
        TextView tvTlfVehiculo = (TextView) activity.findViewById(R.id.tvTlfVehiculo);

        tvIdVehiculo.setText(idFhisa);
        tvImeiVehiculo.setText(imei);
        tvMatriculaVehiculo.setText(matricula);
        tvTlfVehiculo.setText(telefono);

        progressBar.setVisibility(View.GONE);
    }
}
