package Controlador;

import ConexionDB.ConexionDB;
import java.sql.Connection;

import ModeloDAO.PaqueteriaDAO;
import ModeloDAO.UsuarioDAO;
import Modelo.Paqueteria;
import Modelo.Usuario;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class ControladorSeguridad extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        String rol = (session != null && session.getAttribute("rol") != null)
                ? session.getAttribute("rol").toString()
                : "";

        if (!"seguridad".equalsIgnoreCase(rol)) {
            response.sendRedirect("vistasLogin/accesoDenegado.jsp");
            return;
        }

        ConexionDB conexionDB = new ConexionDB();
        Connection con = conexionDB.openConnection();

        try {
            PaqueteriaDAO paqueteriaDAO = new PaqueteriaDAO(con);
            UsuarioDAO usuarioDAO = new UsuarioDAO(con);

            String accion = request.getParameter("accion");
            String acceso = null;

            switch (accion != null ? accion : "menu") {

                case "menu":
                    acceso = "vistasSeguridad/menuSeguridad.jsp";
                    break;

                case "paqueteria":
                    request.setAttribute("listaResidentes", usuarioDAO.listar());
                    request.setAttribute("listaPaqueteria", paqueteriaDAO.listar());
                    acceso = "vistasSeguridad/paqueteria.jsp";
                    break;

                case "nuevoPaquete":
                    List<Usuario> listaResidentes = usuarioDAO.listarResidentesActivos();
                    request.setAttribute("listaResidentes", listaResidentes);
                    acceso = "vistasSeguridad/registrarPaquete.jsp";
                    break;

                case "guardarPaquete":
                    Paqueteria nuevo = new Paqueteria();
                    nuevo.setNumero_guia(request.getParameter("numero_guia"));
                    nuevo.setId_residente(Integer.parseInt(request.getParameter("id_residente")));
                    nuevo.setCasa_residente(request.getParameter("casa_residente"));
                    nuevo.setObservaciones(request.getParameter("observaciones"));

                    Usuario usr = (Usuario) session.getAttribute("usuario");
                    nuevo.setId_agente_registra(usr.getIdUsuario());

                    boolean ok = paqueteriaDAO.insertar(nuevo);

                    if (ok) {
                        response.sendRedirect("ControladorSeguridad?accion=paqueteria");
                        Gestion_bitacora.RegistroBitacora.log(request,"Registró nuevo paquete con número de guía: " + nuevo.getNumero_guia(),"Paquetería", con);
                    } else {
                        response.getWriter().println("Error al registrar el paquete.");
                    }
                    return;

                case "entregarPaquete":
                    int idPaquete = Integer.parseInt(request.getParameter("id"));
                    Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

                    if (usuarioSesion != null && "seguridad".equalsIgnoreCase(usuarioSesion.getRol())) {
                        int idAgente = usuarioSesion.getIdUsuario();
                        boolean entregado = paqueteriaDAO.actualizarEntrega(idPaquete, idAgente);

                        if (entregado) {
                            Paqueteria paquete = paqueteriaDAO.obtenerPorId(idPaquete);
                            Usuario residente = usuarioDAO.list(paquete.getId_residente());
                            String destinatario = residente.getCorreo();
                            String asunto = "Entrega de Paquetería";
                            String fechaHora = java.time.LocalDateTime.now()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                            String cuerpo = "Se le informa que se ha entregado el paquete con identificación "
                                    + paquete.getNumero_guia()
                                    + ", en la fecha " + fechaHora + ".";
                            boolean correoEnviado = util.CorreoNotificacion.enviar(destinatario, asunto, cuerpo);
                            Gestion_bitacora.RegistroBitacora.log(
                                request,"Entregó el paquete con ID " + idPaquete + " al residente " + residente.getNombre() + " " + residente.getApellido() + ".", "Paquetería", con);
                            if (correoEnviado) {
                                request.setAttribute("mensaje", "Paquete entregado y notificación enviada correctamente.");
                            } else {
                                request.setAttribute("mensaje", "Paquete entregado, pero no se pudo enviar el correo de notificación.");
                            }
                            response.sendRedirect("ControladorSeguridad?accion=paqueteria");
                        } else {
                            response.getWriter().println("Error al actualizar el estado del paquete.");
                        }
                    } else {
                        response.sendRedirect("ControladorLogin?accion=login");
                    }
                    return;

                case "buscarPaquete":
                    String texto = request.getParameter("texto");

                    List<Paqueteria> resultados;
                    if (texto == null || texto.trim().isEmpty()) {
                        resultados = paqueteriaDAO.listar();
                    } else {
                        resultados = paqueteriaDAO.buscar(texto.trim());
                    }

                    request.setAttribute("listaPaqueteria", resultados);

                    RequestDispatcher vistaBuscar = request.getRequestDispatcher("vistasSeguridad/paqueteria.jsp");
                    vistaBuscar.forward(request, response);
                    break;

                case "directorio": {
                    String textoDir = request.getParameter("texto");
                    List<Usuario> resultadosDir = new java.util.ArrayList<>();

                    if (textoDir != null && !textoDir.trim().isEmpty()) {
                        textoDir = textoDir.trim();
                        resultadosDir = usuarioDAO.buscarDirectorio(textoDir, textoDir, textoDir, textoDir);
                        request.setAttribute("listaUsuarios", resultadosDir);

                        if (resultadosDir.isEmpty()) {
                            request.setAttribute("mensaje", "No se encontró ningún usuario con los datos ingresados.");
                        }
                    } else {
                        request.setAttribute("mensaje", "Busca por nombre, apellido, lote o número de casa.");
                    }

                    Gestion_bitacora.RegistroBitacora.log(request, "Consultó el directorio general (Seguridad)", "Directorio", con);

                    RequestDispatcher vistaAjaxDir = request.getRequestDispatcher("vistasSeguridad/directorio.jsp");
                    vistaAjaxDir.forward(request, response);
                    return;
                }


                default:
                    acceso = "vistasSeguridad/menuSeguridad.jsp";
                    break;
            }

            if (acceso != null) {
                RequestDispatcher vista = request.getRequestDispatcher(acceso);
                vista.forward(request, response);
            }

        } finally {
            if (conexionDB != null) {
                //conexionDB.closeConnection();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }
}
