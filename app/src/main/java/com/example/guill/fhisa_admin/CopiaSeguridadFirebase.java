package com.example.guill.fhisa_admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Mail.EnviarEmailBackup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by guill on 21/12/2017.
 */

public class CopiaSeguridadFirebase extends AsyncTask <String, String, String> {

    private ProgressDialog pDialog;
    public Context context;
    public SharedPreferences preferences;
    String rutaFichero;
    ProgressBar progressBar;

    public CopiaSeguridadFirebase(Context context, SharedPreferences preferences, ProgressBar progressBar) {
        this.context = context;
        this.preferences = preferences;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("Starting download");
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File fhisaDir = new File(Environment.getExternalStorageDirectory(), "FHISAFirebase");
            if (!fhisaDir.exists()) fhisaDir.mkdirs();
            String fhisaDirString = fhisaDir.toString();

            URL url = new URL(f_url[0]);

            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            //Getting hour to file name
            final Date currentTime = Calendar.getInstance().getTime();
            final String day = (String) android.text.format.DateFormat.format("dd",   currentTime); // 31
            final String monthNumber  = (String) android.text.format.DateFormat.format("MM",   currentTime); // 10
            final String year         = (String) android.text.format.DateFormat.format("yy", currentTime); // 2017
            final String hour = (String) android.text.format.DateFormat.format("HHmmss", currentTime); //1326
            // Output stream to write file
            rutaFichero = fhisaDir+"/firebasebackup"+day+monthNumber+year+hour+".json";
            OutputStream output = new FileOutputStream(rutaFichero);
            byte data[] = new byte[1024];

            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    /**
     * After completing background task
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        Toast.makeText(context, "JSON descargado en la ruta root/FHISAFirebase", Toast.LENGTH_LONG).show();
        new EnviarEmailBackup(context, preferences, rutaFichero, progressBar).execute();
    }


    //---------------------- NO IMPLEMENTADO ----------------------------------------
    public byte[] cifra(String sinCifrar) throws Exception {
        final byte[] bytes = sinCifrar.getBytes("UTF-8");
        final Cipher aes = obtieneCipher(true);
        final byte[] cifrado = aes.doFinal(bytes);
        return cifrado;
    }

    public String descifra(byte[] cifrado) throws Exception {
        final Cipher aes = obtieneCipher(false);
        final byte[] bytes = aes.doFinal(cifrado);
        final String sinCifrar = new String(bytes, "UTF-8");
        return sinCifrar;
    }

    public Cipher obtieneCipher(boolean paraCifrar) throws Exception {
        final String frase = "Ñ?1-¿/-_([{Ó}_]í_É''-*89*ç_=Ç<Ü>_áéÁ-ÍóúÚü_ÜñÑ234-5670!#%$&()%_____";
        final MessageDigest digest = MessageDigest.getInstance("SHA");
        digest.update(frase.getBytes("UTF-8"));
        final SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");

        final Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
        if (paraCifrar) {
            aes.init(Cipher.ENCRYPT_MODE, key);
        } else {
            aes.init(Cipher.DECRYPT_MODE, key);
        }

        return aes;
    }

    public static String getStringFromFile(String archivo) throws FileNotFoundException, IOException {
        String cadena;
        String texto = "";
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine())!=null) {
            texto+=cadena;
        }
        b.close();

        return texto;
    }

}
