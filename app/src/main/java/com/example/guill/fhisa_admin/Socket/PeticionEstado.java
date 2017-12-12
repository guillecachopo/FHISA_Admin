package com.example.guill.fhisa_admin.Socket;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.guill.fhisa_admin.MapsActivity;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

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

    //Libre
    BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
    //Cargado
    BitmapDescriptor icon0 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
    //Llegada Obra
    BitmapDescriptor icon1 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
    //Inicio descarga
    BitmapDescriptor icon2 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
    //Salida Obra
    BitmapDescriptor icon3 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
    //Llegada planta
    BitmapDescriptor icon4 = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);

    public PeticionEstado(Activity activity) {
        this.activity = activity;
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
        Socket socketCliente = null;
        BufferedReader entrada = null;
        PrintWriter salida = null;
        try {
            socketCliente = new Socket("89.17.197.73", 6905); //"89.17.197.73", 6905
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

        int estado = Integer.parseInt(json.substring(1,2));
        MapsActivity map = new MapsActivity();

        if (estado==-1) {
            map.mMarkerMap.get(imei[0]).setIcon(icon);
        } else if (estado==0) {
            map.mMarkerMap.get(imei[0]).setIcon(icon0);
        } else if (estado==0) {
            map.mMarkerMap.get(imei[0]).setIcon(icon1);
        } else if (estado==0) {
            map.mMarkerMap.get(imei[0]).setIcon(icon2);
        } else if (estado==0) {
            map.mMarkerMap.get(imei[0]).setIcon(icon3);
        } else if (estado==0) {
            map.mMarkerMap.get(imei[0]).setIcon(icon4);
        }
        
        return null;
    }

    /**
     * After completing background task
     **/
    @Override
    protected void onPostExecute(String json) {

    }
}