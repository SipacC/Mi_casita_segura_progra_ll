package Controlador;

import Modelo.Usuario;
import ModeloDAO.UsuarioDAO;
import Gestion_bitacora.RegistroBitacora;
import ConexionDB.ConexionDB;

import java.io.IOException;
import java.sql.Connection;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControladorLogin extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("accion");
        if (action == null || action.isEmpty()) {
            action = "login";
        }

        if (action.equalsIgnoreCase("login")) {
            RequestDispatcher vista = request.getRequestDispatcher("/vistasLogin/login.jsp");
            vista.forward(request, response);

        } else if (action.equalsIgnoreCase("logout")) {
            javax.servlet.http.HttpSession s = request.getSession(false);
            if (s != null) {
                Usuario u = (Usuario) s.getAttribute("usuario");
                if (u != null) {
                    try {
                       ConexionDB db = new ConexionDB();
                       Connection con = db.openConnection();
                       RegistroBitacora.log(request, "Cierre de sesión", "Login", con);
                    } catch (Exception e) {
                        System.err.println("Error registrando bitácora de logout: " + e.getMessage());
                    }
                }
                s.invalidate();
            }
            response.sendRedirect("ControladorLogin?accion=login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("accion");

        if ("validar".equalsIgnoreCase(action)) {

            String usuarioParam = request.getParameter("usuario");
            String contrasena = request.getParameter("contrasena");

            ConexionDB db = new ConexionDB();
            Connection con = db.openConnection();
            UsuarioDAO dao = new UsuarioDAO(con);

            try {Usuario usuario = dao.findByLogin(usuarioParam, contrasena);

                if (usuario != null) {
                    request.getSession().invalidate();
                    javax.servlet.http.HttpSession newSession = request.getSession(true);
                    newSession.setAttribute("usuario", usuario);
                    newSession.setAttribute("rol", usuario.getRol().toLowerCase());

                    RegistroBitacora.log(request, "Inicio de sesión", "Login", con);

                    String rol = usuario.getRol().toLowerCase();
                    switch (rol) {
                        case "administrador":
                            response.sendRedirect("ControladorAdmin?accion=menu");
                            break;
                        case "residente":
                            response.sendRedirect("ControladorResidente?accion=menu");
                            break;
                        case "seguridad":
                            response.sendRedirect("ControladorSeguridad?accion=menu");
                            break;
                        default:
                            newSession.invalidate();
                            request.setAttribute("error", "Rol no autorizado");
                            RequestDispatcher vista = request.getRequestDispatcher("/vistasLogin/login.jsp");
                            vista.forward(request, response);
                            break;
                    }

                } else {
                    request.setAttribute("error", "Usuario o contraseña incorrectos");
                    RequestDispatcher vista = request.getRequestDispatcher("/vistasLogin/login.jsp");
                    vista.forward(request, response);
                }

            } catch (Exception e) {
                System.err.println("Error en ControladorLogin: " + e.getMessage());
                request.setAttribute("error", "Error interno en el servidor");
                RequestDispatcher vista = request.getRequestDispatcher("/vistasLogin/login.jsp");
                vista.forward(request, response);
            }

        } else {
            doGet(request, response);
        }
    }
}
