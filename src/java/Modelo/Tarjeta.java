package Modelo;

import java.util.Date;

public class Tarjeta {
    private int idTarjeta;
    private int idUsuario; 
    private String nombreTarjeta; // NUEVO campo
    private String numeroTarjeta;
    private Date fechaVencimiento;
    private String cvv;
    private String nombreTitular;
    private String tipoTarjeta;
    private double saldo;

    // Constructor vacío
    public Tarjeta() {}

    // Constructor completo
    public Tarjeta(int idTarjeta, int idUsuario, String nombreTarjeta, String numeroTarjeta,
                   Date fechaVencimiento, String cvv, String nombreTitular,
                   String tipoTarjeta, double saldo) {
        this.idTarjeta = idTarjeta;
        this.idUsuario = idUsuario;
        this.nombreTarjeta = nombreTarjeta;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaVencimiento = fechaVencimiento;
        this.cvv = cvv;
        this.nombreTitular = nombreTitular;
        this.tipoTarjeta = tipoTarjeta;
        this.saldo = saldo;
    }

    // Getters y Setters
    public int getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(int idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
