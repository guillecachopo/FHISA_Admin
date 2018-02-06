package com.example.guill.fhisa_admin;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by guill on 21/12/2017.
 */

public class CopiaSeguridadFirebase extends AsyncTask <String, String, String> {

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

}
