package Controlador;

import ConexionDB.ConexionDB;
import java.sql.Connection;

import Modelo.Usuario;
import ModeloDAO.UsuarioDAO;
import ModeloDAO.BitacoraDAO;
import Gestion_bitacora.RegistroBitacora;
import Gestion_qr.CrearCodigo;
import Gestion_qr.CrearQr;
import Gestion_qr.RegistrarQrAcceso;
import Gestion_qr.correoEnviarQr;
import ModeloDAO.CatalogoDAO;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControladorAdmin extends HttpServlet {

    private ConexionDB conexionDB;
    private Connection con;


    String menuAdmin = "vistasAdmin/menuAdministrador.jsp";
    String listar = "vistasAdmin/listar.jsp";
    String add = "vistasAdmin/add.jsp";
    String edit = "vistasAdmin/edit.jsp";
    String menuCamaras = "vistasAdmin/menuCamaras.jsp";
    String bitacora = "vistasAdmin/bitacora.jsp";

    Usuario u = new Usuario();
    int id;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getSession(false) == null || request.getSession().getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
            return;
        }

        conexionDB = new ConexionDB();
        con = conexionDB.openConnection();

        UsuarioDAO dao = new UsuarioDAO(con);

        String acceso = "";
        String action = request.getParameter("accion");

        if (action == null || action.isEmpty()) {
            action = "menu";
        }

        if (action.equalsIgnoreCase("menu")) {
            acceso = menuAdmin;

        } else if (action.equalsIgnoreCase("listar")) {
            request.setAttribute("lista", dao.listar());
            acceso = listar;

        } else if (action.equalsIgnoreCase("add")) {

             CatalogoDAO catalogoDAO = new CatalogoDAO(con);

             request.setAttribute("roles", catalogoDAO.listarRoles());
             request.setAttribute("lotes", catalogoDAO.listarLotes());
             request.setAttribute("casas", catalogoDAO.listarNumerosCasa());

             acceso = add;

        } else if (action.equalsIgnoreCase("Agregar")) {
            String usuario = request.getParameter("txtUsuario");
            String correo = request.getParameter("txtCorreo");

            if (dao.existeUsuarioOCorreoODpi(usuario, correo, request.getParameter("txtDpi"))) {
                request.setAttribute("error", "El DPI,correo, usuario ya están registrados.");
                acceso = add;
            } else {
                u.setDpi(request.getParameter("txtDpi"));
                u.setNombre(request.getParameter("txtNombre"));
                u.setApellido(request.getParameter("txtApellido"));
                u.setUsuario(usuario);
                u.setCorreo(correo);
                u.setRol(request.getParameter("txtRol"));
                u.setLote(request.getParameter("txtLote"));
                u.setNumeroCasa(request.getParameter("txtNumeroCasa"));
                u.setEstado("activo");
                u.setContrasena(request.getParameter("txtContrasena"));

                int idNuevo = dao.add(u);

                if (idNuevo > 0) {
                    String codigoQR = CrearCodigo.generarCodigo();
                    String rutaQR = CrearQr.generarQR(idNuevo, codigoQR);

                    RegistrarQrAcceso registrar = new RegistrarQrAcceso();
                    registrar.registrarQrYAcceso(idNuevo, codigoQR, u.getRol(), rutaQR);

                    correoEnviarQr correoQR = new correoEnviarQr();
                    boolean enviado = correoQR.enviarConQR(
                        u.getCorreo(),
                        u.getNombre() + " " + u.getApellido(),
                        u.getUsuario(),
                        u.getContrasena(),
                        rutaQR
                    );


                    if (enviado) {
                        System.out.println("Correo con QR enviado a " + u.getCorreo());
                    } else {
                        System.err.println("No se pudo enviar el correo con QR a " + u.getCorreo());
                    }

                    RegistroBitacora.log(request, "Creó un nuevo usuario con ID " + idNuevo, "Agregar usuario", con);
                } else {
                    System.err.println("Error al crear el usuario.");
                }
                response.sendRedirect("ControladorAdmin?accion=listar");
                return;
            }

        } else if (action.equalsIgnoreCase("editar")) {
            request.setAttribute("idper", request.getParameter("id"));

            CatalogoDAO catalogoDAO = new CatalogoDAO(con);

            request.setAttribute("roles", catalogoDAO.listarRoles());
            request.setAttribute("lotes", catalogoDAO.listarLotes());
            request.setAttribute("casas", catalogoDAO.listarNumerosCasa());

            acceso = edit;

        } else if (action.equalsIgnoreCase("Actualizar")) {
            id = Integer.parseInt(request.getParameter("txtid"));
            u.setIdUsuario(id);

            String usuario = request.getParameter("txtUsuario");
            String correo = request.getParameter("txtCorreo");

            if (dao.existeUsuarioOCorreoEdit(usuario, correo, id)) {
                request.setAttribute("error", "El usuario o correo ya existe, por favor use otro.");
                request.setAttribute("idper", id);
            } else {
                Map<String, java.util.function.Consumer<String>> setters = new LinkedHashMap<>();
                setters.put("txtDpi", valor -> u.setDpi(valor));
                setters.put("txtNombre", valor -> u.setNombre(valor));
                setters.put("txtApellido", valor -> u.setApellido(valor));
                setters.put("txtUsuario", valor -> u.setUsuario(valor));
                setters.put("txtCorreo", valor -> u.setCorreo(valor));
                setters.put("txtRol", valor -> u.setRol(valor));
                setters.put("txtLote", valor -> u.setLote(valor));
                setters.put("txtNumeroCasa", valor -> u.setNumeroCasa(valor));
                setters.put("txtContrasena", valor -> u.setContrasena(valor));

                for (Map.Entry<String, java.util.function.Consumer<String>> entry : setters.entrySet()) {
                    String valor = request.getParameter(entry.getKey());
                    if (valor != null && !valor.trim().isEmpty()) {
                        entry.getValue().accept(valor);
                    }
                }

                dao.edit(u);
                RegistroBitacora.log(request, "Actualizó datos del usuario con ID " + u.getIdUsuario(), "Editar Usuario", con);
                response.sendRedirect("ControladorAdmin?accion=listar");
                return;
            }

        } else if (action.equalsIgnoreCase("eliminar")) {
            id = Integer.parseInt(request.getParameter("id"));
            
            boolean exito = dao.eliminar(id);
            
            if (exito) {
                RegistroBitacora.log(request, "Marcó como inactivo al usuario con ID " + id, "Usuarios", con);
                request.setAttribute("mensajeExito", "Usuario eliminado correctamente");
            } else {
                request.setAttribute("mensajeError", "No se pudo eliminarar el usuario.");
            }
            
            request.setAttribute("lista", dao.listar());
            acceso = listar;

        } else if (action.equalsIgnoreCase("camaras")) {
            acceso = "vistasAdmin/camaras.jsp";

        } else if (action.equalsIgnoreCase("verBitacora")) {
            String filtroUsuario = request.getParameter("usuario");
            String filtroModulo = request.getParameter("modulo");
            String filtroFecha = request.getParameter("fecha");

            Integer idUsuario = null;
            if (filtroUsuario != null && !filtroUsuario.trim().isEmpty()) {
                try {
                    idUsuario = Integer.parseInt(filtroUsuario);
                } catch (Exception e) {
                    idUsuario = null;
                }
            }

            java.sql.Date fecha = null;
            if (filtroFecha != null && !filtroFecha.trim().isEmpty()) {
                try {
                    fecha = java.sql.Date.valueOf(filtroFecha);
                } catch (Exception e) {
                    fecha = null;
                }
            }

            BitacoraDAO bitacoraDAO = new BitacoraDAO(con);
            request.setAttribute("listaBitacora", bitacoraDAO.listar(idUsuario, filtroModulo, fecha));

            acceso = bitacora;
        }

        RequestDispatcher vista = request.getRequestDispatcher(acceso);

        try {
        vista.forward(request, response);
        } finally {
            if (conexionDB != null) {
            //conexionDB.closeConnection();
        }
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
