package Gestion_bitacora;

import ModeloDAO.BitacoraDAO;
import Modelo.Usuario;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class RegistroBitacora {

    private RegistroBitacora() {}

    public static void log(HttpServletRequest req, String accion, String modulo, Connection con) {
        HttpSession ses = (req != null) ? req.getSession(false) : null;
        Usuario actor = (ses != null) ? (Usuario) ses.getAttribute("usuario") : null;
        int idActor = (actor != null) ? actor.getIdUsuario() : 0;
        new BitacoraDAO(con).registrarAccion(idActor, accion, modulo);
    }
    
    
}
