package com.example.guill.fhisa_admin.Adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guill.fhisa_admin.Objetos.ErrorNotificacion;
import com.example.guill.fhisa_admin.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterErrores extends RecyclerView.Adapter<AdapterErrores.ErroresViewHolder>{

    List<ErrorNotificacion> errores;
    Activity activity;

    public static class ErroresViewHolder extends RecyclerView.ViewHolder {

        TextView tvIdError, tvImeiError, tvHoraError,tvHoraPosicionError;
        CardView cvError;

        ErroresViewHolder(View itemView) {
             super(itemView);
             tvIdError = itemView.findViewById(R.id.tvIdError);
             tvImeiError = itemView.findViewById(R.id.tvImeiError);
             tvHoraError = itemView.findViewById(R.id.tvHoraError);
             tvHoraPosicionError = itemView.findViewById(R.id.tvHoraPosicionError);
             cvError = itemView.findViewById(R.id.cvError);
           }
    }

    //Constructor para manejar los datos del RecyclerView
    public AdapterErrores(List<ErrorNotificacion> errores, Activity activity) {

        this.errores = errores;
        this.activity = activity;
    }

   //Número de elementos presentes en los datos
    @Override
    public int getItemCount() {
        return errores.size();
    }


    //Va a inflar el layout y lo pasara al viewholder para que el obtenga los views
    @Override
    public ErroresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_error, parent, false);
        ErroresViewHolder holder = new ErroresViewHolder(v); //CamionesViewHolder es el metodo constructor y recibe un view (le pasamos el contacto inflado como un view)
                                                                //para q empiece a tomarlos como elementos
        return holder;
    }

    //Asociar cada elemento de la lista con cada view
    @Override
    public void onBindViewHolder(ErroresViewHolder holder, int position) {
        final ErrorNotificacion error = errores.get(position);
        String imeiError = error.getImei();
	    long horaErrorLong = error.getHoraActual();
        long horaPosicionErrorLong = error.getHoraPosicion();
	    //El long de la hora se convierte formatea a un Date y de date se pasa a String
	    DateFormat formatoHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    Date horaErrorDate = new Date(horaErrorLong);
        Date horaPosicionErrorDate = new Date(horaPosicionErrorLong);
	    String horaError = formatoHora.format(horaErrorDate);
        String horaPosicionError = formatoHora.format(horaPosicionErrorDate);
	    //Cojo el alias del camión si es que tiene
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String alias = preferences.getString(imeiError+"-nombreCamion", imeiError);
        // Si el alias es distinto al imei, se muestra, sino, no.  
        if (!alias.equals(imeiError)) {
            holder.tvIdError.setText("ID: " + alias);
        } else {
            holder.tvIdError.setText("ID: ");
        }
        holder.tvImeiError.setText("IMEI: " + imeiError);
        holder.tvHoraError.setText("Hora de la notificación: " + horaError);
        holder.tvHoraPosicionError.setText("Hora de la última posición: " + horaPosicionError);

 

    }



    
}
