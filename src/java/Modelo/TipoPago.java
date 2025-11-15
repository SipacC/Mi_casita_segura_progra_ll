package Modelo;

public class TipoPago {
    private int idTipo;
    private String nombre;
    private double monto;

    // Constructor vacío
    public TipoPago() {}

    // Constructor completo
    public TipoPago(int idTipo, String nombre, double monto) {
        this.idTipo = idTipo;
        this.nombre = nombre;
        this.monto = monto;
    }

    // Getters y Setters
    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
