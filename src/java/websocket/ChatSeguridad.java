package websocket;

import ModeloDAO.MensajeDAO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.StringReader;
import java.sql.Connection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import ConexionDB.ConexionDB;

/**
 * WebSocket del módulo de Comunicación Interna para Seguridad.
 * Versión estable y sincronizada con el chat de Residente.
 */
@ServerEndpoint("/chatSeguridad")
public class ChatSeguridad {

    private static final Set<Session> sesiones = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sesiones.add(session);
        System.out.println("[ChatSeguridad] ✅ Conexión abierta. Total sesiones: " + sesiones.size());
    }

    @OnClose
    public void onClose(Session session) {
        sesiones.remove(session);
        System.out.println("[ChatSeguridad] ❌ Conexión cerrada. Restantes: " + sesiones.size());
    }

    @OnMessage
    public void onMessage(String mensaje, Session session) {
        try {
            // 1️⃣ Parsear el JSON recibido
            JsonObject json = Json.createReader(new StringReader(mensaje)).readObject();
            int idConversacion = json.getInt("idConversacion");
            int idEmisor = json.getInt("idEmisor");
            String contenido = json.getString("contenido");

            Integer idMensajeRespuesta = null;
            if (json.containsKey("idMensajeRespuesta") && !json.isNull("idMensajeRespuesta")) {
                idMensajeRespuesta = json.getInt("idMensajeRespuesta");
            }

            // 2️⃣ Guardar mensaje en base de datos
            ConexionDB db = new ConexionDB();
            Connection con = db.openConnection();
            MensajeDAO dao = new MensajeDAO(con);

            int idMensaje = dao.guardarMensaje(idConversacion, idEmisor, contenido, "texto", idMensajeRespuesta);
            boolean guardado = idMensaje > 0;

            // 2.1️⃣ Si se guardó, marcar los mensajes del otro usuario como leídos
            if (guardado) {
                dao.marcarComoLeido(idConversacion, idEmisor);
            }

            String cita = (idMensajeRespuesta != null) ? dao.obtenerContenidoPorId(idMensajeRespuesta) : "";
            db.closeConnection();

            if (guardado) {
                System.out.println("[ChatSeguridad] 💾 Mensaje guardado correctamente con ID: " + idMensaje);
            } else {
                System.err.println("[ChatSeguridad] ⚠️ Error al guardar el mensaje.");
            }

            // 3️⃣ Armar JSON de respuesta (incluyendo 'leido')
            javax.json.JsonObjectBuilder builder = Json.createObjectBuilder()
                    .add("idMensaje", idMensaje)
                    .add("idConversacion", idConversacion)
                    .add("idEmisor", idEmisor)
                    .add("contenido", contenido)
                    .add("leido", false); // se actualizará visualmente a ✓✓ al leer

            if (idMensajeRespuesta != null) {
                builder.add("idMensajeRespuesta", idMensajeRespuesta);
                builder.add("idMensajeRespuestaContenido", cita != null ? cita : "");
            } else {
                builder.add("idMensajeRespuesta", JsonValue.NULL);
                builder.add("idMensajeRespuestaContenido", "");
            }

            JsonObject data = builder.build();

            // 4️⃣ Enviar mensaje a todos los clientes conectados (una sola vez)
            for (Session s : sesiones) {
                if (s.isOpen()) {
                    s.getBasicRemote().sendText(data.toString());
                }
            }

        } catch (Exception e) {
            System.err.println("[ChatSeguridad] ❌ Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("[ChatSeguridad] 💥 Error: " + error.getMessage());
        error.printStackTrace();
    }
}
