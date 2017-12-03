package com.example.guill.fhisa_admin.Objetos;

/**
 * Created by guill on 02/12/2017.
 */

public class AlbaranReducido {
    String id_albaran;
    String destino_cliente;
    String destino_obra;
    String fecha;
    String m3_carga;

    public void AlbaranReducido() {

    }

    public AlbaranReducido(String id_albaran, String destino_cliente, String destino_obra,
                           String fecha, String m3_carga) {
        this.id_albaran = id_albaran;
        this.destino_cliente = destino_cliente;
        this.destino_obra = destino_obra;
        this.fecha = fecha;
        this.m3_carga = m3_carga;
    }

    public String getId_albaran() {
        return id_albaran;
    }

    public void setId_albaran(String id_albaran) {
        this.id_albaran = id_albaran;
    }

    public String getDestino_cliente() {
        return destino_cliente;
    }

    public void setDestino_cliente(String destino_cliente) {
        this.destino_cliente = destino_cliente;
    }

    public String getDestino_obra() {
        return destino_obra;
    }

    public void setDestino_obra(String destino_obra) {
        this.destino_obra = destino_obra;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getM3_carga() {
        return m3_carga;
    }

    public void setM3_carga(String m3_carga) {
        this.m3_carga = m3_carga;
    }
}
