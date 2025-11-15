package Modelo;

public class TipoInconveniente {
    private int idTipoInconveniente;
    private String nombre;

    public TipoInconveniente() {
    }

    public TipoInconveniente(int idTipoInconveniente, String nombre) {
        this.idTipoInconveniente = idTipoInconveniente;
        this.nombre = nombre;
    }

    public int getIdTipoInconveniente() {
        return idTipoInconveniente;
    }

    public void setIdTipoInconveniente(int idTipoInconveniente) {
        this.idTipoInconveniente = idTipoInconveniente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
