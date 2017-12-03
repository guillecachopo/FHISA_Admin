package com.example.guill.fhisa_admin.Socket;

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
           // request = "860935033015443, albaran, 301226" ;
            request = "860935033015443, albaran, 301226";
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


      //  final Type tipoConsumos = new TypeToken<List<Consumo>>(){}.getType();
     //   final List<Consumo> consumos = new Gson().fromJson(json, tipoConsumos);

        /*
        System.out.println("Consumos: " + consumos.size());
        for (Consumo consumo : consumos) {
            System.out.println("id: " + consumo.getId());
            System.out.println("vehiculo: " + consumo.getVehiculo());
            System.out.println("articulo: " + consumo.getArticulo());
            System.out.println("unidades: " + consumo.getUnidades());
            System.out.println("consumo: " + consumo.getConsumo());
            System.out.println("fecha: " + consumo.getFecha());
            System.out.println("km: " + consumo.getKm());
            System.out.println("---------------------");
        }
        */

    }
}