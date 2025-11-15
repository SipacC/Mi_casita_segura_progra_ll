package ModeloDAO;

import Modelo.Visita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitaDAO {

     private final Connection con;

    public VisitaDAO(Connection con) {
        this.con = con;
    }

    public int registrarVisita(Visita v) {
        int idGenerado = -1;
        String sql = "INSERT INTO visita (id_usuario, dpi_visita, nombre, tipo_visita, correo_visita, estado, motivo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_visita";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, v.getId_usuario());
            ps.setString(2, v.getDpi_visita());
            ps.setString(3, v.getNombre());
            ps.setString(4, v.getTipo_visita());
            ps.setString(5, v.getCorreo_visita());
            ps.setString(6, v.getEstado());
            ps.setString(7, v.getMotivo());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idGenerado = rs.getInt("id_visita");
            }

        } catch (SQLException e) {
            System.err.println("Error en registrarVisita: " + e.getMessage());
        }
        return idGenerado;
    }

    public List<Visita> listarPorUsuario(int idUsuario) {
        List<Visita> lista = new ArrayList<>();
        String sql = "SELECT * FROM visita WHERE id_usuario=? ORDER BY id_visita DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Visita v = new Visita(
                    rs.getInt("id_visita"),
                    rs.getInt("id_usuario"),
                    rs.getString("dpi_visita"),
                    rs.getString("nombre"),
                    rs.getString("tipo_visita"),
                    rs.getString("correo_visita"),
                    rs.getString("estado"),
                    rs.getString("motivo")
                );
                lista.add(v);
            }

        } catch (SQLException e) {
            System.err.println("Error en listarPorUsuario: " + e.getMessage());
        }
        return lista;
    }

    public boolean cancelarVisita(int idVisita) {
        String sql = "UPDATE visita SET estado='cancelado' WHERE id_visita=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVisita);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en cancelarVisita: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarVisita(int idVisita) {
    String sql = "DELETE FROM visita WHERE id_visita=?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idVisita);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error al eliminar visita: " + e.getMessage());
    }
    return false;
}

}


