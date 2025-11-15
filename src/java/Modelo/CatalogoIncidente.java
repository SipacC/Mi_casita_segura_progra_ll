package Modelo;

public class CatalogoIncidente {
    private int idTipoIncidente;
    private String nombre;

    public CatalogoIncidente() {
    }

    public CatalogoIncidente(int idTipoIncidente, String nombre) {
        this.idTipoIncidente = idTipoIncidente;
        this.nombre = nombre;
    }

    public int getIdTipoIncidente() {
        return idTipoIncidente;
    }

    public void setIdTipoIncidente(int idTipoIncidente) {
        this.idTipoIncidente = idTipoIncidente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
