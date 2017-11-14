package com.example.guill.fhisa_admin;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Adapter.PageAdapter;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

public class MainActivity extends AppCompatActivity {

    TextView tvCamionInfo;
    double lat = 0.0; //Latitud inicial para el marcador de posicion inicial
    double lng = 0.0; //Longitud inicial para el marcador de posicion inicial
    String imei = "";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setUpViewPager();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
            //tabLayout.setVisibility(View.GONE);
        }


        //tvCamionInfo = (TextView) findViewById(R.id.tvCamion);

        FirebaseDatabase database = FirebaseDatabase.getInstance(); //Cualquier referencia tiene que ser igual al mismo tipo pero cogiendo la instancia
       // final DatabaseReference camionesRef = database.getReference(FirebaseReferences.FHISA_REFERENCE); //referencia a la bdd de firebase
       // camionesRef.child(FirebaseReferences.CAMION_REFERENCE).addValueEventListener(new ValueEventListener()

        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);
        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //Para poner en orbita los fragments
    private ArrayList<Fragment> agregarFragment(){
        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new MapsActivity());
        fragments.add(new RecyclerViewFragment());

        return fragments; //Ya tenemos los fragments en un arraylist
    }

    private void setUpViewPager(){
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), agregarFragment()));
        //Con la linea anterior pasamos soporte fragment manager y la lista de fragments q queremos agregar al page adapter (viewpager)
        tabLayout.setupWithViewPager(viewPager); //Lo agregamos al tabLayout

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_map_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_truck_white);
    }

    //-----MENU OPTIONS---------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones,menu);
        return true;
    }

    private boolean isStarted = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //El item del menu seleccionado

        switch (item.getItemId()){
            case R.id.menuNotificaciones:


                ComponentName cp = new ComponentName(this, NotificationJobScheduler.class);
                JobInfo jb;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    jb = new JobInfo.Builder(1, cp)
                            .setBackoffCriteria(4000, JobInfo.BACKOFF_POLICY_LINEAR)
                            .setPersisted(true)
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setRequiresCharging(false)
                            .setRequiresDeviceIdle(false)
                            .setMinimumLatency(10000) //Setear en milisegundos cada cuánto tiempo queremos que se ejecute el job para comprobar posiciones
                            .build();
                } else {
                    jb = new JobInfo.Builder(1, cp)
                            .setBackoffCriteria(4000, JobInfo.BACKOFF_POLICY_LINEAR)
                            .setPersisted(true)
                            .setPeriodic(10000) //Setear en milisegundos cada cuánto tiempo queremos que se ejecute el job para comprobar posiciones
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .setRequiresCharging(false)
                            .setRequiresDeviceIdle(false)
                            .build();
                }

                JobScheduler js = JobScheduler.getInstance(this);
                js.schedule(jb);
                Toast.makeText(this, "Servicio de notificaciones activo", Toast.LENGTH_LONG).show();
                isStarted = true;
                return true;

            case R.id.menuStopNotificaciones:
                JobScheduler js2 = JobScheduler.getInstance(this);
                js2.cancelAll();
                Toast.makeText(this, "Servicio de notificaciones inactivo", Toast.LENGTH_LONG).show();
                isStarted = false;
                return true;

            case R.id.menuCopiaSeguridad:
                AlertDialog.Builder alertDialogFirebase = new AlertDialog.Builder(MainActivity.this);
                alertDialogFirebase
                        .setIcon(R.drawable.ic_fhisa)
                        .setTitle("Exportación de Firebase Database")
                        .setMessage("Firebase Database es la base de datos utilizada para guardar las posiciones de cada camión y las areas correspondientes a las zonas libres de notificaciones. Las copias de seguridad se guardarán en del directorio raíz del dispositivo dentro la carpeta FHISAFirebase.")
                        .setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new DownloadFileFromURL().execute("https://fhisaservicio.firebaseio.com/.json");
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog b = alertDialogFirebase.create();
                b.show();
                return true;

            case R.id.menuImportarCopia:
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                File fhisaDir = new File(Environment.getExternalStorageDirectory(), "FHISAFirebase"); //Cogemos el directorio donde se guardan los backups
                final String fhisaDirString = fhisaDir.toString();
                File[] files = fhisaDir.listFiles(); //Cogemos todos los ficheros de la carpeta
                ArrayList<String> fileNames = new ArrayList<>(); //Vector dnde guardaremos los nombres de los backups
                for (int i = 0; i < files.length; i++)
                {
                    fileNames.add(files[i].getName()); //Guardamos en nuestro vector de nombres todos los nombres
                }
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this); //AlertDialog para elegir cuál queremos importar
                builderSingle.setIcon(R.drawable.ic_fhisa);
                builderSingle.setTitle("Seleccionar copia de seguridad");
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice); //ArrayAdapter para el AlertDialog

                for (int i=0; i<fileNames.size(); i++) arrayAdapter.add(fileNames.get(i)); //Añadimos al ArrayAdapter nuestro Array de nombres

                builderSingle.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final String strName = arrayAdapter.getItem(which); //Este es el item seleccionado que pasaremos al parser
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                        builderInner.setIcon(R.drawable.ic_fhisa);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("La copia elegida es: ");
                        builderInner.setPositiveButton("IMPORTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                JSONParser parser = new JSONParser();

                                try {
                                    Object obj = parser.parse(new FileReader(fhisaDirString+"/"+strName)); //Cogemos el json elegido
                                    database.getReferenceFromUrl("https://fhisaservicio.firebaseio.com").setValue(obj);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                dialog.dismiss();
                            }
                        });
                        builderInner.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
                return true;

            case R.id.menuBorrarBD:
                    final FirebaseDatabase db = FirebaseDatabase.getInstance();
                    AlertDialog.Builder alertDialogBorrar = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBorrar
                            .setIcon(R.drawable.ic_fhisa)
                            .setTitle("ATENCIÓN: Borrado de Firebase Database")
                            .setMessage("Está a punto de borrar Firebase Database, esto conlleva la pérdida de todas las posiciones, asegúrese de tener guardada una copia de seguridad reciente.")
                            .setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    db.getReferenceFromUrl("https://fhisaservicio.firebaseio.com/camiones").removeValue();
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertdialog = alertDialogBorrar.create();
                    alertdialog.show();
                return true;




        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menuNotificaciones).setVisible(!isStarted);
        menu.findItem(R.id.menuStopNotificaciones).setVisible(isStarted);
        return true;
    }

    //-------------

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Descargando... Espere, por favor...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
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
            Toast.makeText(MainActivity.this, "JSON descargado en la ruta root/FHISAFirebase", Toast.LENGTH_LONG).show();

            pDialog.dismiss();
        }

    }

}
