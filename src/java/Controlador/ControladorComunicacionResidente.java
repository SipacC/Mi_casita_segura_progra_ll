package Controlador;

import Modelo.CatalogoIncidente;
import Modelo.Conversacion;
import Modelo.Incidente;
import Modelo.Mensaje;
import Modelo.Usuario;
import ModeloDAO.CatalogoIncidenteDAO;
import ModeloDAO.ConversacionDAO;
import ModeloDAO.IncidenteDAO;
import ModeloDAO.MensajeDAO;
import ModeloDAO.UsuarioDAO;
import util.CorreoNotificacion;
import ConexionDB.ConexionDB;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;

public class ControladorComunicacionResidente extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usr = (Usuario) (session != null ? session.getAttribute("usuario") : null);

        if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
            response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) accion = "menu";

        ConexionDB db = new ConexionDB();
        Connection con = db.openConnection();

        try {
            ConversacionDAO convDAO = new ConversacionDAO(con);
            UsuarioDAO usuarioDAO = new UsuarioDAO(con);

            switch (accion.toLowerCase()) {

                case "menu":
                    request.getRequestDispatcher("/vistasComunicacionResidente/menuComunicacion.jsp").forward(request, response);
                    break;

                case "reportarincidente":
                    CatalogoIncidenteDAO catalogoDAO = new CatalogoIncidenteDAO(con);
                    List<CatalogoIncidente> tipos = catalogoDAO.listar();
                    request.setAttribute("tiposIncidentes", tipos);
                    request.getRequestDispatcher("/vistasComunicacionResidente/reportarIncidente.jsp").forward(request, response);
                    break;

                case "guardarincidente":
                    int idResidente = usr.getIdUsuario();
                    int idTipoIncidente = Integer.parseInt(request.getParameter("idTipoIncidente"));
                    String descripcion = request.getParameter("descripcion");
                    String fechaHoraStr = request.getParameter("fechaHora");

                    java.sql.Timestamp fechaHora = null;
                    if (fechaHoraStr != null && !fechaHoraStr.isEmpty()) {
                        fechaHora = java.sql.Timestamp.valueOf(fechaHoraStr.replace("T", " ") + ":00");
                    }

                    Incidente nuevo = new Incidente(idResidente, idTipoIncidente, descripcion, fechaHora);

                    IncidenteDAO incDAO = new IncidenteDAO(con);
                    boolean registrado = incDAO.registrar(nuevo);

                    if (registrado) {
                        CatalogoIncidenteDAO catDAO = new CatalogoIncidenteDAO(con);
                        String nombreTipo = catDAO.obtenerNombrePorId(idTipoIncidente);
                        List<Usuario> guardiasActivos = usuarioDAO.listarGuardiasActivos();

                        String asunto = "Reporte de incidente";
                        String cuerpoCorreo = "Se le informa que el residente " + usr.getNombre() + " " + usr.getApellido() +
                                " ha reportado un incidente.\n\n" +
                                "Tipo de incidente: " + nombreTipo + "\n" +
                                "Fecha y hora: " + fechaHora + "\n" +
                                "Descripción: " + descripcion + "\n\n" +
                                "Por favor, tomar las acciones correspondientes.";

                        new Thread(() -> {
                            try {
                                for (Usuario guardia : guardiasActivos) {
                                    if (guardia.getCorreo() != null && !guardia.getCorreo().isEmpty()) {
                                        CorreoNotificacion.enviar(guardia.getCorreo(), asunto, cuerpoCorreo);
                                    }
                                }
                            } catch (Exception ex) {
                                System.err.println("[Thread-Correo] Error al enviar notificaciones: " + ex.getMessage());
                            }
                        }).start();

                        request.setAttribute("mensajeExito", "Se ha creado el incidente y se notificó a los guardias.");
                        request.getRequestDispatcher("/vistasComunicacionResidente/menuComunicacion.jsp").forward(request, response);

                    } else {
                        request.setAttribute("mensajeError", "Error al registrar el incidente. Intente nuevamente.");
                        request.getRequestDispatcher("/vistasComunicacionResidente/reportarIncidente.jsp").forward(request, response);
                    }
                    break;

                case "menuchats":
                    try {
                        if (usr == null) {
                            response.sendRedirect("ControladorLogin?accion=login");
                            return;
                        }

                        List<Conversacion> conversaciones = convDAO.listarPorUsuario(usr.getIdUsuario());
                        request.setAttribute("conversaciones", conversaciones);
                        request.getRequestDispatcher("vistasComunicacionResidente/chatResidente.jsp").forward(request, response);
                    } catch (Exception e) {
                        System.err.println("[ControladorComunicacionResidente] Error en menuChats: " + e.getMessage());
                        request.setAttribute("mensajeError", "No se pudieron cargar las conversaciones.");
                        request.getRequestDispatcher("vistasComunicacionResidente/chatResidente.jsp").forward(request, response);
                    }
                    break;

                case "crearconversacion":
                    try {
                        idResidente = usr.getIdUsuario();
                        int idGuardia = Integer.parseInt(request.getParameter("idGuardia"));
                        boolean existe = convDAO.validarConversacionExistente(idResidente, idGuardia);

                        if (existe) {
                            int idExistente = convDAO.obtenerIdConversacionActiva(idResidente, idGuardia);
                            if (idExistente > 0) {
                                response.sendRedirect("ControladorComunicacionResidente?accion=verchat&idConversacion=" + idExistente);
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

                        List<Conversacion> conversaciones = convDAO.listarPorUsuario(idResidente);
                        request.setAttribute("conversaciones", conversaciones);
                        request.getRequestDispatcher("vistasComunicacionResidente/chatResidente.jsp").forward(request, response);

                    } catch (Exception e) {
                        System.err.println("[crearConversacion] Error: " + e.getMessage());
                        request.setAttribute("mensajeError", "No se pudo crear la conversación. Intente nuevamente.");
                        request.getRequestDispatcher("vistasComunicacionResidente/chatResidente.jsp").forward(request, response);
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
                            MensajeDAO mensajeDAO = new MensajeDAO(con);

                            int idMensaje = mensajeDAO.guardarMensaje(idConversacion, idEmisor, contenido, "texto", idMensajeRespuesta);
                            boolean guardado = idMensaje > 0;


                            if (guardado) {
                                request.setAttribute("mensajeExito", "Mensaje enviado correctamente.");
                            } else {
                                request.setAttribute("mensajeError", "Error al guardar el mensaje.");
                            }
                        }

                        MensajeDAO mensajeDAO = new MensajeDAO(con);
                        List<Mensaje> mensajes = mensajeDAO.listarPorConversacion(idConversacion);
                        List<Conversacion> conversaciones = convDAO.listarPorUsuario(usr.getIdUsuario());

                        request.setAttribute("mensajes", mensajes);
                        request.setAttribute("conversaciones", conversaciones);
                        request.setAttribute("idConversacion", idConversacion);

                        request.getRequestDispatcher("vistasComunicacionResidente/chatResidente.jsp").forward(request, response);

                    } catch (Exception e) {
                        System.err.println("[enviarMensaje] Error: " + e.getMessage());
                        request.setAttribute("mensajeError", "No se pudo enviar el mensaje.");
                        request.getRequestDispatcher("vistasComunicacionResidente/chatResidente.jsp").forward(request, response);
                    }
                    break;


                case "verchat":
                    try {
                        int idConversacion = Integer.parseInt(request.getParameter("idConversacion"));
                        int idUsuario = usr.getIdUsuario();
                        MensajeDAO mensajeDAO = new MensajeDAO(con);
                        ConversacionDAO convDAO2 = new ConversacionDAO(con);
                        mensajeDAO.marcarComoLeido(idConversacion, idUsuario);
                        List<Mensaje> mensajes = mensajeDAO.listarPorConversacion(idConversacion);
                        List<Conversacion> conversaciones = convDAO2.listarPorUsuario(idUsuario);
                        request.setAttribute("conversaciones", conversaciones);
                        request.setAttribute("mensajes", mensajes);
                        request.setAttribute("idConversacion", idConversacion);
                        request.getRequestDispatcher("vistasComunicacionResidente/chatResidente.jsp").forward(request, response);

                    } catch (Exception e) {
                        System.err.println("[verChat] Error al cargar mensajes: " + e.getMessage());
                        request.setAttribute("mensajeError", "No se pudieron cargar los mensajes de la conversación.");
                        request.getRequestDispatcher("vistasComunicacionResidente/chatResidente.jsp").forward(request, response);
                    }
                    break;




                default:
                    response.sendRedirect("ControladorComunicacionResidente?accion=menu");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error en módulo Comunicación Interna (Residente).");
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
