package com.example.guill.fhisa_admin.Socket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by guill on 12/12/2017.
 */

public class PeticionEstado extends AsyncTask<String, String, String> {

    public Activity activity;
    public Marker marcador;
    public ProgressBar progressBar;

    //Libre -- verde
    BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
    //Cargado -- rojo
    BitmapDescriptor icon0 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
    //Llegada Obra -- azul claro
    BitmapDescriptor icon1 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
    //Inicio descarga -- azul oscuro
    BitmapDescriptor icon2 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
    //Salida Obra -- naranja
    BitmapDescriptor icon3 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
    //Llegada planta -- verde
    BitmapDescriptor icon4 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

    public PeticionEstado(Activity activity, Marker marcador, ProgressBar progressBar) {
        this.activity = activity;
        this.marcador = marcador;
        this.progressBar = progressBar;
    }

    /**
     * Before starting background thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("Starting download");
        progressBar.setVisibility(View.VISIBLE);
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
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream(), "ISO-8859-1"));
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
            request = imei[0] + ", estado, ";
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

        Log.i("AsyncTask", "Json: " + json);
        return json;
    }

    /**
     * After completing background task
     **/
    @Override
    protected void onPostExecute(String json) {

        if (json.compareTo("error 401")!=0) {
            int estado = 0;

            if (json.compareTo("[-1 ]") == 0) {
                estado = -1;
            } else {
                estado = Integer.parseInt(json.substring(1, 2));
            }

            if (estado==-1) {
                marcador.setIcon(icon);
            } else  if (estado==0) {
                marcador.setIcon(icon0);
            } else if (estado==1) {
                marcador.setIcon(icon1);
            } else if (estado==2) {
                marcador.setIcon(icon2);
            } else if (estado==3) {
                marcador.setIcon(icon3);
            } else if (estado==4) {
                marcador.setIcon(icon4);
            }
        }
        progressBar.setVisibility(View.GONE);
    }
}