package com.example.guill.fhisa_admin.OpcionesCamion;

/*
public class DetallePosicionesCamion extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private ImageView btnBack;

    ArrayList<Camion> camiones;
    private RecyclerView listaPosiciones;
    ArrayList<String> posicionesString;
    ArrayList<String> posicionesAux;

    ArrayList<String> horasString;
    ArrayList<String> horasAux;

    ArrayList<String> h;
    ArrayList<String> p;

    String id;
    double altitude;
    double latitude;
    double longitude;
    float speed;
    long time;
    List<String> IDs;
    private TextView tvPosiciones;

    private Spinner spFechas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posiciones_camiones_recycler);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        if (toolbar!=null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listaPosiciones = (RecyclerView) findViewById(R.id.rvCamionIndividual);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listaPosiciones.setLayoutManager(llm); //Para q el recycleview se comporte como un LinearLayout

        this.spFechas = (Spinner) findViewById(R.id.spFechas);
  //      loadSpinnerFechas();


        inicializarListaPosiciones(1);
        this.spFechas = (Spinner) findViewById(R.id.spFechas);
        loadSpinnerFechas();
        inicializarAdaptador();


    }

    public void inicializarListaPosiciones(int num){

        final int numero = num;
        posicionesString = new ArrayList<>();
        posicionesAux = new ArrayList<>();
        camiones = new ArrayList<>();
        IDs = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        posicionesAux = (ArrayList<String>) getIntent().getSerializableExtra("posiciones");

        horasAux = (ArrayList<String>) getIntent().getSerializableExtra("horas");
        horasString = new ArrayList<>();

        h = new ArrayList<>();
        p = new ArrayList<>();

        final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final Date currentTime = Calendar.getInstance().getTime();
        String currentTimeString = df.format(currentTime);
        String reportDate = df.format(currentTime);
        final String day = (String) DateFormat.format("dd",   currentTime); // 31
        final String monthNumber  = (String) DateFormat.format("MM",   currentTime); // 10
        final String year         = (String) DateFormat.format("yyyy", currentTime); // 2017

        FirebaseDatabase database = FirebaseDatabase.getInstance(); //Cualquier referencia tiene que ser igual al mismo tipo pero cogiendo la instancia
        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);
        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {

                    Camion camion=null;

                    if (!IDs.contains(id)) {
                        camion = new Camion(id);
                        IDs.add(id);
                        camiones.add(camion);
                    }
                    else {
                        for (int i=0; i<camiones.size(); i++)
                            if (camiones.get(i).getId().compareTo(id)==0) {
                                camion = camiones.get(i);
                                camion.clearPosiciones();
                                posicionesAux.clear();
                                horasAux.clear();
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

                    for (int i = 0; i< camion.getPosicionesList().size(); i++) {
                        posicionesAux.add(camion.getPosicionesList().get(i).toString());
                    }

                    for (int i = 0; i < camion.getHorasList().size(); i++) {
                        horasAux.add(camion.getHorasList().get(i).toString());
                    }

                    Log.i("HORAS", " " + camion.getHorasList().size());


                    for (int i = 0; i < posicionesAux.size(); i++) {
                        if (!posicionesAux.get(i).startsWith("com")) {
                            posicionesString.add(posicionesAux.get(i));
                            horasString.add(horasAux.get(i));
                        }
                    }

/*

                    if (numero==1) {
                        for (int i = 0; i < horasString.size(); i++) {
                            final long horalong = Long.parseLong(horasString.get(i)); //Paso la hora del vector y la paso a long
                            Date date = new Date(horalong); //Cojo el long y la paso a date
                            String hora = df.format(date); //Paso la date a dd/mm/yyyy  hh:mm:ss

                            if (hora.startsWith(day + "/" + monthNumber + "/" + year)) {
                                p.add(posicionesString.get(i));
                                h.add(hora);
                            }
                        }
                        Log.i("RECYCLERNO", "" + h.size() + " " + p.size() + " " +posicionesString.size()+ " " +horasString.size() );
                    }


                    if (numero==3) {
                        for (int i = 0; i < horasString.size(); i++) {
                            final long horalong = Long.parseLong(horasString.get(i)); //Paso la hora del vector y la paso a long
                            Date date = new Date(horalong); //Cojo el long y la paso a date
                            String hora = df.format(date); //Paso la date a dd/mm/yyyy  hh:mm:ss

                            p.add(posicionesString.get(i));
                            h.add(hora);

                        }
                        Log.i("RECYCLERNO", "" + h.size() + " " + p.size() + " " +posicionesString.size()+ " " +horasString.size() );
                    }

*/

/*
                } //for snapshot (Iterador donde estan las IDs)
                adaptador.notifyDataSetChanged();
            } //onDataChange
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //ValueEventListener

    }

    public AdapterPosiciones adaptador;
    public void inicializarAdaptador(){
        //Crea un objeto de contacto adaptador y le pasa la lista que tenemos para hacer internamente lo configurado en esa activity

        adaptador = new AdapterPosiciones(posicionesString, horasString, id, this);
      //  adaptador = new AdapterPosiciones(p, h, id, this);
        listaPosiciones.setAdapter(adaptador);
    }



    /**
     * Populate the Spinner.
     */
/*    private void loadSpinnerFechas() {

        // Create an ArrayAdapter using the string array and a default spinner
        // layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.listFechas, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        this.spFechas.setAdapter(adapter);

        // This activity implements the AdapterView.OnItemSelectedListener
        this.spFechas.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long ident) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();

        if (parent.getItemAtPosition(pos).toString().equals("Hoy")) {
          //  inicializarListaPosiciones(1);
            // inicializarAdaptador();
                                     // spFechas.setSelection(0);
        } else if (parent.getItemAtPosition(pos).toString().equals("Última semana")) {

        } else if (parent.getItemAtPosition(pos).toString().equals("Todas las posiciones")) {
          //  inicializarListaPosiciones(3);
                               //spFechas.setSelection(2);
          //  inicializarAdaptador();
        }
        inicializarAdaptador();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Callback method to be invoked when the selection disappears from this
        // view. The selection can disappear for instance when touch is
        // activated or when the adapter becomes empty
    }




    //Para volver al fragment anterior cuando hacemos click y no al activity
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
*/