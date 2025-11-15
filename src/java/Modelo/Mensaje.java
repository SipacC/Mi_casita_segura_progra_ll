package Modelo;

import java.sql.Timestamp;

public class Mensaje {
    private int idMensaje;
    private int idConversacion;
    private int idEmisor;
    private String contenido;
    private Timestamp fechaEnvio;
    private boolean leido;
    private String tipo;
    private Integer idMensajeRespuesta;

    public Mensaje() {
    }

    public Mensaje(int idMensaje, int idConversacion, int idEmisor,
                   String contenido, Timestamp fechaEnvio, boolean leido,
                   String tipo, Integer idMensajeRespuesta) {
        this.idMensaje = idMensaje;
        this.idConversacion = idConversacion;
        this.idEmisor = idEmisor;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.leido = leido;
        this.tipo = tipo;
        this.idMensajeRespuesta = idMensajeRespuesta;
    }

    public int getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    public int getIdConversacion() {
        return idConversacion;
    }

    public void setIdConversacion(int idConversacion) {
        this.idConversacion = idConversacion;
    }

    public int getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(int idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Timestamp getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Timestamp fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getIdMensajeRespuesta() {
        return idMensajeRespuesta;
    }

    public void setIdMensajeRespuesta(Integer idMensajeRespuesta) {
        this.idMensajeRespuesta = idMensajeRespuesta;
    }
}
