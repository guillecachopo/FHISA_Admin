package com.example.guill.fhisa_admin;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Adapter.PageAdapter;
import com.example.guill.fhisa_admin.Mapa.MapsFragment;
import com.example.guill.fhisa_admin.OpcionesMenu.OpcionesMenuActivity;
import com.google.firebase.database.FirebaseDatabase;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ProgressBar progressBar;

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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

    }


    //Para poner en orbita los fragments
    private ArrayList<Fragment> agregarFragment(){
        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new MapsFragment());
        fragments.add(new ListadoCamionesFragment());
        fragments.add(new ErroresFragment());

        return fragments; //Ya tenemos los fragments en un arraylist
    }

    private void setUpViewPager(){
        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), agregarFragment()));
        //Con la linea anterior pasamos soporte fragment manager y la lista de fragments q queremos agregar al page adapter (viewpager)
        tabLayout.setupWithViewPager(viewPager); //Lo agregamos al tabLayout

        viewPager.setOffscreenPageLimit(2); //Para poder navegar entre fragments sin que se recarguen desde 0

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_map_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_truck_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_error_blanco);
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
                recibirNotificaciones();
                isStarted = true;
                return true;

            case R.id.menuStopNotificaciones:
                pararNotificaciones();
                isStarted = false;
                return true;

            case R.id.menuCopiaSeguridad:
                crearCopiaDB();
                return true;

            case R.id.menuImportarCopia:
                importarCopiaDB();
                return true;

            case R.id.menuBorrarBD:
                //borrarBD();
                return true;

            case R.id.menuLeyenda:
                infoLeyenda();
                return true;

            case R.id.menuOpciones:
                startActivity(new Intent(this, OpcionesMenuActivity.class));
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

    public void infoLeyenda() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.marcadores_leyenda, null);
        dialogBuilder.setView(dialogView);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }



    /**
     * Método empleado para recibir notificaciones
     * @return
     */
    public void recibirNotificaciones() {
        ComponentName cp = new ComponentName(this, NotificacionesScheduler.class);
        JobInfo jb;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jb = new JobInfo.Builder(1, cp)
                    .setBackoffCriteria(4000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .setPersisted(false)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .setMinimumLatency(1000 * 60 * 2) //Setear en milisegundos cada cuánto tiempo queremos que se ejecute el job para comprobar posiciones
                    .build();
        } else {
            jb = new JobInfo.Builder(1, cp)
                    .setBackoffCriteria(4000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .setPersisted(false)
                    .setPeriodic(1000 * 60 * 2) //Setear en milisegundos cada cuánto tiempo queremos que se ejecute el job para comprobar posiciones
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .build();
        }

        JobScheduler js = JobScheduler.getInstance(this);
        js.schedule(jb);
        Toast.makeText(this, "Servicio de notificaciones activo", Toast.LENGTH_LONG).show();
    }

    /**
     * Método empleado para dejar de recibir notificaciones
     */
    public void pararNotificaciones() {
        JobScheduler js2 = JobScheduler.getInstance(this);
        js2.cancelAll();
        Toast.makeText(this, "Servicio de notificaciones inactivo", Toast.LENGTH_LONG).show();
    }

    /**
     * Método empelado para crear una copia de la base de datos en el sistema
     */

    public void crearCopiaDB() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        AlertDialog.Builder alertDialogFirebase = new AlertDialog.Builder(MainActivity.this);
        alertDialogFirebase
                .setIcon(R.drawable.ic_fhisa)
                .setTitle("Exportación de Firebase Database")
                .setMessage("Firebase Database es la base de datos utilizada para guardar las posiciones de cada camión y las areas correspondientes a las zonas libres de notificaciones. Las copias de seguridad se guardarán en del directorio raíz del dispositivo dentro la carpeta FHISAFirebase.")
                .setPositiveButton("GUARDAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        permisosAlmacenamiento();

                        new CopiaSeguridadFirebase(getApplicationContext(), preferences, progressBar)
                                .execute("https://fhisaservicio.firebaseio.com/.json");
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
    }

    /**
     * Método empleado para importar  a Firebase una copia de seguridad guardada en el dispositivo
     */
    public void importarCopiaDB() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        File fhisaDir = new File(Environment.getExternalStorageDirectory(), "FHISAFirebase"); //Cogemos el directorio donde se guardan los backups
        final String fhisaDirString = fhisaDir.toString();
        File[] files = fhisaDir.listFiles(); //Cogemos todos los ficheros de la carpeta
        if (files!=null) {
            ArrayList<String> fileNames = new ArrayList<>(); //Vector donde guardaremos los nombres de los backups
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
        } else {
            Toast.makeText(getApplicationContext(), "No hay copias de seguridad almacenadas", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Método utilizado para borrar los camiones de la base de datos en la nube (FIREBASE)
     */
    public void borrarBD() {
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
    }

    /**
     * Solicitamos permisos de almacenamiento
     */
    public void permisosAlmacenamiento() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);

        }
    }

}
