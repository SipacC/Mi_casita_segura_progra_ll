package ModeloDAO;

import Modelo.Mensaje;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensajeDAO {

    private final Connection con;

    public MensajeDAO(Connection con) {
        this.con = con;
    }
    
    /**
     * Guarda un mensaje en la base de datos.
     * Devuelve el ID del mensaje insertado, o -1 si ocurre un error.
     */
    public int guardarMensaje(int idConversacion, int idEmisor, String contenido, String tipo, Integer idMensajeRespuesta) {
        String sql = "INSERT INTO mensaje (id_conversacion, id_emisor, contenido, tipo, id_mensaje_respuesta, fecha_envio, leido) " +
                    "VALUES (?, ?, ?, ?, ?, NOW(), FALSE)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idConversacion);
            ps.setInt(2, idEmisor);
            ps.setString(3, contenido);
            ps.setString(4, tipo);

            if (idMensajeRespuesta != null) {
                ps.setInt(5, idMensajeRespuesta);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        System.out.println("[MensajeDAO] ✅ Mensaje guardado con ID: " + idGenerado);
                        return idGenerado;
                    }
                }
            } else {
                System.err.println("[MensajeDAO] ⚠️ No se insertó ningún registro.");
            }

        } catch (SQLException e) {
            System.err.println("[MensajeDAO] ❌ Error al guardar mensaje: " + e.getMessage());
            e.printStackTrace();
        }

        return -1; // Si algo falla
    }


    /**
     * Lista todos los mensajes de una conversación ordenados por fecha.
     */
    public List<Mensaje> listarPorConversacion(int idConversacion) {
        List<Mensaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM mensaje WHERE id_conversacion = ? ORDER BY fecha_envio ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConversacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mensaje m = new Mensaje();
                    m.setIdMensaje(rs.getInt("id_mensaje"));
                    m.setIdConversacion(rs.getInt("id_conversacion"));
                    m.setIdEmisor(rs.getInt("id_emisor"));
                    m.setContenido(rs.getString("contenido"));
                    m.setFechaEnvio(rs.getTimestamp("fecha_envio"));
                    m.setLeido(rs.getBoolean("leido"));
                    m.setTipo(rs.getString("tipo"));

                    int idResp = rs.getInt("id_mensaje_respuesta");
                    if (!rs.wasNull()) {
                        m.setIdMensajeRespuesta(idResp);
                    } else {
                        m.setIdMensajeRespuesta(null);
                    }

                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("[MensajeDAO] Error listarPorConversacion: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene el contenido de un mensaje por su ID (para mostrar en citas/respuestas).
     */
    public String obtenerContenidoPorId(int idMensaje) {
        String sql = "SELECT contenido FROM mensaje WHERE id_mensaje = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMensaje);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("contenido");
                }
            }
        } catch (SQLException e) {
            System.err.println("[MensajeDAO] Error obtenerContenidoPorId: " + e.getMessage());
        }
        return null;
    }

    public void marcarComoLeido(int idConversacion, int idUsuario) {
        String sql = "UPDATE mensaje SET leido = TRUE " +
                    "WHERE id_conversacion = ? AND id_emisor <> ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConversacion);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[MensajeDAO] Error al marcar leídos: " + e.getMessage());
        }
    }

    public void marcarLeidosPorConversacion(int idConversacion, int idUsuario) {
        String sql = "UPDATE mensaje SET leido = TRUE WHERE id_conversacion = ? AND id_emisor <> ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConversacion);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[MensajeDAO] Error marcarLeidosPorConversacion: " + e.getMessage());
        }
    }


}
