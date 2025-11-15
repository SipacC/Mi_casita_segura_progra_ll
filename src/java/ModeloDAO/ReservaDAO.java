package ModeloDAO;
import Modelo.Reserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    private final Connection con;

    public ReservaDAO(Connection con) {
        this.con = con;
    }

    public List<Reserva> listarPorUsuario(int idUsuario) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.id_reserva, r.id_usuario, r.id_area, r.fecha_reserva, " +
                     "r.hora_inicio, r.hora_fin, r.estado, a.nombre AS nombre_area " +
                     "FROM reserva r " +
                     "JOIN areacomun a ON r.id_area = a.id_area " +
                     "WHERE r.id_usuario = ? " +
                     "ORDER BY r.fecha_reserva DESC, r.hora_inicio";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reserva r = new Reserva();
                    r.setId_reserva(rs.getInt("id_reserva"));
                    r.setId_usuario(rs.getInt("id_usuario"));
                    r.setId_area(rs.getInt("id_area"));
                    r.setNombreArea(rs.getString("nombre_area"));
                    r.setFecha_reserva(rs.getDate("fecha_reserva"));
                    r.setHora_inicio(rs.getTime("hora_inicio"));
                    r.setHora_fin(rs.getTime("hora_fin"));
                    r.setEstado(rs.getString("estado"));
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en listarPorUsuario: " + e.getMessage());
        }
        return lista;
    }

    public List<Reserva> buscarPorNombre(int idUsuario, String nombre) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.id_reserva, r.id_usuario, r.id_area, r.fecha_reserva, " +
                     "r.hora_inicio, r.hora_fin, r.estado, a.nombre AS nombre_area " +
                     "FROM reserva r " +
                     "JOIN areacomun a ON r.id_area = a.id_area " +
                     "WHERE r.id_usuario = ? AND a.nombre ILIKE ? " +
                     "ORDER BY r.fecha_reserva DESC, r.hora_inicio";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reserva r = new Reserva();
                    r.setId_reserva(rs.getInt("id_reserva"));
                    r.setId_usuario(rs.getInt("id_usuario"));
                    r.setId_area(rs.getInt("id_area"));
                    r.setNombreArea(rs.getString("nombre_area"));
                    r.setFecha_reserva(rs.getDate("fecha_reserva"));
                    r.setHora_inicio(rs.getTime("hora_inicio"));
                    r.setHora_fin(rs.getTime("hora_fin"));
                    r.setEstado(rs.getString("estado"));
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en buscarPorNombre: " + e.getMessage());
        }
        return lista;
    }

    public int insertar(Reserva r) {
        String sql = "INSERT INTO reserva (id_usuario, id_area, fecha_reserva, hora_inicio, hora_fin, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id_reserva";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getId_usuario());
            ps.setInt(2, r.getId_area());
            ps.setDate(3, r.getFecha_reserva());
            ps.setTime(4, r.getHora_inicio());
            ps.setTime(5, r.getHora_fin());
            ps.setString(6, r.getEstado());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idReserva = rs.getInt(1);
                    String sqlArea = "SELECT nombre FROM areacomun WHERE id_area = ?";
                    try (PreparedStatement ps2 = con.prepareStatement(sqlArea)) {
                        ps2.setInt(1, r.getId_area());
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            if (rs2.next()) {
                                r.setNombreArea(rs2.getString("nombre"));
                            }
                        }
                    }

                    return idReserva;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en insertar reserva: " + e.getMessage());
        }
        return -1;
    }


    public boolean validarDisponibilidad(Reserva r) {
        String sql = "SELECT COUNT(*) FROM reserva " +
                     "WHERE id_area = ? AND fecha_reserva = ? " +
                     "AND estado = 'Activa' " +
                     "AND ((hora_inicio < ? AND hora_fin > ?) OR " +
                     "     (hora_inicio < ? AND hora_fin > ?))";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, r.getId_area());
            ps.setDate(2, r.getFecha_reserva());
            ps.setTime(3, r.getHora_fin());
            ps.setTime(4, r.getHora_inicio());
            ps.setTime(5, r.getHora_inicio());
            ps.setTime(6, r.getHora_fin());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en validarDisponibilidad: " + e.getMessage());
        }
        return false;
    }

    public boolean cancelar(int idReserva) {
        String sql = "UPDATE reserva SET estado = 'Cancelada' WHERE id_reserva = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error en cancelar reserva: " + e.getMessage());
        }
        return false;
    }
}
