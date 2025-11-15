package ModeloDAO;

import Modelo.QrVisita;
import java.sql.*;

public class QrVisitaDAO {

    private final Connection con;

    public QrVisitaDAO(Connection con) {
        this.con = con;
    }

    public int registrarQr(QrVisita qr) {
        int idQrVisita = -1;
        String sql = "INSERT INTO qr_visita(id_visita, codigo_qr_visita, valido_hasta, intentos, ruta_qr) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id_qr_visita";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, qr.getId_visita());
            ps.setString(2, qr.getCodigo_qr_visita());

            if (qr.getValido_hasta() != null) {
                ps.setTimestamp(3, qr.getValido_hasta());
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }

            if (qr.getIntentos() != null) {
                ps.setInt(4, qr.getIntentos());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, qr.getRuta_qr());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idQrVisita = rs.getInt("id_qr_visita");
            }

        } catch (SQLException e) {
            System.err.println("Error en registrarQr: " + e.getMessage());
        }
        return idQrVisita;
    }

    public QrVisita obtenerPorVisita(int idVisita) {
        String sql = "SELECT * FROM qr_visita WHERE id_visita=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVisita);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                QrVisita qr = new QrVisita(
                    rs.getInt("id_qr_visita"),
                    rs.getInt("id_visita"),
                    rs.getString("codigo_qr_visita"),
                    rs.getTimestamp("valido_hasta"),
                    (Integer) rs.getObject("intentos"),
                    rs.getString("ruta_qr")
                );
                return qr;
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerPorVisita: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarIntentos(int idQrVisita, int nuevosIntentos) {
        String sql = "UPDATE qr_visita SET intentos=? WHERE id_qr_visita=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevosIntentos);
            ps.setInt(2, idQrVisita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizarIntentos: " + e.getMessage());
        }
        return false;
    }

    public boolean cancelarPorVisita(int idVisita) {
        String sql = "DELETE FROM qr_visita WHERE id_visita=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVisita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en cancelarPorVisita: " + e.getMessage());
        }
        return false;
    }

    public String obtenerRutaPorVisita(int idVisita) {
    String ruta = null;
    String sql = "SELECT ruta_qr FROM qr_visita WHERE id_visita=?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idVisita);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                ruta = rs.getString("ruta_qr");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error en obtenerRutaPorVisita: " + e.getMessage());
    }
    return ruta;
}

}
