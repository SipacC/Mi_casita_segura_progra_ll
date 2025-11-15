package Modelo;

import java.sql.Timestamp;

public class QrVisita {
    private int id_qr_visita;
    private int id_visita;
    private String codigo_qr_visita;
    private Timestamp valido_hasta;  // aplica para "Visita"
    private Integer intentos;        // aplica para "Por intentos"
    private String ruta_qr;          // 🔹 nuevo campo

    public QrVisita() {}

    public QrVisita(int id_qr_visita, int id_visita, String codigo_qr_visita,
                    Timestamp valido_hasta, Integer intentos, String ruta_qr) {
        this.id_qr_visita = id_qr_visita;
        this.id_visita = id_visita;
        this.codigo_qr_visita = codigo_qr_visita;
        this.valido_hasta = valido_hasta;
        this.intentos = intentos;
        this.ruta_qr = ruta_qr;
    }

    public int getId_qr_visita() { return id_qr_visita; }
    public void setId_qr_visita(int id_qr_visita) { this.id_qr_visita = id_qr_visita; }

    public int getId_visita() { return id_visita; }
    public void setId_visita(int id_visita) { this.id_visita = id_visita; }

    public String getCodigo_qr_visita() { return codigo_qr_visita; }
    public void setCodigo_qr_visita(String codigo_qr_visita) { this.codigo_qr_visita = codigo_qr_visita; }

    public Timestamp getValido_hasta() { return valido_hasta; }
    public void setValido_hasta(Timestamp valido_hasta) { this.valido_hasta = valido_hasta; }

    public Integer getIntentos() { return intentos; }
    public void setIntentos(Integer intentos) { this.intentos = intentos; }

    public String getRuta_qr() { return ruta_qr; }
    public void setRuta_qr(String ruta_qr) { this.ruta_qr = ruta_qr; }
}
