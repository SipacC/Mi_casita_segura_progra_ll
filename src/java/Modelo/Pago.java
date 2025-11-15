package Modelo;

import java.util.Date;

public class Pago {
    private int idPago;
    private int idUsuario;   // FK Usuarios
    private int idTipo;      // FK TipoPago
    private int idMetodo;    // FK MetodoPago
    private Integer idTarjeta; // FK Tarjeta (opcional)
    private Date fechaPago;
    private double monto;
    private double mora;
    private String observaciones;
    private String estado;

    // 🔹 Campos JOIN y auxiliares
    private String nombreTipo;
    private String nombreMetodo;
    private String nombreTarjeta;

    // 🔹 Nuevos campos de periodo
    private int mesPagado;
    private int anioPagado;

    // ===============================
    // 🔸 Constructores
    // ===============================

    public Pago() {}

    public Pago(int idPago, int idUsuario, int idTipo, int idMetodo, Integer idTarjeta,
                Date fechaPago, double monto, double mora,
                String observaciones, String estado, String nombreTipo,
                int mesPagado, int anioPagado) {
        this.idPago = idPago;
        this.idUsuario = idUsuario;
        this.idTipo = idTipo;
        this.idMetodo = idMetodo;
        this.idTarjeta = idTarjeta;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.mora = mora;
        this.observaciones = observaciones;
        this.estado = estado;
        this.nombreTipo = nombreTipo;
        this.mesPagado = mesPagado;
        this.anioPagado = anioPagado;
    }

    // ===============================
    // 🔸 Getters y Setters
    // ===============================

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public int getIdMetodo() {
        return idMetodo;
    }

    public void setIdMetodo(int idMetodo) {
        this.idMetodo = idMetodo;
    }

    public Integer getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(Integer idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getMora() {
        return mora;
    }

    public void setMora(double mora) {
        this.mora = mora;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public String getNombreMetodo() {
        return nombreMetodo;
    }

    public void setNombreMetodo(String nombreMetodo) {
        this.nombreMetodo = nombreMetodo;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }

    public int getMesPagado() {
        return mesPagado;
    }

    public void setMesPagado(int mesPagado) {
        this.mesPagado = mesPagado;
    }

    public int getAnioPagado() {
        return anioPagado;
    }

    public void setAnioPagado(int anioPagado) {
        this.anioPagado = anioPagado;
    }
}
