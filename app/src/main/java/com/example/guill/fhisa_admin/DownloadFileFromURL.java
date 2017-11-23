package com.example.guill.fhisa_admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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
 * Created by guill on 15/11/2017.
 */

class DownloadFileFromURL extends AsyncTask<String, String, String> {

    private ProgressDialog pDialog;
    private Context mContext;

    public DownloadFileFromURL (Context context){
        mContext = context;
    }

    /**
     * Before starting background thread
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("Starting download");

        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Descargando... Espere, por favor...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    /**
     * Descarga un archivo en una background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File fhisaDir = new File(Environment.getExternalStorageDirectory(), "FHISAFirebase");
            if (!fhisaDir.exists()) fhisaDir.mkdirs();
            String fhisaDirString = fhisaDir.toString();

            System.out.println("Downloading");
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
            OutputStream output = new FileOutputStream(fhisaDir+"/firebasebackup"+day+monthNumber+year+hour+".json");
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
        Toast.makeText(mContext, "JSON descargado en la ruta root/FHISAFirebase", Toast.LENGTH_LONG).show();

        pDialog.dismiss();
    }

}
