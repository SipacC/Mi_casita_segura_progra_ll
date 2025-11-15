package websocket;

import util.CorreoNotificacion;
import java.sql.*;
import java.time.LocalDateTime;

public class NotificacionService {

    public static void notificarAccesoVisita(Connection con,
                                             int idVisita,
                                             String nombreVisitante,
                                             String tipo,
                                             Timestamp validoHasta,
                                             Integer intentos) {
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT u.correo, (u.nombre || ' ' || u.apellido) AS residente " +
                "FROM visita v " +
                "INNER JOIN usuarios u ON v.id_usuario = u.id_usuario " +
                "WHERE v.id_visita=?"
            );
            ps.setInt(1, idVisita);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String correoResidente = rs.getString("correo");
                //String nombreResidente = rs.getString("residente");

                LocalDateTime ahora = LocalDateTime.now();
                String asunto = "Notificación de acceso";
                String cuerpo = "El código QR generado para la persona " + nombreVisitante +
                        " fue utilizado exitosamente el día " + ahora.toLocalDate() +
                        " a las " + ahora.toLocalTime().withNano(0) +
                        " para " + tipo + " al condominio.\n\n" +
                        "Este código tiene una validez de " +
                        (validoHasta != null ? validoHasta.toString() : intentos + " intentos") +
                        ".\n\nEn caso de cualquier irregularidad, por favor contacte al administrador del sistema.";

                CorreoNotificacion.enviar(correoResidente, asunto, cuerpo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
