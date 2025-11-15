package Gestion_qr_visita;

import ConexionDB.ConexionDB;
import java.sql.*;

public class RegistrarQrAccesoVisita {
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    /**
     * Inserta en qr_visita y acceso_visita al mismo tiempo
     * @param idVisita -> id de la visita creada
     * @param codigoQR -> el código aleatorio generado para el QR
     * @param validoHasta -> fecha límite de validez (puede ser null si es "Por intentos")
     * @param intentos -> número de intentos (puede ser null si es "Visita")
     * @param rutaQR -> ruta en disco/servidor donde se guardó la imagen del QR
     * @return id_qr_visita generado
     */
    public int registrarQrYAcceso(int idVisita, String codigoQR,
                                  Timestamp validoHasta, Integer intentos, String rutaQR) {
        int idQrVisita = -1;
        try (ConexionDB db = new ConexionDB();
             Connection con = db.openConnection()) {
            String sqlQr = "INSERT INTO qr_visita(id_visita, codigo_qr_visita, valido_hasta, intentos, ruta_qr) " +
                           "VALUES (?, ?, ?, ?, ?) RETURNING id_qr_visita";
            try (PreparedStatement ps = con.prepareStatement(sqlQr)) {
                ps.setInt(1, idVisita);
                ps.setString(2, codigoQR);

                if (validoHasta != null) {
                    ps.setTimestamp(3, validoHasta);
                } else {
                    ps.setNull(3, Types.TIMESTAMP);
                }

                if (intentos != null) {
                    ps.setInt(4, intentos);
                } else {
                    ps.setNull(4, Types.INTEGER);
                }

                ps.setString(5, rutaQR);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idQrVisita = rs.getInt("id_qr_visita");
                    }
                }
            }

            if (idQrVisita > 0) {
                String sqlAcceso = "INSERT INTO acceso_visita(id_qr_visita, tipo) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlAcceso)) {
                    ps.setInt(1, idQrVisita);
                    ps.setString(2, "registro");
                    ps.executeUpdate();
                }
            }

            System.out.println("Registro en qr_visita y acceso_visita completado (id_qr_visita=" + idQrVisita + ")");
        } catch (Exception e) {
            System.err.println("Error en registrarQrYAccesoVisita: " + e.getMessage());
            e.printStackTrace();
        }
        return idQrVisita;
    }
}
