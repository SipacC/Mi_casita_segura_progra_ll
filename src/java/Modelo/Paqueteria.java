package Modelo;

import java.sql.Timestamp;

public class Paqueteria {
    private int id_paqueteria;
    private String numero_guia;
    private int id_residente;
    private int id_agente_registra;
    private Integer id_agente_entrega; // puede ser null
    private String casa_residente;
    private String estado;
    private String observaciones;
    private Timestamp fecha_registro;
    private Timestamp fecha_entrega;

    // Campos adicionales para mostrar datos del JOIN
    private String residente;         // nombre + apellido del residente
    private String lote_residente;    // lote del residente (desde Usuarios)
    private String agente_registra;   // nombre + apellido del agente que registró
    private String agente_entrega;    // nombre + apellido del agente que entregó

    // ====== Constructores ======

    public Paqueteria() {}

    // Constructor con los campos principales
    public Paqueteria(int id_paqueteria, String numero_guia, int id_residente, int id_agente_registra,
                      Integer id_agente_entrega, String casa_residente, String estado,
                      String observaciones, Timestamp fecha_registro, Timestamp fecha_entrega) {
        this.id_paqueteria = id_paqueteria;
        this.numero_guia = numero_guia;
        this.id_residente = id_residente;
        this.id_agente_registra = id_agente_registra;
        this.id_agente_entrega = id_agente_entrega;
        this.casa_residente = casa_residente;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fecha_registro = fecha_registro;
        this.fecha_entrega = fecha_entrega;
    }

    // ====== Getters y Setters ======

    public int getId_paqueteria() {
        return id_paqueteria;
    }

    public void setId_paqueteria(int id_paqueteria) {
        this.id_paqueteria = id_paqueteria;
    }

    public String getNumero_guia() {
        return numero_guia;
    }

    public void setNumero_guia(String numero_guia) {
        this.numero_guia = numero_guia;
    }

    public int getId_residente() {
        return id_residente;
    }

    public void setId_residente(int id_residente) {
        this.id_residente = id_residente;
    }

    public int getId_agente_registra() {
        return id_agente_registra;
    }

    public void setId_agente_registra(int id_agente_registra) {
        this.id_agente_registra = id_agente_registra;
    }

    public Integer getId_agente_entrega() {
        return id_agente_entrega;
    }

    public void setId_agente_entrega(Integer id_agente_entrega) {
        this.id_agente_entrega = id_agente_entrega;
    }

    public String getCasa_residente() {
        return casa_residente;
    }

    public void setCasa_residente(String casa_residente) {
        this.casa_residente = casa_residente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Timestamp getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(Timestamp fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public Timestamp getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(Timestamp fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    // ====== Datos del JOIN ======

    public String getResidente() {
        return residente;
    }

    public void setResidente(String residente) {
        this.residente = residente;
    }

    public String getLote_residente() {
        return lote_residente;
    }

    public void setLote_residente(String lote_residente) {
        this.lote_residente = lote_residente;
    }

    public String getAgente_registra() {
        return agente_registra;
    }

    public void setAgente_registra(String agente_registra) {
        this.agente_registra = agente_registra;
    }

    public String getAgente_entrega() {
        return agente_entrega;
    }

    public void setAgente_entrega(String agente_entrega) {
        this.agente_entrega = agente_entrega;
    }
}
