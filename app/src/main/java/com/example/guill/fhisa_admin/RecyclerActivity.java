package com.example.guill.fhisa_admin;


/*
public class RecyclerActivity extends AppCompatActivity {

    String id;
    double altitude;
    double latitude;
    double longitude;
    float speed;
    long time;
    List<String> IDs;

    RecyclerView rv;

    List<Camion> camiones;

    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        rv = (RecyclerView) findViewById(R.id.rvCamiones);

        rv.setLayoutManager(new LinearLayoutManager(this));

        camiones = new ArrayList<>();

        adapter = new Adapter(camiones);

        rv.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
     //   final DatabaseReference camionesRef = database.getReference(FirebaseReferences.FHISA_REFERENCE);
        final DatabaseReference camionesRef = database.getReference(FirebaseReferences.CAMIONES_REFERENCE);

       // camionesRef.child(FirebaseReferences.CAMION_REFERENCE).addValueEventListener(new ValueEventListener() {
        camionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot :
                        dataSnapshot.getChildren()) {

                    id = snapshot.getKey();
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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //Cada vez q cambien los datos se refresca





    }
}
*/

