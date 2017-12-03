package com.example.guill.fhisa_admin.Socket;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Adapter.AdapterAlbaranes;
import com.example.guill.fhisa_admin.Objetos.AlbaranReducido;
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
import java.util.Collections;
import java.util.List;

/**
 * Created by guill on 02/12/2017.
 */

public class PeticionAlbaranes extends AsyncTask<String, String, String> {

    public Activity activity;
    public RecyclerView recyclerView;

    public PeticionAlbaranes(Activity activity, RecyclerView recyclerView) {
        this.activity = activity;
        this.recyclerView = recyclerView;
    }

    /**
     * Before starting background thread
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(activity, "Se están descargando los datos del Servidor...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... imei) {
        Socket socketCliente = null;
        BufferedReader entrada = null;
        PrintWriter salida = null;
        try {
            socketCliente = new Socket("89.17.197.73", 6905); //"89.17.197.73", 6905
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
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
            request = imei[0] + ", albaranes, ";
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

        String jsonImei = imei[0] + "---" +json;
        return jsonImei;
    }

    /**
     * After completing background task
     **/
    @Override
    protected void onPostExecute(String jsonImei) {
        String parts[] = jsonImei.split("---");
        String imei = parts[0];
        String json = parts[1];

        final Type tipoAlbaranes = new TypeToken<List<AlbaranReducido>>() {
        }.getType();
        final List<AlbaranReducido> albaranes = new Gson().fromJson(json, tipoAlbaranes);

        Collections.reverse(albaranes);

        AdapterAlbaranes adapterAlbaranes = new AdapterAlbaranes(activity, albaranes, imei);
        recyclerView.setAdapter(adapterAlbaranes);

    }

}