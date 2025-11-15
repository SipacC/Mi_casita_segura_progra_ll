package Modelo;

import java.sql.Date;
import java.sql.Time;

public class Reserva {
    private int id_reserva;
    private int id_usuario;
    private int id_area;
    private String nombreArea;
    private Date fecha_reserva;
    private Time hora_inicio;
    private Time hora_fin;
    private String estado;

    public int getId_reserva() { return id_reserva; }
    public void setId_reserva(int id_reserva) { this.id_reserva = id_reserva; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public int getId_area() { return id_area; }
    public void setId_area(int id_area) { this.id_area = id_area; }

    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String nombreArea) { this.nombreArea = nombreArea; }

    public Date getFecha_reserva() { return fecha_reserva; }
    public void setFecha_reserva(Date fecha_reserva) { this.fecha_reserva = fecha_reserva; }

    public Time getHora_inicio() { return hora_inicio; }
    public void setHora_inicio(Time hora_inicio) { this.hora_inicio = hora_inicio; }

    public Time getHora_fin() { return hora_fin; }
    public void setHora_fin(Time hora_fin) { this.hora_fin = hora_fin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
