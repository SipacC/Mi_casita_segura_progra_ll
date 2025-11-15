package Modelo;

import java.sql.Timestamp;

public class ArchivoMensaje {
    private int idArchivo;
    private int idMensaje;
    private String rutaArchivo;
    private String tipoMime;
    private String nombreOriginal;
    private Timestamp fechaSubida;

    public ArchivoMensaje() {
    }

    public ArchivoMensaje(int idArchivo, int idMensaje, String rutaArchivo,
                          String tipoMime, String nombreOriginal, Timestamp fechaSubida) {
        this.idArchivo = idArchivo;
        this.idMensaje = idMensaje;
        this.rutaArchivo = rutaArchivo;
        this.tipoMime = tipoMime;
        this.nombreOriginal = nombreOriginal;
        this.fechaSubida = fechaSubida;
    }

    public int getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(int idArchivo) {
        this.idArchivo = idArchivo;
    }

    public int getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public Timestamp getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Timestamp fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
}
