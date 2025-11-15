<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.Connection"%>
<%@page import="ConexionDB.ConexionDB"%>
<%@page import="ModeloDAO.BitacoraDAO"%>
<%@page import="Modelo.Bitacora"%>

<%
    // ===============================
    // Validar sesión y rol
    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"administrador".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }
    // ===============================

    // Parámetros de filtro
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

    ConexionDB db = new ConexionDB();
    Connection con = db.openConnection();

    // Obtener lista desde DAO con filtro de fecha
    BitacoraDAO dao = new BitacoraDAO(con);
    List<Bitacora> lista = dao.listar(idUsuario, filtroModulo, fecha);
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bitácora del Sistema</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/bitacora.css" rel="stylesheet">
</head>
<body class="bitacora-page container mt-4">

    <h2 class="mb-4">📜 Bitácora del Sistema</h2>

    <form method="get" action="<%= request.getContextPath() %>/ControladorAdmin" class="row g-3 mb-4">
        <input type="hidden" name="accion" value="verBitacora"/>

        <div class="col-md-3">
            <label for="usuario" class="form-label">ID Usuario (actor):</label>
            <input type="text" id="usuario" name="usuario" class="form-control"
                   value="<%= (filtroUsuario != null ? filtroUsuario : "") %>">
        </div>

        <div class="col-md-3">
            <label for="modulo" class="form-label">Módulo:</label>
            <input type="text" id="modulo" name="modulo" class="form-control"
                   value="<%= (filtroModulo != null ? filtroModulo : "") %>">
        </div>

        <!-- 🔹 Nuevo campo de filtro por fecha -->
        <div class="col-md-3">
            <label for="fecha" class="form-label">Fecha:</label>
            <input type="date" id="fecha" name="fecha" class="form-control"
                   value="<%= (filtroFecha != null ? filtroFecha : "") %>">
        </div>

        <div class="col-md-3 d-flex align-items-end">
            <button type="submit" class="btn btn-primary me-2">Filtrar</button>
            <a href="<%= request.getContextPath() %>/ControladorAdmin?accion=verBitacora" 
               class="btn btn-warning">Recargar y limpiar</a>
            <a href="<%= request.getContextPath() %>/vistasAdmin/menuAdministrador.jsp" class="btn btn-secondary">⬅ Volver al Menú</a>
        </div>
    </form>

    <table class="table table-striped table-bordered">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>ID Usuario Actor</th>
                <th>Usuario Actor</th>
                <th>Usuario Afectado</th>
                <th>Acción</th>
                <th>Módulo</th>
                <th>Fecha / Hora</th>
            </tr>
        </thead>
        <tbody>
        <%
            if (lista != null && !lista.isEmpty()) {
                for (Bitacora b : lista) {
        %>
            <tr>
                <td><%= b.getId_bitacora() %></td>
                <td><%= b.getId_usuario() %></td>
                <td><%= b.getUsuario_actor() != null ? b.getUsuario_actor() : "-" %></td>
                <td><%= b.getUsuario_afectado() != null ? b.getUsuario_afectado() : "-" %></td>
                <td><%= b.getAccion() %></td>
                <td><%= b.getModulo() %></td>
                <td><%= b.getFecha_hora() %></td>
            </tr>
        <%
                }
            } else {
        %>
            <tr>
                <td colspan="7" class="text-center">No hay registros en la bitácora</td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>

</body>
</html>
