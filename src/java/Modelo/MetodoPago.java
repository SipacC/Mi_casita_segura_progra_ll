package Modelo;

public class MetodoPago {
    private int idMetodo;
    private String nombre;

    // Constructor vacío
    public MetodoPago() {}

    // Constructor completo
    public MetodoPago(int idMetodo, String nombre) {
        this.idMetodo = idMetodo;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdMetodo() {
        return idMetodo;
    }

    public void setIdMetodo(int idMetodo) {
        this.idMetodo = idMetodo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
