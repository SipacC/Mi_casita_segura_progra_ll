package ModeloDAO;

import Modelo.Conversacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversacionDAO {

    private final Connection con;

    public ConversacionDAO(Connection con) {
        this.con = con;
    }

    /**
     * Valida si ya existe una conversación activa entre dos usuarios (de cualquier rol).
     * @param idUsuario1 ID del primer usuario (residente, guardia o admin)
     * @param idUsuario2 ID del segundo usuario (residente, guardia o admin)
     * @return true si ya existe conversación activa, false si no
     */
    public boolean validarConversacionExistente(int idUsuario1, int idUsuario2) {
        String sql = "SELECT 1 FROM conversacion " +
                     "WHERE estado = 'activa' AND " +
                     "((id_residente = ? AND (id_guardia = ? OR id_administrador = ?)) OR " +
                     " (id_guardia = ? AND (id_residente = ? OR id_administrador = ?)) OR " +
                     " (id_administrador = ? AND (id_residente = ? OR id_guardia = ?)))";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Asigna los 9 parámetros en pares cruzados
            ps.setInt(1, idUsuario1); ps.setInt(2, idUsuario2); ps.setInt(3, idUsuario2);
            ps.setInt(4, idUsuario1); ps.setInt(5, idUsuario2); ps.setInt(6, idUsuario2);
            ps.setInt(7, idUsuario1); ps.setInt(8, idUsuario2); ps.setInt(9, idUsuario2);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[ConversacionDAO] Error validarConversacionExistente: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lista las conversaciones activas para cualquier usuario según su rol.
     * @param idUsuario ID del usuario actual (residente, guardia o admin)
     * @return lista de conversaciones donde participa el usuario
     */
    
    public List<Conversacion> listarPorUsuario(int idUsuario) {
        List<Conversacion> lista = new ArrayList<>();
        String sql = "SELECT c.*, u.nombre AS nombre_guardia, u.apellido AS apellido_guardia " +
                    "FROM conversacion c " +
                    "LEFT JOIN usuarios u ON c.id_guardia = u.id_usuario " +
                    "WHERE c.estado = 'activa' AND (c.id_residente = ? OR c.id_guardia = ? OR c.id_administrador = ?) " +
                    "ORDER BY c.fecha_creacion DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Conversacion c = new Conversacion();
                    c.setIdConversacion(rs.getInt("id_conversacion"));
                    c.setIdResidente(rs.getInt("id_residente"));
                    c.setIdGuardia(rs.getInt("id_guardia"));
                    c.setIdAdministrador(rs.getInt("id_administrador"));
                    c.setTipoConversacion(rs.getString("tipo_conversacion"));
                    c.setEstado(rs.getString("estado"));
                    c.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                    c.setUltimoMensaje(rs.getTimestamp("ultimo_mensaje"));

                    // ✅ Nuevo: asignar nombre del guardia
                    String nombre = rs.getString("nombre_guardia");
                    String apellido = rs.getString("apellido_guardia");
                    if (nombre != null) {
                        c.setNombreGuardia(nombre + " " + (apellido != null ? apellido : ""));
                    }

                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ConversacionDAO] Error listarPorUsuario: " + e.getMessage());
        }
        return lista;
    }


    /**
     * Crea una nueva conversación dinámica entre cualquier combinación de roles.
     * @param idResidente nullable
     * @param idGuardia nullable
     * @param idAdministrador nullable
     * @param tipoConversacion puede ser 'residente-guardia', 'residente-administrador', etc.
     */
    public boolean crearConversacion(Integer idResidente, Integer idGuardia, Integer idAdministrador, String tipoConversacion) {
        String sql = "INSERT INTO conversacion (id_residente, id_guardia, id_administrador, tipo_conversacion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (idResidente != null) ps.setInt(1, idResidente);
            else ps.setNull(1, java.sql.Types.INTEGER);

            if (idGuardia != null) ps.setInt(2, idGuardia);
            else ps.setNull(2, java.sql.Types.INTEGER);

            if (idAdministrador != null) ps.setInt(3, idAdministrador);
            else ps.setNull(3, java.sql.Types.INTEGER);

            ps.setString(4, tipoConversacion);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[ConversacionDAO] Error crearConversacion: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene el ID de la conversación activa entre dos usuarios (residente y guardia).
     * @param idUsuario1 ID del primer usuario
     * @param idUsuario2 ID del segundo usuario
     * @return ID de la conversación activa si existe, o -1 si no se encuentra
     */
    public int obtenerIdConversacionActiva(int idUsuario1, int idUsuario2) {
        String sql = "SELECT id_conversacion FROM conversacion " +
                    "WHERE estado = 'activa' AND " +
                    "((id_residente = ? AND id_guardia = ?) OR (id_guardia = ? AND id_residente = ?)) " +
                    "LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario1);
            ps.setInt(2, idUsuario2);
            ps.setInt(3, idUsuario1);
            ps.setInt(4, idUsuario2);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_conversacion");
                }
            }
        } catch (SQLException e) {
            System.err.println("[ConversacionDAO] Error obtenerIdConversacionActiva: " + e.getMessage());
        }
        return -1;
    }

    public List<Conversacion> listarPorGuardia(int idGuardia) {
    List<Conversacion> lista = new ArrayList<>();
    String sql = 
        "SELECT c.id_conversacion, c.id_guardia, c.id_residente, c.estado, " +
        "       u.nombre AS nombreResidente, u.apellido AS apellidoResidente " +
        "FROM conversacion c " +
        "JOIN usuarios u ON c.id_residente = u.id_usuario " +  // ✅ CAMBIO AQUÍ
        "WHERE c.id_guardia = ? AND LOWER(c.estado) = 'activa' " +
        "ORDER BY c.fecha_creacion DESC";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idGuardia);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Conversacion c = new Conversacion();
                c.setIdConversacion(rs.getInt("id_conversacion"));
                c.setIdGuardia(rs.getInt("id_guardia"));
                c.setIdResidente(rs.getInt("id_residente"));
                c.setNombreResidente(
                    rs.getString("nombreResidente") + " " + rs.getString("apellidoResidente")
                );
                c.setEstado(rs.getString("estado"));
                lista.add(c);
            }
        }
    } catch (SQLException e) {
        System.err.println("[ConversacionDAO] Error en listarPorGuardia: " + e.getMessage());
    }
    return lista;
}



}
