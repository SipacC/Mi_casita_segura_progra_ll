package Controlador;

import ConexionDB.ConexionDB;
import Gestion_bitacora.RegistroBitacora;
import Modelo.Conversacion;
import Modelo.Mensaje;
import Modelo.Usuario;
import ModeloDAO.ConversacionDAO;
import ModeloDAO.MensajeDAO;
import ModeloDAO.UsuarioDAO;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;

public class ControladorComunicacionSeguridad extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usr = (Usuario) (session != null ? session.getAttribute("usuario") : null);

        if (usr == null || !"seguridad".equalsIgnoreCase(usr.getRol())) {
            response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) accion = "menuchats";

        ConexionDB db = new ConexionDB();
        Connection con = db.openConnection();

        try {
            ConversacionDAO convDAO = new ConversacionDAO(con);
            MensajeDAO mensajeDAO = new MensajeDAO(con);
            UsuarioDAO usuarioDAO = new UsuarioDAO(con);

            switch (accion.toLowerCase()) {
                case "menuchats":
                    try {
                        List<Conversacion> conversaciones = convDAO.listarPorGuardia(usr.getIdUsuario());
                        request.setAttribute("conversaciones", conversaciones);
                        request.getRequestDispatcher("vistasComunicacionSeguridad/chatSeguridad.jsp").forward(request, response);
                    } catch (Exception e) {
                        System.err.println("[ComunicacionSeguridad] Error en menuchats: " + e.getMessage());
                        request.setAttribute("mensajeError", "No se pudieron cargar las conversaciones.");
                        request.getRequestDispatcher("vistasComunicacionSeguridad/chatSeguridad.jsp").forward(request, response);
                    }
                    break;
                case "crearconversacion":
                    try {
                        int idGuardia = usr.getIdUsuario();
                        int idResidente = Integer.parseInt(request.getParameter("idResidente"));

                        boolean existe = convDAO.validarConversacionExistente(idResidente, idGuardia);

                        if (existe) {
                            int idExistente = convDAO.obtenerIdConversacionActiva(idResidente, idGuardia);
                            if (idExistente > 0) {
                                response.sendRedirect("ControladorComunicacionSeguridad?accion=verchat&idConversacion=" + idExistente);
                                return;
                            } else {
                                request.setAttribute("mensajeError", "Ya existe una conversación activa, pero no se pudo abrir.");
                            }
                        } else {
                            boolean creada = convDAO.crearConversacion(idResidente, idGuardia, null, "residente-guardia");
                            if (creada) {
                                request.setAttribute("mensajeExito", "Se ha creado la nueva conversación correctamente.");
                            } else {
                                request.setAttribute("mensajeError", "Ocurrió un error al crear la conversación.");
                            }
                        }

                        List<Conversacion> conversaciones = convDAO.listarPorGuardia(idGuardia);
                        request.setAttribute("conversaciones", conversaciones);
                        request.getRequestDispatcher("vistasComunicacionSeguridad/chatSeguridad.jsp").forward(request, response);

                    } catch (Exception e) {
                        System.err.println("[crearConversacion Seguridad] Error: " + e.getMessage());
                        request.setAttribute("mensajeError", "No se pudo crear la conversación. Intente nuevamente.");
                        request.getRequestDispatcher("vistasComunicacionSeguridad/chatSeguridad.jsp").forward(request, response);
                    }
                    break;
                case "enviarmensaje":
                    try {
                        int idConversacion = Integer.parseInt(request.getParameter("idConversacion"));
                        String contenido = request.getParameter("contenido");
                        int idEmisor = usr.getIdUsuario();
                        String idMensajeRespStr = request.getParameter("idMensajeRespuesta");
                        Integer idMensajeRespuesta = null;
                        if (idMensajeRespStr != null && !idMensajeRespStr.isEmpty()) {
                            idMensajeRespuesta = Integer.parseInt(idMensajeRespStr);
                        }

                        if (contenido == null || contenido.trim().isEmpty()) {
                            request.setAttribute("mensajeError", "El mensaje no puede estar vacío.");
                        } else {
                            int idMensaje = mensajeDAO.guardarMensaje(idConversacion, idEmisor, contenido, "texto", idMensajeRespuesta);
                            boolean guardado = idMensaje > 0;

                            if (guardado) {
                                request.setAttribute("mensajeExito", "Mensaje enviado correctamente.");
                            } else {
                                request.setAttribute("mensajeError", "Error al guardar el mensaje.");
                            }
                        }
                        List<Mensaje> mensajes = mensajeDAO.listarPorConversacion(idConversacion);
                        List<Conversacion> conversaciones = convDAO.listarPorGuardia(usr.getIdUsuario());

                        request.setAttribute("mensajes", mensajes);
                        request.setAttribute("conversaciones", conversaciones);
                        request.setAttribute("idConversacion", idConversacion);

                        request.getRequestDispatcher("vistasComunicacionSeguridad/chatSeguridad.jsp").forward(request, response);

                    } catch (Exception e) {
                        System.err.println("[enviarMensaje Seguridad] Error: " + e.getMessage());
                        request.setAttribute("mensajeError", "No se pudo enviar el mensaje.");
                        request.getRequestDispatcher("vistasComunicacionSeguridad/chatSeguridad.jsp").forward(request, response);
                    }
                    break;
                case "verchat":
                    try {
                        int idConversacion = Integer.parseInt(request.getParameter("idConversacion"));
                        int idGuardia = usr.getIdUsuario();

                        mensajeDAO.marcarLeidosPorConversacion(idConversacion, idGuardia);

                        List<Mensaje> mensajes = mensajeDAO.listarPorConversacion(idConversacion);
                        List<Conversacion> conversaciones = convDAO.listarPorGuardia(idGuardia);

                        request.setAttribute("mensajes", mensajes);
                        request.setAttribute("conversaciones", conversaciones);
                        request.setAttribute("idConversacion", idConversacion);

                        request.getRequestDispatcher("vistasComunicacionSeguridad/chatSeguridad.jsp").forward(request, response);

                    } catch (Exception e) {
                        System.err.println("[verChat Seguridad] Error: " + e.getMessage());
                        request.setAttribute("mensajeError", "Error al cargar los mensajes de la conversación.");
                        request.getRequestDispatcher("vistasComunicacionSeguridad/chatSeguridad.jsp").forward(request, response);
                    }
                    break;

                default:
                    response.sendRedirect("ControladorComunicacionSeguridad?accion=menuchats");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error en módulo Comunicación Interna (Seguridad).");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } finally {
            db.closeConnection();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
