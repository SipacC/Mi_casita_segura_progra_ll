package Modelo;

import java.sql.Timestamp;

public class Incidente {
    private int idIncidente;
    private int idResidente;
    private Integer idGuardia;
    private int idTipoIncidente;
    private String descripcion;
    private Timestamp fechaReporte;
    private String estado;
    private String prioridad;

    public Incidente() {}

    public Incidente(int idResidente, int idTipoIncidente, String descripcion) {
        this.idResidente = idResidente;
        this.idTipoIncidente = idTipoIncidente;
        this.descripcion = descripcion;
    }

    public Incidente(int idResidente, int idTipoIncidente, String descripcion, Timestamp fechaReporte) {
        this.idResidente = idResidente;
        this.idTipoIncidente = idTipoIncidente;
        this.descripcion = descripcion;
        this.fechaReporte = fechaReporte;
    }
    
    

    public int getIdIncidente() {
        return idIncidente;
    }

    public void setIdIncidente(int idIncidente) {
        this.idIncidente = idIncidente;
    }

    public int getIdResidente() {
        return idResidente;
    }

    public void setIdResidente(int idResidente) {
        this.idResidente = idResidente;
    }

    public Integer getIdGuardia() {
        return idGuardia;
    }

    public void setIdGuardia(Integer idGuardia) {
        this.idGuardia = idGuardia;
    }

    public int getIdTipoIncidente() {
        return idTipoIncidente;
    }

    public void setIdTipoIncidente(int idTipoIncidente) {
        this.idTipoIncidente = idTipoIncidente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(Timestamp fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
}
