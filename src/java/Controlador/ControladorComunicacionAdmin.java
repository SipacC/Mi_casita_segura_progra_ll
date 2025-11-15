package Controlador;

import Modelo.Usuario;
import ConexionDB.ConexionDB;
import Gestion_bitacora.RegistroBitacora;
import java.io.IOException;
import java.sql.Connection;
import javax.servlet.*;
import javax.servlet.http.*;

public class ControladorComunicacionAdmin extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usr = (Usuario) (session != null ? session.getAttribute("usuario") : null);

        if (usr == null || !"administrador".equalsIgnoreCase(usr.getRol())) {
            response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) accion = "menu";

        ConexionDB db = new ConexionDB();
        Connection con = db.openConnection();

        try {
            switch (accion.toLowerCase()) {

                case "menu":
                    RegistroBitacora.log(request, "Ingreso al módulo Comunicación Interna", "Comunicación Admin", con);
                    request.getRequestDispatcher("/vistasComunicacionAdministrador/menuComunicacion.jsp")
                           .forward(request, response);
                    break;

                default:
                    response.sendRedirect("ControladorComunicacionAdministrador.java?accion=menu");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error en módulo de Comunicación Interna (Administrador).");
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
