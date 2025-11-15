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
 * WebSocket general de Comunicación Interna.
 * Permite comunicación en tiempo real entre residentes y guardias.
 */
@ServerEndpoint("/chatGeneral")
public class ChatGeneral {

    private static final Set<Session> sesiones = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sesiones.add(session);
        System.out.println("[ChatGeneral] ✅ Nueva conexión. Total: " + sesiones.size());
    }

    @OnClose
    public void onClose(Session session) {
        sesiones.remove(session);
        System.out.println("[ChatGeneral] ❌ Conexión cerrada. Restantes: " + sesiones.size());
    }

    @OnMessage
    public void onMessage(String mensaje, Session session) {
        System.out.println("[ChatGeneral] 📩 Recibido JSON: " + mensaje);

        try {
            JsonObject json = Json.createReader(new StringReader(mensaje)).readObject();

            // ✅ Conversión segura (maneja números y strings)
            int idConversacion = Integer.parseInt(json.get("idConversacion").toString().replace("\"", ""));
            int idEmisor = Integer.parseInt(json.get("idEmisor").toString().replace("\"", ""));
            String contenido = json.getString("contenido");

            Integer idMensajeRespuesta = null;
            if (json.containsKey("idMensajeRespuesta") && !json.isNull("idMensajeRespuesta")) {
                try {
                    idMensajeRespuesta = Integer.parseInt(json.get("idMensajeRespuesta").toString().replace("\"", ""));
                } catch (Exception ignored) {}
            }

            // 2️⃣ Guardar mensaje en base de datos
            ConexionDB db = new ConexionDB();
            Connection con = db.openConnection();
            MensajeDAO dao = new MensajeDAO(con);

            int idMensaje = dao.guardarMensaje(idConversacion, idEmisor, contenido, "texto", idMensajeRespuesta);
            boolean guardado = idMensaje > 0;

            // 2.1️⃣ Si se guardó, marcar como leído los del otro usuario
            if (guardado) {
                dao.marcarComoLeido(idConversacion, idEmisor);
            }

            String cita = (idMensajeRespuesta != null) ? dao.obtenerContenidoPorId(idMensajeRespuesta) : "";
            db.closeConnection();

            if (guardado) {
                System.out.println("[ChatGeneral] 💾 Mensaje guardado con ID: " + idMensaje);
            } else {
                System.err.println("[ChatGeneral] ⚠️ Error al guardar el mensaje en DB");
            }

            // 3️⃣ Crear JSON de respuesta
            javax.json.JsonObjectBuilder builder = Json.createObjectBuilder()
                    .add("idMensaje", idMensaje)
                    .add("idConversacion", idConversacion)
                    .add("idEmisor", idEmisor)
                    .add("contenido", contenido)
                    .add("leido", false);

            if (idMensajeRespuesta != null) {
                builder.add("idMensajeRespuesta", idMensajeRespuesta);
                builder.add("idMensajeRespuestaContenido", cita != null ? cita : "");
            } else {
                builder.add("idMensajeRespuesta", JsonValue.NULL);
                builder.add("idMensajeRespuestaContenido", "");
            }

            JsonObject data = builder.build();

            // 4️⃣ Enviar mensaje a todos los clientes conectados
            for (Session s : sesiones) {
                if (s.isOpen()) {
                    s.getBasicRemote().sendText(data.toString());
                }
            }

        } catch (Exception e) {
            System.err.println("[ChatGeneral] ❌ Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("[ChatGeneral] 💥 Error general: " + error.getMessage());
        error.printStackTrace();
    }
}
