package com.example.guill.fhisa_admin.Socket;

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
 * Created by guill on 13/11/2017.
 */


public class SocketClienteTCP {
    public static void main(String[] args)  throws IOException {
        Socket socketCliente = null;
        BufferedReader entrada = null;
        PrintWriter salida = null;

        // Creamos un socket en el lado cliente, enlazado con un
        // servidor que está en la misma máquina que el cliente
        // y que escucha en el puerto 4444
        try {
            socketCliente = new Socket("89.17.197.73", 6905);
            // Obtenemos el canal de entrada
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            // Obtenemos el canal de salida
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
            request = "860935033015443, vehiculo, ";
            salida.println(request);
            respuesta = entrada.readLine();
            //System.out.println(i+": " +respuesta);
            if (respuesta!=null) json+=respuesta;
            i++;
        } while (respuesta != null);

        salida.close();
        entrada.close();
        stdIn.close();
        socketCliente.close();

        System.out.println(json);

        Vehiculo vehiculo = new Gson().fromJson(json, Vehiculo.class);

        System.out.println("Matricula: " + vehiculo.getMatricula());
        System.out.println("Numero de tlf: " + vehiculo.getTlf());

    }
}