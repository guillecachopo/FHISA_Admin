package com.example.guill.fhisa_admin.Socket;

/**
 * Created by guill on 21/11/2017.
 */

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SocketTCP2 {
    public static void main(String[] args) throws IOException {
        Socket client = null;
        PrintWriter out = null;
        DataInputStream dis = null;
        try {
            client = new Socket("89.17.197.73", 6905);
            client.setSoTimeout(5000);
            out = new PrintWriter(client.getOutputStream(), true);
            dis = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out.println("860935033015443, vehiculo, ");

            int br;
            byte[] data = new byte[4096];

            FileOutputStream fos = new FileOutputStream("fileout");

            while ((br = dis.read(data, 0, data.length)) != -1) {
                fos.write(data, 0, br);
            }

            fos.close();
            dis.close();


        } catch (SocketTimeoutException ste) {
            ste.printStackTrace();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}