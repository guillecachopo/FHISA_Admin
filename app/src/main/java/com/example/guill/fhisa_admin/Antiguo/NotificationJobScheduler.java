package com.example.guill.fhisa_admin.Antiguo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.guill.fhisa_admin.MainActivity;
import com.example.guill.fhisa_admin.Objetos.Area;
import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.ErrorNotificacion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.example.guill.fhisa_admin.R;
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

    ArrayList<Area> areasList;
    ArrayList<String> IDsAreas;
    String idArea;


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

        IDsAreas = new ArrayList<>();
        areasList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);
        final DatabaseReference areasRef = database.getReference(FirebaseReferences.AREAS_REFERENCE);

        areasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    idArea = snapshot.getValue(Area.class).getIdentificador();
                    Area area = null;
                    if(!IDsAreas.contains(idArea)) {
                        area = snapshot.getValue(Area.class);
                        IDsAreas.add(idArea);
                        areasList.add(area);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference erroresRef = database.getReference(FirebaseReferences.ERRORES_REFERENCE);

                for (int i=0; i<camionesList.size(); i++){
                    int notifId = i;
                    Camion camionNotif = camionesList.get(i);
                    long ultimaHora = camionNotif.getUltimaPosicion().getTime();
                    Date horaActualDate = Calendar.getInstance().getTime();
                    long horaActual = horaActualDate.getTime();
                    long diferencia = horaActual - ultimaHora;
                    //Log.i("JobScheduler", "Diferencia: " + diferencia);

                    boolean dentro = camionEnArea(camionNotif, areasList);
                    Log.i("JobScheduler Area", "Camion " + camionesList.get(i).getId() + "en area: " + dentro );
                    //Setear en milisegundos cuánto tiempo queremos que puede estar sin recibir una posición antes de que salte
                    if(diferencia >= 1000 * 60 * 10 && !dentro) {
                        enviarNotificacion(camionNotif, notifId);
                        ErrorNotificacion errorNotificacion = new ErrorNotificacion(camionNotif.getId(), diferencia, horaActual);
                        //erroresRef.child(camionNotif.getId()).push().setValue(errorNotificacion);
                        erroresRef.push().setValue(errorNotificacion);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    final static String GROUP_KEY = "group_key";

    public void enviarNotificacion(Camion camionError, int notifId) {
        Log.i("JobScheduler", "Dentro de enviarNotificacion" );
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Advertencia")
                        .setContentText("No se reciben posiciones de " + camionError.getId())
                        .setSound(alarmSound)
                        .setVibrate(new long[] { 500, 500 })
                        //.setOngoing(true) //Si se marca, no se puede cerrar la notificacion!
                        .setAutoCancel(true)
                        .setGroup(GROUP_KEY)
                        .setGroupSummary(true)
                        .setLights(Color.RED, 1000, 1000);

        // Create pending intent, mention the Activity which needs to be
        //triggered when user clicks on notification(MainActivity.class)
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);


        nManager.notify(notifId, mBuilder.build());
    }


    public boolean camionEnArea(Camion camionComprobar, ArrayList<Area> listaAreas) {
        ArrayList<Integer> d = new ArrayList<>();
        boolean dentro = false;

        for (int i=0; i < listaAreas.size(); i++) {
            float[] distance = new float[2];
            Location.distanceBetween(camionComprobar.getUltimaPosicion().getLatitude(), camionComprobar.getUltimaPosicion().getLongitude(),
                    listaAreas.get(i).getLatitud(), listaAreas.get(i).getLongitud(), distance);

            Log.i("JobScheduler", "distancia: " + distance[0] + ", radio: " + listaAreas.get(i).getDistancia());

            if (distance[0] <= areasList.get(i).getDistancia()) { //Camion dentro del circulo
                // Inside The Circle
                dentro = true;
                d.add(1);
            } else {
                dentro = false;
                d.add(0);
            }
        }

        for (int i=0; i<d.size(); i++) {
            if (d.get(i) == 1) dentro = true;
        }
        return dentro;
    }



}
