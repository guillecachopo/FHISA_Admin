package com.example.guill.fhisa_admin.Socket;

import android.util.Log;

import com.example.guill.fhisa_admin.Objetos.Vehiculo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by guill on 22/11/2017.
 */

public class Peticiones {

    String ip;
    int puerto;

    public Peticiones(String ip, int puerto) {
        this.ip = ip;
        this.puerto = puerto;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public void inicializarSocket(String ip, int puerto) {
        Socket socketCliente = null;
        BufferedReader entrada = null;
        PrintWriter salida = null;
        try {
            socketCliente = new Socket(ip, puerto); //"89.17.197.73", 6905
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(new BufferedWriter(new
                    OutputStreamWriter(socketCliente.getOutputStream())),true);

        } catch (IOException e) {
            System.err.println("No puede establer canales de E/S para la conexión");
            System.exit(-1);
        }
        BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));

    }

    public Vehiculo obtenerVehiculo(String imei) throws IOException {
        //TODO: Cambiar lo del socket por inicializarSocket cuando sepamos...
        //inicializarSocket(ip,puerto)

        Socket socketCliente = null;
        BufferedReader entrada = null;
        PrintWriter salida = null;
        Log.i("PeticionS", ip);
        Log.i("PeticionS", String.valueOf(puerto));
        try {
            socketCliente = new Socket(ip, puerto); //"89.17.197.73", 6905
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(new BufferedWriter(new
                    OutputStreamWriter(socketCliente.getOutputStream())),true);

        } catch (IOException e) {
            System.err.println("No puede establer canales de E/S para la conexión");
            System.exit(-1);
        }
        BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));

        String json, request, respuesta;
        json = "";
        int i=0;
        do {
            request = imei +", vehiculo, ";
            salida.println(request);
            respuesta = entrada.readLine();
            if (respuesta!=null) json+=respuesta;
            i++;
        } while (respuesta != null);

        salida.close();
        entrada.close();
        stdIn.close();
        socketCliente.close();

        System.out.println(json);

        Vehiculo vehiculo = new Gson().fromJson(json, Vehiculo.class);

        return vehiculo;
    }
}
