package com.example.guill.fhisa_admin.Socket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        String resultado = imei[0]+"---"+json;
        return resultado;
    }

    /**
     *After completing background task
     * **/
    @Override
    protected void onPostExecute(String resultado) {
        String[] parts = resultado.split("---");
        String imeiDisp = parts[0];
        String json = parts[1];

        if (json.startsWith("error 401")) {

            if (activity.getClass().getSimpleName().compareTo("OpcionesCamionActivity") == 0) {
                TextView tvLlamar = (TextView) activity.findViewById(R.id.tvLlamar);
                TextView tvAlbaranes = (TextView) activity.findViewById(R.id.tvVerAlbaranes);
                TextView tvConsumo = (TextView) activity.findViewById(R.id.tvConsumo);
                TextView tvMantenimiento = (TextView) activity.findViewById(R.id.tvMantenimiento);

                RelativeLayout rlLlamar = (RelativeLayout) activity.findViewById(R.id.rlLlamar);
                RelativeLayout rlAlbaranes = (RelativeLayout) activity.findViewById(R.id.rlVerAlbaranes);
                RelativeLayout rlConsumo = (RelativeLayout) activity.findViewById(R.id.rlConsumo);
                RelativeLayout rlMantenimiento = (RelativeLayout) activity.findViewById(R.id.rlMantenimiento);

                tvLlamar.setTextColor(Color.GRAY);
                tvAlbaranes.setTextColor(Color.GRAY);
                tvConsumo.setTextColor(Color.GRAY);
                tvMantenimiento.setTextColor(Color.GRAY);

                vehiculoNoRegistrado(rlLlamar);
                vehiculoNoRegistrado(rlAlbaranes);
                vehiculoNoRegistrado(rlConsumo);
                vehiculoNoRegistrado(rlMantenimiento);



            } else {
                TextView tvIdVehiculo = (TextView) activity.findViewById(R.id.tvIdVehiculo);
                TextView tvImeiVehiculo = (TextView) activity.findViewById(R.id.tvImeiVehiculo);
                TextView tvMatriculaVehiculo = (TextView) activity.findViewById(R.id.tvMatriculaVehiculo);
                TextView tvTlfVehiculo = (TextView) activity.findViewById(R.id.tvTlfVehiculo);

                tvIdVehiculo.setText("No disponible");
                tvImeiVehiculo.setText(imeiDisp);
                tvMatriculaVehiculo.setText("No disponible");
                tvTlfVehiculo.setText("No disponible");
            }


        } else {

            if (activity.getClass().getSimpleName().compareTo("DetallesVehiculoActivity") == 0) {
                if (json.startsWith("{    \"id\" : ,") ) {
                    json = json.replace("{    \"id\" : ,", "{    \"id\" : \"\",");
                }
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
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    public void vehiculoNoRegistrado (RelativeLayout rlVehiculo) {
        rlVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "El vehículo no está registrado en Velneo", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
