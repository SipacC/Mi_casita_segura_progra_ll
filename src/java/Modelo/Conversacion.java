package Modelo;

import java.sql.Timestamp;

public class Conversacion {
    private int idConversacion;
    private int idResidente;
    private int idGuardia;
    private int idAdministrador;
    private String tipoConversacion;
    private Timestamp fechaCreacion;
    private String estado;
    private Timestamp ultimoMensaje;

    private String nombreGuardia;
    private String nombreResidente;

    public Conversacion() {}

    public Conversacion(int idConversacion, int idResidente, int idGuardia,
                        Timestamp fechaCreacion, String estado, Timestamp ultimoMensaje) {
        this.idConversacion = idConversacion;
        this.idResidente = idResidente;
        this.idGuardia = idGuardia;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.ultimoMensaje = ultimoMensaje;
    }


    public int getIdConversacion() { return idConversacion; }
    public void setIdConversacion(int idConversacion) { this.idConversacion = idConversacion; }

    public int getIdResidente() { return idResidente; }
    public void setIdResidente(int idResidente) { this.idResidente = idResidente; }

    public int getIdGuardia() { return idGuardia; }
    public void setIdGuardia(int idGuardia) { this.idGuardia = idGuardia; }

    public int getIdAdministrador() { return idAdministrador; }
    public void setIdAdministrador(int idAdministrador) { this.idAdministrador = idAdministrador; }

    public String getTipoConversacion() { return tipoConversacion; }
    public void setTipoConversacion(String tipoConversacion) { this.tipoConversacion = tipoConversacion; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Timestamp getUltimoMensaje() { return ultimoMensaje; }
    public void setUltimoMensaje(Timestamp ultimoMensaje) { this.ultimoMensaje = ultimoMensaje; }

    public String getNombreGuardia() { return nombreGuardia; }
    public void setNombreGuardia(String nombreGuardia) { this.nombreGuardia = nombreGuardia; }

    public String getNombreResidente() { return nombreResidente; }
    public void setNombreResidente(String nombreResidente) { this.nombreResidente = nombreResidente; }
}
