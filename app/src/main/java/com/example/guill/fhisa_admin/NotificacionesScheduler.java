package com.example.guill.fhisa_admin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.guill.fhisa_admin.Objetos.BaseOperativa;
import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Objetos.ErrorNotificacion;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by guill on 08/01/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NotificacionesScheduler extends JobService {

    /**
     * Lista que contiene los camiones
     */
    ArrayList<Camion> listaCamiones = new ArrayList<>();

    /**
     * Lista que contiene las Ids de los camiones
     */
    ArrayList<String> listaIdsCamiones = new ArrayList<>();

    /**
     * Base de datos Firebase a utilizar
     */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Referencia de los camiones en Firebase
     */
    final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);

    /**
     * Referencia de las areas en Firebase
     */
    final DatabaseReference areasRef = database.getReference(FirebaseReferences.AREAS_REFERENCE);

    /**
     * Lista que contiene las areas
     */
    ArrayList<BaseOperativa> listaBasesOperativas = new ArrayList<>();

    /**
     * Lista que contiene las Ids de los camiones
     */
    ArrayList<String> IDsAreas = new ArrayList<>();

    /**
     * Referencia de los errores en Firebase
     */
    DatabaseReference erroresRef = database.getReference(FirebaseReferences.ERRORES_REFERENCE);

    /**
     * Referencia de las frecuencias en Firebase
     */
    DatabaseReference frecuenciasRef = database.getReference("frecuencias");

    int notifId = 0;


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("JobScheduler", "onStartJob");
        FirebaseCamionesListener();
        jobFinished(jobParameters, true);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("JobScheduler", "onStopJob");
        return true;
    }

    private void FirebaseCamionesListener() {
        Log.i("JobScheduler", "FirebaseCamionesListener");

        inicializarAreas(areasRef);
        inicializarListaCamiones(camionesRef);

    }

    /**
     * Comprueba si el camión está dentro de un area o no
     * @param camionComprobar
     * @param listaBasesOperativas
     * @return
     */
    public boolean camionEnArea(Camion camionComprobar, ArrayList<BaseOperativa> listaBasesOperativas) {
        ArrayList<Integer> d = new ArrayList<>();
        boolean dentro = false;

        for (BaseOperativa baseOperativa : listaBasesOperativas) {
            float[] distance = new float[2];
            Location.distanceBetween(camionComprobar.getUltimaPosicion().getLatitude(), camionComprobar.getUltimaPosicion().getLongitude(),
                    baseOperativa.getLatitud(), baseOperativa.getLongitud(), distance);

            Log.i("JobScheduler", "distancia: " + distance[0] + ", radio: " + baseOperativa.getDistancia());

            if (distance[0] <= baseOperativa.getDistancia()) { //Camion dentro del circulo
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



    final static String GROUP_KEY = "group_key";

    /**
     * Envía una notificación al usuario
     * @param camionError
     * @param notifId
     */
    private void enviarNotificacion(Camion camionError, int notifId) {
        Log.i("JobScheduler", "Dentro de enviarNotificacion" );

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String nombre = preferences.getString(camionError.getId()+"-nombreCamion", "");

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Advertencia")
                        .setContentText("No se reciben posiciones de " + nombre)
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

    /**
     * Obtiene las areas de Firebase
     * @param areasRef
     */
    private void inicializarAreas(DatabaseReference areasRef) {
        Log.i("JobScheduler", "inicializarBasesOperativas");
        areasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String idArea = snapshot.getValue(BaseOperativa.class).getIdentificador();
                    BaseOperativa baseOperativa = null;
                    if(!IDsAreas.contains(idArea)) {
                        baseOperativa = snapshot.getValue(BaseOperativa.class);
                        IDsAreas.add(idArea);
                        listaBasesOperativas.add(baseOperativa);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.i("JobScheduler", "endInicializarAreas");
    }

    /**
     * Método encargado de escuchar a Firebase con las actualizaciones de los camiones
     * @param camionesRef
     */
    public void inicializarListaCamiones(DatabaseReference camionesRef) {
        Log.i("JobScheduler", "obtenerErrores");

        camionesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Camion camion = null;
                if (!listaIdsCamiones.contains(id)) { //Si la ID no está en la lista añadimos el camion
                    camion = new Camion(id);
                    listaIdsCamiones.add(id);
                    listaCamiones.add(camion);
                }
                else {
                    for (int i = 0; i < listaCamiones.size(); i++)
                        if (listaCamiones.get(i).getId().compareTo(id) == 0) {
                            camion = listaCamiones.get(i);
                            camion.clearPosiciones();
                            camion.clearHoras();
                        }
                }

                final Camion camionPos = camion;
                actualizarCamion(camionPos, dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Camion camion = null;
                if (!listaIdsCamiones.contains(id)) { //Si la ID no está en la lista añadimos el camion
                    camion = new Camion(id);
                    listaIdsCamiones.add(id);
                    listaCamiones.add(camion);
                }
                else {
                    for (int i = 0; i < listaCamiones.size(); i++)
                        if (listaCamiones.get(i).getId().compareTo(id) == 0) {
                            camion = listaCamiones.get(i);
                        }
                }

                final Camion camionPos = camion;
                actualizarCamion(camionPos, dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Log.i("JobScheduler", "endInicializarListaCamiones");

    }

    /**
     * Método encargado de actualizar las posiciones de cada camión
     * @param camionPos
     * @param snapshot
     */
    private void actualizarCamion(final Camion camionPos, final DataSnapshot snapshot) {
        Log.i("JobScheduler", "actualizarCamion");
        Query q = snapshot.child("rutas").child("ruta_actual").getRef().orderByKey().limitToLast(1);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Posicion posicion = child.getValue(Posicion.class);
                        camionPos.setPosicion(posicion);
                        long time = camionPos.getUltimaPosicion().getTime();
                        camionPos.setHoras(time);
                    }
                    comprobarHoras(camionPos);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        Log.i("JobScheduler", "endActualizarCamion");

    }

    private void comprobarHoras(final Camion camionNotif) {
        Log.i("JobScheduler", "ListaCamiones comprobarHoras: " + listaCamiones.size());

        Log.i("JobScheduler", "camionNotif : listaCamiones");
        Log.i("JobScheduler", camionNotif.getId() + " posiciones: " + camionNotif.getPosicionesList().size());
        if (camionNotif.getPosicionesList().size() > 0) {

            camionesRef.child(camionNotif.getId()).child("frecuencia_errores").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("JobScheduler", "FrecuenciasRef");
                    long frecuencia;
                    if (dataSnapshot.exists()) {
                        frecuencia = (long) dataSnapshot.getValue() * 60 * 1000;
                        Log.i("FRECUENCIA", String.valueOf(frecuencia));
                    } else {
                        frecuencia = 10 * 60 * 1000; //Frecuencia por defecto
                    }

                    Log.i("FRECUENCIA_DESPUES", String.valueOf(frecuencia));

                    long ultimaHora = camionNotif.getUltimaPosicion().getTime();
                    Date horaActualDate = Calendar.getInstance().getTime();
                    long horaActual = horaActualDate.getTime();
                    long diferencia = horaActual - ultimaHora;

                    boolean dentro = camionEnArea(camionNotif, listaBasesOperativas);
                    Log.i("JobScheduler BaseOperativa", "Camion " + camionNotif.getId() + " en area: " + dentro);

                    if (diferencia >= frecuencia && !dentro) {
                        enviarNotificacion(camionNotif, notifId);
                        ErrorNotificacion errorNotificacion = new ErrorNotificacion(camionNotif.getId(), diferencia, horaActual);
                        erroresRef.push().setValue(errorNotificacion);
                        notifId++;
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
