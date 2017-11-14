package com.example.guill.fhisa_admin.Opciones;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.guill.fhisa_admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AlbaranActivity extends AppCompatActivity {

    TextView tvId, tvNAlb, tvVehicle, tvExpedition, tvTransportDate,
    tvLoader, tvOrigin, tvDestination, tvPrice, tvNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albaran);

        tvId = (TextView) findViewById(R.id.tv_id);
        tvNAlb = (TextView) findViewById(R.id.tv_n_alb);
        tvVehicle = (TextView) findViewById(R.id.tv_vehicle);
        tvExpedition = (TextView) findViewById(R.id.tv_transportDate);
        tvTransportDate = (TextView) findViewById(R.id.tv_loader);
        tvLoader = (TextView) findViewById(R.id.tv_loader);
        tvOrigin = (TextView) findViewById(R.id.tv_origin);
        tvDestination = (TextView) findViewById(R.id.tv_destination);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        tvNotes = (TextView) findViewById(R.id.tv_notes);

        //First of all, we need to create a String, which contains the above-mentioned JSON:
        String albaranJson = "{'id':167578,'n_alb':'93049','vehicle':{'id':2166,'driver':{'id':6307," +
                "'name':'Feito Barredo  Javier','dni':'45430853B'},'plate':'4802-CMN'," +
                "'trailer':{'id':1828,'plate':'R-5792-BBY'}," +
                "'shipper':{'id':5953,'name':'Marcelino Alvarez Alvarez','cif':'9390177J'," +
                "'address':{'street':'','cp':'','city':'','province':'','country':'Spain'}}," +
                "'phase':'R','cargo':{'id':1,'name':'ARENA (AF-T- 0/4-S-L)','amount':27740}," +
                "'tare':13260,'pma':40000},'expedition':'13/11/2017','transportDate':'13/11/2017'," +
                "'loader':{'id':3,'name':'CANTERA','cif':'B33482654'}," +
                "'origin':{'street':'San Juan de Villapañada s/n','cp':'33820','city':'Grado'," +
                "'province':'Asturias','country':'Spain'},'destination':{'id':1026014," +
                "'name':'ORV 2016 6425 CANTERA GRADO 3er TRIM. 2016','address':{'street':''," +
                "'cp':'33836','city':'','province':'','country':'Spain'},'customer':{'id':1026," +
                "'name':'Orovalle Minerals S.L.','cif':'B84963537'," +
                "'address':{'street':'Planta El Valle-Boins','cp':'33836','city':'Belmonte de Miranda'," +
                "'province':'Asturias','country':'Spain'}}},'price':0,'notes':''}";
        //Objecto Gson que maneja la conversion
       // Gson gson = new Gson();
        //Finally, we've to map from a JSON to a Java object with fromJson():
       // Albaran albaranObject = gson.fromJson(albaranJson, Albaran.class);

        JSONObject object = null;
        JSONObject info = null;
        try {
            object = new JSONObject(albaranJson);
            info = object.getJSONObject("vehicle");
            Map<String,String> albaran = new HashMap<String, String>();
            parse(info,albaran);
            String id = albaran.get("id");
            Log.i("LOG", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static Map<String,String> parse(JSONObject json , Map<String,String> out) throws JSONException {
        Iterator<String> keys = json.keys();
        while(keys.hasNext()){
            String key = keys.next();
            String val = null;
            try{
                JSONObject value = json.getJSONObject(key);
                parse(value,out);
            }catch(Exception e){
                val = json.getString(key);
            }

            if(val != null){
                out.put(key,val);
            }
        }
        return out;
    }
}
