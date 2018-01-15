package com.example.guill.fhisa_admin.Socket;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Process;
import android.preference.PreferenceManager;

import com.example.guill.fhisa_admin.Objetos.Albaran;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;

/**
 * Created by guill on 10/12/2017.
 */

    public class PeticionUltimoAlbaran extends AsyncTask<String, String, String> {

        public Activity activity;

        public PeticionUltimoAlbaran(Activity activity) {
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
        protected String doInBackground(String... imei) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
            String ip = preferences.getString("etIP", "89.17.197.73");
            String puertoString = preferences.getString("etPuerto","6905");
            int puerto = Integer.parseInt(puertoString);

            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
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
                request = imei[0] + ", ultimo_albaran, ";
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

            Gson gson = new GsonBuilder().serializeNulls().create();
            Albaran albaran = gson.fromJson(json, Albaran.class);
            String destino = albaran.getDestino();

            return destino;
        }

        /**
         * After completing background task
         **/
        @Override
        protected void onPostExecute(String destino) {
            super.onPostExecute(destino);;
        }

    }

