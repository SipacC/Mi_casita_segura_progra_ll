package Modelo;

public class Visita {
    private int id_visita;
    private int id_usuario;       // residente que creó la visita
    private String dpi_visita;
    private String nombre;
    private String tipo_visita;   // "Visita" o "Por intentos"
    private String correo_visita;
    private String estado;        // activo o cancelado
    private String motivo;        // 🔹 nuevo campo

    public Visita() {}

    public Visita(int id_visita, int id_usuario, String dpi_visita, String nombre,
                  String tipo_visita, String correo_visita, String estado, String motivo) {
        this.id_visita = id_visita;
        this.id_usuario = id_usuario;
        this.dpi_visita = dpi_visita;
        this.nombre = nombre;
        this.tipo_visita = tipo_visita;
        this.correo_visita = correo_visita;
        this.estado = estado;
        this.motivo = motivo;
    }

    public int getId_visita() { return id_visita; }
    public void setId_visita(int id_visita) { this.id_visita = id_visita; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getDpi_visita() { return dpi_visita; }
    public void setDpi_visita(String dpi_visita) { this.dpi_visita = dpi_visita; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo_visita() { return tipo_visita; }
    public void setTipo_visita(String tipo_visita) { this.tipo_visita = tipo_visita; }

    public String getCorreo_visita() { return correo_visita; }
    public void setCorreo_visita(String correo_visita) { this.correo_visita = correo_visita; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
