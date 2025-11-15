<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Conversacion"%>
<%@page import="Modelo.Usuario"%>
<%@page import="ModeloDAO.UsuarioDAO"%>
<%@page import="ModeloDAO.MensajeDAO"%>
<%@page import="ConexionDB.ConexionDB"%>

<%
    Usuario usr = (Usuario) session.getAttribute("usuario");
    if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    List<Conversacion> conversaciones = (List<Conversacion>) request.getAttribute("conversaciones");
    List<Modelo.Mensaje> mensajes = (List<Modelo.Mensaje>) request.getAttribute("mensajes");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Comunicación Interna - Residente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/chatcomint.css" rel="stylesheet">

    <style>
        #mensajes {
    height: 520px !important;
    overflow-y: auto;
    border: 1px solid #ccc;
    padding: 10px;
    background: white;
    border-radius: 10px;
}

/* Burbuja de mensajes */
.burbuja {
    border-radius: 12px;
    display: inline-block;
    max-width: 75%;
    padding: 8px 12px;
}

/* =============================
   📱 OPTIMIZACIÓN PARA MÓVIL
   ============================= */
@media (max-width: 768px) {

    /* Oculta panel izquierdo (lista de chats) */
    .col-md-3 {
        display: none !important;
    }

    /* Chat ocupa todo el ancho */
    .col-md-9 {
        width: 100% !important;
        padding: 0 8px;
    }

    /* Cuadro de chat ocupa casi toda la pantalla */
    #mensajes {
        height: calc(100vh - 120px) !important; /* 🔹 más alto que antes */
        max-height: calc(100vh - 120px);
        border-radius: 10px;
        background: #fff;
        padding: 12px;
    }

    /* Barra de entrada fija abajo */
    .input-group {
        position: fixed;
        bottom: 0;
        left: 0;
        width: 100%;
        background: #ffffff;
        padding: 10px;
        border-top: 1px solid #ddd;
    }

    /* Ajustes de input y botón */
    #mensajeInput {
        font-size: 1em;
    }

    .btn-primary {
        font-size: 0.95em;
        padding: 8px 14px;
    }

    /* Botón volver visible arriba en móvil */
    .btn-volver {
        display: inline-block !important;
        margin-bottom: 6px;
    }

    /* 🔘 Modal adaptado para móvil */
    dialog {
        width: 92%;
        max-width: 420px;
        border: none;
        border-radius: 10px;
        padding: 15px;
        box-shadow: 0 4px 25px rgba(0,0,0,0.3);
        background: #fff;
    }

    dialog::backdrop {
        background: rgba(0,0,0,0.6);
    }
}

/* 📲 Pantallas muy pequeñas (menos de 480px) */
@media (max-width: 480px) {
    #mensajes {
        height: calc(100vh - 110px) !important; /* 🔹 aún más alto en celulares pequeños */
        padding: 10px;
    }

    .burbuja {
        font-size: 0.95em;
        padding: 8px 10px;
    }

    .btn-primary {
        font-size: 0.9em;
        padding: 6px 12px;
    }
}
    </style>
    
    <script>
        let socket;
        let idConversacion = <%= request.getAttribute("idConversacion") != null ? request.getAttribute("idConversacion") : "null" %>;
        let idEmisor = <%= usr.getIdUsuario() %>;
        let idMensajeRespuesta = null;
        let textoCita = "";

        function conectarWebSocket() {
            const protocolo = window.location.protocol === "https:" ? "wss:" : "ws:";
            const host = window.location.host;
            socket = new WebSocket(protocolo + "//" + host + "<%= request.getContextPath() %>/chatGeneral");

            socket.onopen = () => console.log("✅ WS conectado");
            socket.onclose = () => setTimeout(conectarWebSocket, 3000);

            socket.onmessage = (event) => {
                const data = JSON.parse(event.data);
                if (!data.idConversacion) return;

                if (data.idConversacion !== idConversacion) {
                    const boton = document.getElementById("chat-" + data.idConversacion);
                    if (boton) boton.classList.add("nuevo-mensaje");
                    return;
                }

                if (document.querySelector(`[data-id='${data.idMensaje}']`)) return;

                agregarMensaje(data, data.idEmisor === idEmisor ? "enviado" : "recibido");
            };
        }

        function enviarMensaje() {
            const input = document.getElementById("mensajeInput");
            const contenido = input.value.trim();
            if (!contenido || !idConversacion) return;

            const data = {
                idConversacion: parseInt(idConversacion),
                idEmisor: parseInt(idEmisor),
                contenido: contenido,
                idMensajeRespuesta: idMensajeRespuesta ? parseInt(idMensajeRespuesta) : null,
                idMensajeRespuestaContenido: textoCita || ""
            };

            console.log("📤 Enviando:", data);
            socket.send(JSON.stringify(data));
            input.value = "";
            cancelarRespuesta();
        }


        function agregarMensaje(data, tipo) {
            const area = document.getElementById("mensajes");
            const div = document.createElement("div");
            div.classList.add("mensaje", tipo);
            div.dataset.id = data.idMensaje;

            const burbuja = document.createElement("div");
            burbuja.classList.add("burbuja");
            burbuja.onclick = () => seleccionarParaResponder(data.idMensaje, data.contenido);

            if (data.idMensajeRespuestaContenido && data.idMensajeRespuestaContenido.trim() !== "") {
                const citaDiv = document.createElement("div");
                citaDiv.innerHTML = "<div class='cita-label'>En respuesta a:</div><div class='cita-texto'>"
                    + data.idMensajeRespuestaContenido.substring(0, 60) + "</div>";
                burbuja.appendChild(citaDiv);
            }

            const texto = document.createElement("span");
            texto.textContent = data.contenido;
            burbuja.appendChild(texto);

            const hora = document.createElement("span");
            hora.classList.add("hora");
            hora.textContent = new Date().toLocaleTimeString("es-GT", { hour: "2-digit", minute: "2-digit" });
            burbuja.appendChild(hora);

            if (tipo === "enviado") {
                const check = document.createElement("span");
                check.textContent = data.leido ? " ✓✓" : " ✓";
                check.style.color = data.leido ? "gray" : "lightgray";
                burbuja.appendChild(check);
            }

            div.appendChild(burbuja);
            area.appendChild(div);
            area.scrollTop = area.scrollHeight;
        }

        function seleccionarConversacion(id) {
            if (!id) return;
            const boton = document.getElementById("chat-" + id);
            if (boton) boton.classList.remove("nuevo-mensaje");
            window.location.href = "ControladorComunicacionResidente?accion=verchat&idConversacion=" + id;
        }

        function seleccionarParaResponder(idMensaje, contenido) {
            idMensajeRespuesta = idMensaje;
            textoCita = contenido.length > 60 ? contenido.substring(0, 60) + "..." : contenido;
            document.getElementById("texto-respuesta").textContent = textoCita;
            document.getElementById("respuesta-barra").style.display = "flex";
        }

        function cancelarRespuesta() {
            idMensajeRespuesta = null;
            textoCita = "";
            document.getElementById("respuesta-barra").style.display = "none";
            document.getElementById("texto-respuesta").textContent = "";
        }

        window.onload = () => {
            conectarWebSocket();
            const area = document.getElementById("mensajes");
            if (area) setTimeout(() => area.scrollTop = area.scrollHeight, 200);
        };
    </script>
</head>

<body class="bg-light">
<div class="container mt-3">
    <div class="row">
        <!-- Panel izquierdo -->
        <div class="col-md-3 border-end">
            <div class="d-flex justify-content-between align-items-center">
                <h5>Chats</h5>
                <button class="btn btn-outline-primary btn-sm" onclick="window.location.href='ControladorComunicacionResidente?accion=menu'">🏠 Menú</button>
            </div>
            <button class="btn btn-sm btn-success mb-2 w-100 mt-2" onclick="document.getElementById('modalNuevaConversacion').showModal()">+ Nueva conversación</button>

            <ul class="list-unstyled">
                <% if (conversaciones != null && !conversaciones.isEmpty()) {
                    for (Conversacion c : conversaciones) { %>
                        <li>
                            <button id="chat-<%= c.getIdConversacion() %>" class="btn btn-outline-secondary w-100 text-start mb-1"
                                    onclick="seleccionarConversacion(<%= c.getIdConversacion() %>)">
                                👮‍♂️ <%= c.getNombreGuardia() != null ? c.getNombreGuardia() : "Guardia" %>
                            </button>
                        </li>
                <% } } else { %>
                    <li><small>No tienes conversaciones activas</small></li>
                <% } %>
            </ul>
        </div>

        <!-- Panel derecho -->
        <div class="col-md-9">
            <% if (request.getAttribute("idConversacion") != null) { %>
                <!-- 🔙 Botón volver -->
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <button class="btn btn-outline-secondary btn-sm btn-volver" onclick="window.location.href='ControladorComunicacionResidente?accion=menuchats'">← Volver</button>
                    <h6 class="m-0">Conversación actual</h6>
                </div>

                <div id="mensajes" class="mb-2" style="overflow-y:auto; border:1px solid #ccc; padding:10px; background:white;">
                    <% if (mensajes != null && !mensajes.isEmpty()) {
                        for (Modelo.Mensaje m : mensajes) {
                            String clase = (m.getIdEmisor() == usr.getIdUsuario()) ? "enviado" : "recibido";
                            ConexionDB dbTemp = new ConexionDB();
                            MensajeDAO daoTemp = new MensajeDAO(dbTemp.openConnection());
                            String cita = (m.getIdMensajeRespuesta() != null) ? daoTemp.obtenerContenidoPorId(m.getIdMensajeRespuesta()) : null;
                            dbTemp.closeConnection();
                    %>
                        <div class="mensaje <%= clase %>">
                            <div class="burbuja" onclick="seleccionarParaResponder(<%= m.getIdMensaje() %>, '<%= m.getContenido().replace("'", "\\\\'").replace("\"", "&quot;") %>')">
                                <% if (cita != null) { %>
                                    <div class="cita-label">En respuesta a:</div>
                                    <div class="cita-texto"><%= cita.length()>60 ? cita.substring(0,60)+"..." : cita %></div>
                                <% } %>
                                <span style="font-size:1em;"><%= m.getContenido() %></span>
                                <span class="hora"><%= new java.text.SimpleDateFormat("HH:mm").format(m.getFechaEnvio()) %></span>
                                <% if (m.getIdEmisor() == usr.getIdUsuario()) { %>
                                    <span style="color:<%= m.isLeido() ? "gray" : "lightgray" %>;"> <%= m.isLeido() ? "✓✓" : "✓" %></span>
                                <% } %>
                            </div>
                        </div>
                    <% } } %>
                </div>

                <div class="mt-2">
                    <div id="respuesta-barra" style="display:none;align-items:center;background:#e8f5e9;padding:5px;border-radius:6px;">
                        <strong>En respuesta a:</strong> <span id="texto-respuesta"></span>
                        <button onclick="cancelarRespuesta()" style="border:none;background:none;color:#888;">✖</button>
                    </div>
                    <div class="input-group mt-1">
                        <input type="text" id="mensajeInput" class="form-control" placeholder="Escribe tu mensaje...">
                        <button class="btn btn-primary" onclick="enviarMensaje()">Enviar</button>
                    </div>
                </div>
            <% } else { %>
                <div class="text-center text-muted" style="padding:40px;">Selecciona o crea una conversación.</div>
            <% } %>
        </div>
    </div>
</div>

<!-- Modal -->
<dialog id="modalNuevaConversacion">
    <form method="get" action="ControladorComunicacionResidente">
        <input type="hidden" name="accion" value="crearConversacion">
        <h5>Iniciar nueva conversación</h5>
        <p>Selecciona un guardia:</p>
        <select name="idGuardia" class="form-select" required>
            <option value="">-- Selecciona --</option>
            <%
                ConexionDB db = new ConexionDB();
                UsuarioDAO usuarioDAO = new UsuarioDAO(db.openConnection());
                List<Usuario> guardias = usuarioDAO.listarGuardiasActivos();
                if (guardias != null && !guardias.isEmpty()) {
                    for (Usuario g : guardias) {
            %>
                <option value="<%= g.getIdUsuario() %>"><%= g.getNombre() %> <%= g.getApellido() %></option>
            <% } } else { %>
                <option disabled>No hay guardias activos</option>
            <% } db.closeConnection(); %>
        </select>
        <div class="mt-2 text-end">
            <button type="submit" class="btn btn-success btn-sm">Crear</button>
            <button type="button" class="btn btn-secondary btn-sm" onclick="document.getElementById('modalNuevaConversacion').close()">Cancelar</button>
        </div>
    </form>
</dialog>
</body>
</html>
