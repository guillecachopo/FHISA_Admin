package com.example.guill.fhisa_admin.OpcionesCamion;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Adapter.AdapterRutas;
import com.example.guill.fhisa_admin.Objetos.FirebaseReferences;
import com.example.guill.fhisa_admin.Objetos.Posicion;
import com.example.guill.fhisa_admin.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RutasActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Toolbar toolbar;

    /**
     * Adaptador del RecyclerView
     */
    AdapterRutas adaptador;

    /**
     * RecyclerView que se va a utilizar
     */
    private RecyclerView rvRutasCamion;

    /**
     * Lista de rutas
     */
    List<String> listaRutas = new ArrayList<>();

    /**
     * Base de datos Firebase a utilizar
     */
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * Hora de inicio de la ruta que obtendremos de coger la primera ruta
     */
    List<String> listaHorasInicioRuta = new ArrayList<>();

    EditText tvFechaElegida;
    ImageView btnBuscar;
    int anio, mes, dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        setToolbar(toolbar);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.fechas_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        rvRutasCamion = (RecyclerView) findViewById(R.id.rvRutasCamion);
        setLinearLayout(rvRutasCamion);

        tvFechaElegida = (EditText) findViewById(R.id.tvFechaElegida);
        //Para quitar el focus del Edit Text
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        tvFechaElegida.setCursorVisible(false);

        btnBuscar = (ImageView) findViewById(R.id.btnBuscar);

        Calendar calendar = java.util.Calendar.getInstance();
        anio = calendar.get(java.util.Calendar.YEAR);
        mes = calendar.get(java.util.Calendar.MONTH);
        dia = calendar.get(java.util.Calendar.DAY_OF_MONTH);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String sSelected=adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this,sSelected, Toast.LENGTH_SHORT).show();
        if (i==0) { //Hoy
            rvRutasCamion.setAdapter(null);
            listaHorasInicioRuta.clear();
            listaRutas.clear();
            inicializarRutasHoy(rvRutasCamion);
        } else if (i==1) { //Ultimos 7 dias
            rvRutasCamion.setAdapter(null);
            listaHorasInicioRuta.clear();
            listaRutas.clear();
            inicializarRutasSemana(rvRutasCamion);
        } else if (i==2) { //Ultimos 30 dias
            rvRutasCamion.setAdapter(null);
            listaHorasInicioRuta.clear();
            listaRutas.clear();
            inicializarRutasMes(rvRutasCamion);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Buscar rutas por fecha elegida cuando se hace click en el botón
     * @param view
     */
    public void buscarRutaPorFecha(View view) {
        rvRutasCamion.setAdapter(null);
        listaHorasInicioRuta.clear();
        listaRutas.clear();
        inicializarRutasFecha(rvRutasCamion);
    }

    /**
     * Acceder a la ruta elegida
     * @param view
     */
    public void irRutaElegida(View view) {
        TextView tvRuta = (TextView) findViewById(R.id.tvRuta);
        TextView tvHoraFinRuta = (TextView) findViewById(R.id.tvHoraFinRuta);

        Log.i("RutaElegida", tvRuta.getText().toString() + ", " + tvHoraFinRuta.getText().toString());
    }

    /**
     * Hacemos visible el parpadeo del cursor para elegir fecha
     * @param view
     */
    public void makeCursorVisible(View view) {
        tvFechaElegida.setCursorVisible(true);
    }

    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, anio, mes, dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int anio, int mes, int dia) {
            mostrarFecha(anio, mes+1, dia);
        }
    };

    private void mostrarFecha(int anio, int mes, int dia) {
        tvFechaElegida.setText(new StringBuilder().append(dia).append("/")
                .append(mes).append("/").append(anio));
    }

    private String getImei() {
        Bundle extras = getIntent().getExtras();
        String imei = extras.getString("imei");
        return imei;
    }

    private void inicializarRutasHoy(RecyclerView rvRutasCamion) {
        final String imei = getImei();
        Calendar calendar = Calendar.getInstance();

        final String hoyDia;
        if (String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)).length() == 1) {
            hoyDia = "0"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            hoyDia = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }

        final String hoyMes;
        if (String.valueOf((calendar.get(Calendar.MONTH)+1)).length() == 1) {
            hoyMes = "0"+String.valueOf((calendar.get(Calendar.MONTH)+1));
        } else {
            hoyMes = String.valueOf((calendar.get(Calendar.MONTH)+1));
        }

        final String hoyYear = String.valueOf(calendar.get(Calendar.YEAR));

        DatabaseReference rutasRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE)
                .child(imei).child("rutas").child("rutas_completadas");

        rutasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //rutas

                for (DataSnapshot rutaNombre : dataSnapshot.getChildren()) {
                    String ruta = rutaNombre.getKey();
                    String[] parts = ruta.split("_");
                    String fechaRuta = parts[1];
                    Log.i("FechaRuta", fechaRuta + ", " + hoyYear+hoyMes+hoyDia);
                    if (fechaRuta.startsWith(hoyYear+hoyMes+hoyDia))
                    listaRutas.add(rutaNombre.getKey());
                }

                Log.i("Lista Rutas", String.valueOf(listaRutas.size()));
                getHoraInicioRuta(dataSnapshot, listaRutas, imei);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarRutasSemana(RecyclerView rvRutasCamion) {
        final String imei = getImei();
        final Calendar calendar = Calendar.getInstance();
        final int hoyYear = calendar.get(Calendar.YEAR);
        final int hoySemana =calendar.get(Calendar.WEEK_OF_YEAR);

        DatabaseReference rutasRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE)
                .child(imei).child("rutas").child("rutas_completadas");

        rutasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //rutas

                for (DataSnapshot rutaNombre : dataSnapshot.getChildren()) {
                    String ruta = rutaNombre.getKey();
                    String[] parts = ruta.split("_");
                    String fechaRuta = parts[1];

                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    Date dateRuta = new Date();
                    try {
                        dateRuta = df.parse(fechaRuta);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendarRuta = Calendar.getInstance();
                    calendarRuta.setTime(dateRuta);
                    int rutaSemana = calendarRuta.get(Calendar.WEEK_OF_YEAR);
                    int rutaYear = calendarRuta.get(Calendar.YEAR);

                    Log.i("Calendario", "hoySemana: " + hoySemana + ", rutaSemana: " + rutaSemana);

                    if (rutaYear == hoyYear && rutaSemana == hoySemana)
                        listaRutas.add(rutaNombre.getKey());
                }

                Log.i("Lista Rutas", String.valueOf(listaRutas.size()));
                getHoraInicioRuta(dataSnapshot, listaRutas, imei);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarRutasMes(RecyclerView rvRutasCamion) {
        final String imei = getImei();
        final Calendar calendar = Calendar.getInstance();
        final int hoyYear = calendar.get(Calendar.YEAR);
        final int hoyMes =calendar.get(Calendar.MONTH);

        DatabaseReference rutasRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE)
                .child(imei).child("rutas").child("rutas_completadas");

        rutasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //rutas

                for (DataSnapshot rutaNombre : dataSnapshot.getChildren()) {
                    String ruta = rutaNombre.getKey();
                    String[] parts = ruta.split("_");
                    String fechaRuta = parts[1];

                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    Date dateRuta = new Date();
                    try {
                        dateRuta = df.parse(fechaRuta);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendarRuta = Calendar.getInstance();
                    calendarRuta.setTime(dateRuta);
                    int rutaMes = calendarRuta.get(Calendar.MONTH);
                    int rutaYear = calendarRuta.get(Calendar.YEAR);


                    if (rutaYear == hoyYear && rutaMes == hoyMes)
                        listaRutas.add(rutaNombre.getKey());
                }

                Log.i("Lista Rutas", String.valueOf(listaRutas.size()));
                getHoraInicioRuta(dataSnapshot, listaRutas, imei);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarRutasFecha(RecyclerView rvRutasCamion) {
        final String imei = getImei();

        String fechaElegida = tvFechaElegida.getText().toString();
        Log.i("Fechas", fechaElegida);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateRutaElegida = new Date();
        try {
            dateRutaElegida = dateFormat.parse(fechaElegida);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendarFechaElegida = Calendar.getInstance();
        calendarFechaElegida.setTime(dateRutaElegida);
        dia = calendarFechaElegida.get(Calendar.DAY_OF_MONTH);
        mes = calendarFechaElegida.get(Calendar.MONTH);
        anio = calendarFechaElegida.get(Calendar.YEAR);
        Log.i("Fechas", "Introducida: " + "Dia: " + dia + " , mes: " + mes + ", año: " + anio);


        DatabaseReference rutasRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE)
                .child(imei).child("rutas").child("rutas_completadas");

        rutasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //rutas

                for (DataSnapshot rutaNombre : dataSnapshot.getChildren()) {
                    String ruta = rutaNombre.getKey();
                    String[] parts = ruta.split("_");
                    String fechaRuta = parts[1];

                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    Date dateRuta = new Date();
                    try {
                        dateRuta = df.parse(fechaRuta);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendarRuta = Calendar.getInstance();
                    calendarRuta.setTime(dateRuta);
                    int rutaDia = calendarRuta.get(Calendar.DAY_OF_MONTH);
                    int rutaMes = calendarRuta.get(Calendar.MONTH);
                    int rutaYear = calendarRuta.get(Calendar.YEAR);

                    Log.i("Fechas", "Firebase: " + "Dia: " + rutaDia + " , mes: " + rutaMes + ", año: " + rutaYear);

                    if (rutaYear == anio && rutaMes == mes && rutaDia == dia)
                        listaRutas.add(rutaNombre.getKey());
                }

                Log.i("Lista Rutas", String.valueOf(listaRutas.size()));
                getHoraInicioRuta(dataSnapshot, listaRutas, imei);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getHoraInicioRuta(DataSnapshot dataSnapshot, final List<String> listaRutas, final String imei) {

        for (String ruta : listaRutas) {
            dataSnapshot.child(ruta).getRef().orderByKey().limitToFirst(1).
                    addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Posicion posicion = dataSnapshot.getValue(Posicion.class);
                            DateFormat format = new SimpleDateFormat("HH:mm");
                            Date date = new Date(posicion.getTime());
                            String horaInicioRuta = format.format(date);
                            listaHorasInicioRuta.add(horaInicioRuta);

                            if (listaHorasInicioRuta.size() == listaRutas.size()) {
                                inicializarAdaptador(rvRutasCamion, listaHorasInicioRuta, imei);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        }

    }

    /**
     * Método encargado de crear el LinearLayout para el RecyclerView
     * @param recyclerView
     */
    private void setLinearLayout(RecyclerView recyclerView) {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm); //Para q el RecyclerView se comporte como un LinearLayout
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    /**
     * Método encargado de inicializar el adaptador del RecyclerView
     * @param recyclerView
     */
    private void inicializarAdaptador(RecyclerView recyclerView, List<String> listaHorasInicioRuta, String imei) {
        //Crea un objeto de contacto adaptador y le pasa la lista que tenemos para hacer internamente lo configurado en esa activity
        adaptador = new AdapterRutas(listaRutas, listaHorasInicioRuta, this, imei);
        recyclerView.setAdapter(adaptador);
    }


    /**
     * Método para activar la toolbar
     * @param toolbar
     */
    private void setToolbar(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Método empleado para volver al fragment anterior cuando se pulsa atrás
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }
}
