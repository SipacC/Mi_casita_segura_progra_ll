package websocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import ConexionDB.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import util.CorreoNotificacion;

import java.io.IOException;
import java.sql.*;

@ServerEndpoint("/qrSocket")
public class QrSocket {

    private static String ultimoQR = "";
    private static long tiempoUltimoQR = 0;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Conexión WebSocket abierta: " + session.getId());
        try {
            ArduinoSerial.conectar("COM3", 115200);
        } catch (Exception e) {
            System.err.println("Error al conectar con Arduino: " + e.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("QR recibido: " + message);
        String respuesta = validarQr(message);
        session.getBasicRemote().sendText(respuesta);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("🔌 Conexión WebSocket cerrada: " + session.getId());
        try {
            ArduinoSerial.cerrar();
        } catch (Exception e) {
            System.err.println("Error al cerrar conexión con Arduino: " + e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error en WebSocket: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private String validarQr(String message) {
        long ahora = System.currentTimeMillis();
        if (message.equals(ultimoQR) && (ahora - tiempoUltimoQR < 2000)) {
            System.out.println("Ignorado QR duplicado en servidor (anti-spam): " + message);
            return "{\"resultado\":\"duplicado\"}";
        }
        ultimoQR = message;
        tiempoUltimoQR = ahora;

        try (ConexionDB db = new ConexionDB();
             Connection con = db.openConnection()) {
            String[] partes = message.split(";");
            String tipo = "entrada";
            String idStr;
            String codigo;

            if (message.startsWith("tipo:")) {
                tipo = partes[0].split(":")[1];
                idStr = partes[1].split(":")[1];
                codigo = partes[2].split(":")[1];
            } else {
                idStr = partes[0].split(":")[1];
                codigo = partes[1].split(":")[1];
            }

            int id = Integer.parseInt(idStr);

            PreparedStatement ps = con.prepareStatement(
                "SELECT q.id_qr_usuario, (u.nombre || ' ' || u.apellido) AS nombre_completo " +
                "FROM qr_usuario q " +
                "INNER JOIN usuarios u ON q.id_usuario = u.id_usuario " +
                "WHERE q.id_usuario=? AND q.codigo_qr_usuario=? AND q.estado='activo'"
            );
            ps.setInt(1, id);
            ps.setString(2, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int idQrUsuario = rs.getInt("id_qr_usuario");
                String usuario = rs.getString("nombre_completo");

                if (registrarAcceso(con, idQrUsuario, tipo, "acceso_usuario")) {
                    if ("entrada".equalsIgnoreCase(tipo)) {
                        ArduinoSerial.enviar("1\n");
                    } else {
                        ArduinoSerial.enviar("2\n");
                    }
                    return "{\"resultado\":\"valido\",\"usuario\":\"" + usuario + "\"}";
                } else {
                    return "{\"resultado\":\"duplicado\",\"usuario\":\"" + usuario + "\"}";
                }
            }
            
            ps = con.prepareStatement(
                "SELECT q.id_qr_visita, v.nombre, v.estado, q.valido_hasta, q.intentos, v.correo_visita " +
                "FROM qr_visita q " +
                "INNER JOIN visita v ON q.id_visita = v.id_visita " +
                "WHERE q.id_visita=? AND q.codigo_qr_visita=?"
            );
            ps.setInt(1, id);
            ps.setString(2, codigo);
            rs = ps.executeQuery();

            if (rs.next()) {
                int idQrVisita = rs.getInt("id_qr_visita");
                String nombreVisitante = rs.getString("nombre");
                String estado = rs.getString("estado");
                String correoVisitante = rs.getString("correo_visita");
                Timestamp validoHasta = rs.getTimestamp("valido_hasta");
                Integer intentos = rs.getInt("intentos");
                if (rs.wasNull()) intentos = null;

                if (!"activo".equalsIgnoreCase(estado)) {
                    return "{\"resultado\":\"invalido\",\"usuario\":\"" + nombreVisitante + "\"}";
                }
                if (validoHasta != null && validoHasta.before(new Timestamp(System.currentTimeMillis()))) {
                    enviarAvisoQRVencido(id, nombreVisitante, validoHasta, correoVisitante);
                    return "{\"resultado\":\"invalido\",\"usuario\":\"" + nombreVisitante + "\"}";
                }
                if (intentos != null && intentos <= 0) {
                    enviarAvisoQRAgotado(nombreVisitante, correoVisitante);
                    return "{\"resultado\":\"invalido\",\"usuario\":\"" + nombreVisitante + "\"}";
                }

                if (registrarAcceso(con, idQrVisita, tipo, "acceso_visita")) {
                    if (intentos != null) {
                        PreparedStatement psUpdate = con.prepareStatement(
                            "UPDATE qr_visita SET intentos = intentos - 1 WHERE id_qr_visita=?"
                        );
                        psUpdate.setInt(1, idQrVisita);
                        psUpdate.executeUpdate();
                        intentos = intentos - 1;
                    }

                    if ("entrada".equalsIgnoreCase(tipo)) {
                        ArduinoSerial.enviar("1\n");
                    } else {
                        ArduinoSerial.enviar("2\n");
                    }

                    notificarUsoVisita(id, nombreVisitante, tipo, validoHasta, intentos);
                    if (intentos != null && intentos <= 0) {
                        enviarAvisoQRAgotado(nombreVisitante, correoVisitante);
                    }

                    return "{\"resultado\":\"valido\",\"usuario\":\"" + nombreVisitante + "\"}";
                } else {
                    return "{\"resultado\":\"duplicado\",\"usuario\":\"" + nombreVisitante + "\"}";
                }
            }
            ArduinoSerial.enviar("0\n");
            return "{\"resultado\":\"invalido\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"resultado\":\"error\"}";
        }
    } 

    private void notificarUsoVisita(int idVisita, String nombreVisitante, String tipo, Timestamp validoHasta, Integer intentos) {
        new Thread(() -> {
            try (ConexionDB dbNotif = new ConexionDB();
                 Connection conNotif = dbNotif.openConnection()) {
                PreparedStatement psMail = conNotif.prepareStatement(
                    "SELECT u.correo, (u.nombre || ' ' || u.apellido) AS residente " +
                    "FROM visita v " +
                    "INNER JOIN usuarios u ON v.id_usuario = u.id_usuario " +
                    "WHERE v.id_visita=?"
                );
                psMail.setInt(1, idVisita);
                ResultSet rsMail = psMail.executeQuery();
                if (rsMail.next()) {
                    String correoResidente = rsMail.getString("correo");
                    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
                    String asunto = "Notificación de acceso";

                    String cuerpo;
                    if (intentos != null && intentos <= 0) {
                        cuerpo = "El código QR generado para la persona " + nombreVisitante +
                                " fue utilizado el día " + ahora.toLocalDate() +
                                " a las " + ahora.toLocalTime().withNano(0) +
                                " para " + tipo + " al condominio.\n\n" +
                                "Este QR ya no tiene más intentos disponibles y ha quedado INVALIDADO.";
                    } else {
                        cuerpo = "El código QR generado para la persona " + nombreVisitante +
                                " fue utilizado exitosamente el día " + ahora.toLocalDate() +
                                " a las " + ahora.toLocalTime().withNano(0) +
                                " para " + tipo + " al condominio.\n\n" +
                                "Este código tiene una validez de " +
                                (validoHasta != null ? validoHasta.toString() : intentos + " intentos restantes") +
                                ".";
                    }

                    cuerpo += "\n\nEn caso de cualquier irregularidad, por favor contacte al administrador del sistema.";

                    CorreoNotificacion.enviar(correoResidente, asunto, cuerpo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void enviarAvisoQRVencido(int idVisita, String nombreVisitante, Timestamp validoHasta, String correoVisitante) {
        new Thread(() -> {
            try (ConexionDB dbNotif = new ConexionDB();
                 Connection conNotif = dbNotif.openConnection()) {
                PreparedStatement psMail = conNotif.prepareStatement(
                    "SELECT u.correo FROM visita v INNER JOIN usuarios u ON v.id_usuario = u.id_usuario WHERE v.id_visita=?"
                );
                psMail.setInt(1, idVisita);
                ResultSet rsMail = psMail.executeQuery();
                if (rsMail.next()) {
                    String correoResidente = rsMail.getString("correo");
                    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
                    String asunto = "Intento de acceso con QR vencido";
                    String cuerpo = "El código QR generado para la persona " + nombreVisitante +
                            " intentó ser utilizado el " + ahora.toLocalDate() +
                            " a las " + ahora.toLocalTime().withNano(0) +
                            " pero ya había vencido (fecha de validez: " + validoHasta.toString() + ").\n\n" +
                            "El acceso fue denegado.";
                    CorreoNotificacion.enviar(correoResidente, asunto, cuerpo);
                }
                if (correoVisitante != null && !correoVisitante.isEmpty()) {
                    String asunto = "Su código QR ha caducado";
                    String cuerpo = "Hola " + nombreVisitante + ",\n\n" +
                            "Su código QR venció el " + validoHasta.toString() + " y ya no puede ser utilizado.\n" +
                            "Por favor contacte al residente para solicitar un nuevo código.";
                    CorreoNotificacion.enviar(correoVisitante, asunto, cuerpo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void enviarAvisoQRAgotado(String nombreVisitante, String correoVisitante) {
        if (correoVisitante != null && !correoVisitante.isEmpty()) {
            new Thread(() -> {
                java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
                String asunto = "Su código QR ha quedado inválido";
                String cuerpo = "Hola " + nombreVisitante + ",\n\n" +
                        "Su código QR fue utilizado el " + ahora.toLocalDate() + " a las " + ahora.toLocalTime().withNano(0) +
                        " y ya no tiene más intentos disponibles.\n" +
                        "Por favor contacte al residente para solicitar un nuevo código.";
                CorreoNotificacion.enviar(correoVisitante, asunto, cuerpo);
            }).start();
        }
    }
    private boolean registrarAcceso(Connection con, int idQr, String tipo, String tabla) throws SQLException {
        PreparedStatement psUltimo = con.prepareStatement(
            "SELECT tipo FROM " + tabla + " WHERE " +
            (tabla.equals("acceso_usuario") ? "id_qr_usuario" : "id_qr_visita") +
            "=? ORDER BY fecha_hora DESC LIMIT 1"
        );
        psUltimo.setInt(1, idQr);
        ResultSet rsUltimo = psUltimo.executeQuery();

        if (rsUltimo.next()) {
            String ultimoTipo = rsUltimo.getString("tipo");
            if (ultimoTipo != null && ultimoTipo.equalsIgnoreCase(tipo)) {
                return false;
            }
        }

        PreparedStatement ps2 = con.prepareStatement(
            "INSERT INTO " + tabla + " (" +
            (tabla.equals("acceso_usuario") ? "id_qr_usuario" : "id_qr_visita") +
            ", tipo, fecha_hora) VALUES (?, ?, NOW())"
        );
        ps2.setInt(1, idQr);
        ps2.setString(2, tipo);
        ps2.executeUpdate();

        return true;
    }
}
