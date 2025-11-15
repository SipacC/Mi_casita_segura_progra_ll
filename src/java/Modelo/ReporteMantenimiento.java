package Modelo;

import java.sql.Timestamp;

public class ReporteMantenimiento {
    private int idReporte;
    private int idResidente;
    private int idTipoInconveniente;
    private String descripcion;
    private Timestamp fechaIncidente;

    public ReporteMantenimiento() {
    }

    public ReporteMantenimiento(int idReporte, int idResidente, int idTipoInconveniente, String descripcion, Timestamp fechaIncidente) {
        this.idReporte = idReporte;
        this.idResidente = idResidente;
        this.idTipoInconveniente = idTipoInconveniente;
        this.descripcion = descripcion;
        this.fechaIncidente = fechaIncidente;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public int getIdResidente() {
        return idResidente;
    }

    public void setIdResidente(int idResidente) {
        this.idResidente = idResidente;
    }

    public int getIdTipoInconveniente() {
        return idTipoInconveniente;
    }

    public void setIdTipoInconveniente(int idTipoInconveniente) {
        this.idTipoInconveniente = idTipoInconveniente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFechaIncidente() {
        return fechaIncidente;
    }

    public void setFechaIncidente(Timestamp fechaIncidente) {
        this.fechaIncidente = fechaIncidente;
    }
}
