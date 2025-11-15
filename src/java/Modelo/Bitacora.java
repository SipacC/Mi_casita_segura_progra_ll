package Modelo;

import java.sql.Timestamp;

public class Bitacora {
    private int id_bitacora;
    private int id_usuario;
    private String accion;
    private String modulo;
    private Timestamp fecha_hora;

    private String usuario_actor;    
    private String usuario_afectado; 


    public Bitacora() {}
    public Bitacora(int id_bitacora, int id_usuario, String accion, String modulo, Timestamp fecha_hora) {
        this.id_bitacora = id_bitacora;
        this.id_usuario = id_usuario;
        this.accion = accion;
        this.modulo = modulo;
        this.fecha_hora = fecha_hora;

        
    }
    public int getId_bitacora() {
        return id_bitacora;
    }

    public void setId_bitacora(int id_bitacora) {
        this.id_bitacora = id_bitacora;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Timestamp getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(Timestamp fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
    
    public String getUsuario_actor() {
        return usuario_actor;
    }

    public void setUsuario_actor(String usuario_actor) {
        this.usuario_actor = usuario_actor;
    }

    public String getUsuario_afectado() {
        return usuario_afectado;
    }

    public void setUsuario_afectado(String usuario_afectado) {
        this.usuario_afectado = usuario_afectado;
    }

}
