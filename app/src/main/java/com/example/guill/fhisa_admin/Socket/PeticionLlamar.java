package com.example.guill.fhisa_admin.Socket;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Objetos.Vehiculo;
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

public class PeticionLlamar extends AsyncTask<String, String, String> {

    public Activity activity;
    public PeticionLlamar(Activity activity) {
        this.activity = activity;
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

        if (json.startsWith("{    \"id\" : ,") ) {
            json = json.replace("{    \"id\" : ,", "{    \"id\" : \"\",");
        }

        String telefono;
        if (json.startsWith("error 401")) {
            telefono = "No registrado";
        } else {
            Gson gson = new GsonBuilder().serializeNulls().create();
            Vehiculo vehiculo = gson.fromJson(json, Vehiculo.class);
            telefono = vehiculo.getTlf();
        }

        return telefono;
    }

    /**
     *After completing background task
     * **/
    @Override
    protected void onPostExecute(String telefono) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+telefono));
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},1);
        } else {
            if(telefono.compareTo("No registrado") == 0 ) {
                Toast.makeText(activity.getApplicationContext(), "El teléfono no está registrado en Velneo",
                        Toast.LENGTH_SHORT).show();

            } else {
                activity.getApplicationContext().startActivity(callIntent);
            }
        }
    }
}
