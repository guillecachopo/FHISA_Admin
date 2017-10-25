package com.example.guill.fhisa_admin;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by guill on 25/10/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificationJobScheduler extends JobService {


    Posicion location;
    String id;
    LatLng latlng;

    double altitude;
    double latitude;
    double longitude;
    float speed;
    long time;
    List<Camion> camionesList;
    List<String> IDs;
    long numCamiones;



    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("JobScheduler", "Starting job");
        FirebaseCamionesListener();
        jobFinished(jobParameters, true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("JobScheduler", "Stopping job");
        return true;
    }

    public void FirebaseCamionesListener() {
        camionesList = new ArrayList<>();
        IDs = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);

        Log.i("JobScheduler", "Dentro de Funcion");

        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("JobScheduler", "Dentro de childEventListener");
                //AQUI ESTAN LAS ID
                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {

                    id = snapshot.getKey();
                    Camion camion=null;

                    if (!IDs.contains(id)) {
                        camion = new Camion(id);
                        IDs.add(id);
                        camionesList.add(camion);

                    }
                    else {
                        for (int i=0; i<camionesList.size(); i++)
                            if (camionesList.get(i).getId().compareTo(id)==0) {
                                camion = camionesList.get(i);
                                camion.clearPosiciones();
                            }
                    }

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                            Posicion posicion = snapshot2.getValue(Posicion.class);
                            camion.setPosiciones(posicion);

                            altitude = camion.getUltimaPosicion().getAltitude();
                            latitude = camion.getUltimaPosicion().getLatitude();
                            longitude = camion.getUltimaPosicion().getLongitude();
                            speed = camion.getUltimaPosicion().getSpeed();
                            time = camion.getUltimaPosicion().getTime();
                            LatLng latlng = new LatLng(latitude, longitude);
                        } //for snapshot2 (Iterador donde estan las posiciones)
                    } //for snapshot1 (Iterador donde esta la cadena "posiciones")

                } //for snapshot (Iterador donde estan las IDs)

                Log.i("JobScheduler", "Tamaño lista camiones: " + String.valueOf(camionesList.size()));

                for (int i=0; i<camionesList.size(); i++){
                    Camion camionNotif = camionesList.get(i);
                    long ultimaHora = camionNotif.getUltimaPosicion().getTime();
                    Date horaActualDate = Calendar.getInstance().getTime();
                    long horaActual = horaActualDate.getTime();
                    long diferencia = horaActual - ultimaHora;
                    Log.i("JobScheduler", "Diferencia: " + diferencia);
                    if(diferencia >= 1000) enviarNotificacion(); //Setear en milisegundos cuánto tiempo queremos que puede estar sin recibir una posición antes de que salte
                    Log.i("JobScheduler", "Notificacion ya lanzada, estamos de nuevo en dataSnapshot (bucle camiones)");
                }
                Log.i("JobScheduler", "Notificacion ya lanzada, estamos de nuevo en dataSnapshot (fuera bucle)");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void enviarNotificacion() {
        Log.i("JobScheduler", "Dentro de enviarNotificacion" );
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Advertencia")
                        .setContentText("No se reciben posiciones de algún camión")
                        .setSound(alarmSound)
                        .setVibrate(new long[] { 500, 500 })
                        .setLights(Color.RED, 1000, 1000);
        nManager.notify(12345, mBuilder.build());
    }



}
