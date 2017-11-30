package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guill.fhisa_admin.Objetos.Camion;
import com.example.guill.fhisa_admin.Opciones.OpcionesCamionActivity;
import com.example.guill.fhisa_admin.Opciones.VehiculoActivity;
import com.example.guill.fhisa_admin.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by guill on 21/09/2017.
 */


public class Adapter extends RecyclerView.Adapter<Adapter.CamionesViewHolder>{

    List<Camion> camiones;
    Activity activity;

    public Adapter(List<Camion> camiones, Activity activity) {

        this.camiones = camiones;
        this.activity = activity;
    }

    //Va a inflar el layout y lo pasara al viewholder para que el obtenga los views
    @Override
    public CamionesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_camiones, parent, false);
        CamionesViewHolder holder = new CamionesViewHolder(v); //CamionesViewHolder es el metodo constructor y recibe un view (le pasamos el contacto inflado como un view)
                                                                //para q empiece a tomarlos como elementos
        return holder;
    }

    //Asciar cada elemento de la lista con cada view
    @Override
    public void onBindViewHolder(CamionesViewHolder holder, int position) {
        final Camion camion = camiones.get(position);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        final String id = camion.getId();
//        String latitud = Double.toString(camion.getUltimaPosicion().getLatitude());
 //       String longitud = Double.toString(camion.getUltimaPosicion().getLongitude());
        String nombre = preferences.getString(id+"-nombreCamion", "");

        if (camion.getPosicionesList().size() > 0) { //Damos tiempo a los datos para que carguen
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date(camion.getUltimaPosicion().getTime());
            String hora = format.format(date);

            //Geocoder para hacer geolocalización inversa
            Geocoder geocoder = new Geocoder(holder.tvImei.getContext(), Locale.getDefault());
            try {
                List<Address> list = geocoder.getFromLocation(camion.getUltimaPosicion().getLatitude(), camion.getUltimaPosicion().getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address direccion = list.get(0);
                    holder.tvUltimaPosicion.setText("Ultima posicion: " + direccion.getAddressLine(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (nombre.equals(id)) { //Si se ha reestablecido el imei como nombre del camion
                holder.tvIdentificador.setText("Identificador: ");
                //Sin lo anterior, apareceria -> Identificador: 3843828217 (el imei) y no un blank

            } else {
                holder.tvIdentificador.setText("Identificador: " + nombre);
            }
            holder.tvImei.setText("IMEI: " + id);
            holder.tvHora.setText("Hora: " + hora);

            final ArrayList<String> posicionesString = new ArrayList<>();
            final ArrayList<String> horasString = new ArrayList<>();

            for (int i = 0; i < camion.getPosicionesList().size(); i++) {
                posicionesString.add(String.valueOf(camion.getPosicionesList().get(i).getLatitude()) + "," + camion.getPosicionesList().get(i).getLongitude());
            }


            for (int i = 0; i < camion.getHorasList().size(); i++) {
                horasString.add(String.valueOf(camion.getPosicionesList().get(i).getTime()));
            }


            holder.cvCamion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(activity, DetallePosicionesCamion.class);
                    Intent intent = new Intent(activity, OpcionesCamionActivity.class);
                    intent.putExtra("id", id);
                    intent.putStringArrayListExtra("posiciones", posicionesString);
                    intent.putStringArrayListExtra("horas", horasString);
                    activity.startActivity(intent);
                }
            });

            holder.cvCamion.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(activity, "Datos del camión", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, VehiculoActivity.class);
                    intent.putExtra("id", id);
                    activity.startActivity(intent);
                    return false;
                }

            });

        }

    }

    @Override
    public int getItemCount() {
        return camiones.size();
    }

    public static class CamionesViewHolder extends RecyclerView.ViewHolder {

        TextView tvIdentificador, tvImei, tvUltimaPosicion, tvHora;
        CardView cvCamion;

        public CamionesViewHolder(View itemView) {
            super(itemView);
            tvIdentificador = itemView.findViewById(R.id.tvNombreCV);
            tvImei = itemView.findViewById(R.id.tvImeiCV);
            tvUltimaPosicion = itemView.findViewById(R.id.tvPosicionCV);
            tvHora = itemView.findViewById(R.id.tvHoraCV);
            cvCamion = itemView.findViewById(R.id.cvCamion);
        }
    }

}


