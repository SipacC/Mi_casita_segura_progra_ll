<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Usuario"%>

<%
    Usuario usr = (Usuario) session.getAttribute("usuario");
    if (usr == null) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return; // detener ejecución
    }
%>

<% if (request.getAttribute("mensajeExito") != null) { %>
    <div class="alert alert-success text-center">
        <%= request.getAttribute("mensajeExito") %>
    </div>
<% } %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Listado de Usuarios</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/usuarios.css">
</head>
<body class="usuarios-page">
<div class="container mt-4">
    <div class="card shadow-lg border-0 rounded-4">
        <div class="card-body p-4">
            <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-3">
                <h2 class="titulo-usuarios mb-3 text-center">Usuarios</h2>
                <div class="d-flex gap-2">
                    <a class="btn btn-success btn-sm" href="ControladorAdmin?accion=add">Crear Usuario</a>
                    <a class="btn btn-primary btn-sm" href="<%= request.getContextPath() %>/vistasAdmin/menuAdministrador.jsp">Regresar</a>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-bordered table-hover align-middle text-center">
                    <thead class="table-dark">
                        <tr>
                            <th>DPI</th>
                            <th>Nombre</th>
                            <th>Apellido</th>
                            <th>Usuario</th>
                            <th>Correo</th>
                            <th>Lote</th>
                            <th>No. Casa</th>
                            <th>Rol</th>
                            <th>Contraseña</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                        List<Usuario> list = (List<Usuario>) request.getAttribute("lista");
                        if (list == null || list.isEmpty()) {
                    %>
                        <tr>
                            <td colspan="12" class="text-center text-danger">No hay registros en la base de datos.</td>
                        </tr>
                    <%
                        } else {
                            boolean hayActivos = false;
                            for (Usuario u : list) {
                                // Mostrar solo los usuarios activos
                                if (u.getEstado() != null && u.getEstado().equalsIgnoreCase("activo")) {
                                    hayActivos = true;
                    %>
                        <tr>
                            <td data-label="DPI"><%= u.getDpi() %></td>
                            <td data-label="Nombre"><%= u.getNombre() %></td>
                            <td data-label="Apellido"><%= u.getApellido() %></td>
                            <td data-label="Usuario"><%= u.getUsuario() %></td>
                            <td data-label="Correo" class="text-break"><%= u.getCorreo() %></td>
                            <td data-label="Lote"><%= u.getLote() %></td>
                            <td data-label="No. Casa"><%= u.getNumeroCasa() %></td>
                            <td data-label="Rol"><%= u.getRol() %></td>
                            <td data-label="Contraseña"><%= u.getContrasena() %></td>
                            <td data-label="Estado"><%= u.getEstado() %></td>
                            <td data-label="Acciones">
                                <div class="d-flex flex-wrap gap-2 justify-content-center">
                                    <a class="btn btn-warning btn-sm" href="ControladorAdmin?accion=editar&id=<%= u.getIdUsuario() %>">Editar</a>
                                    <a class="btn btn-danger btn-sm" href="ControladorAdmin?accion=eliminar&id=<%= u.getIdUsuario() %>" onclick="return confirm('¿Está seguro de eliminar este usuario?');">Eliminar</a>
                                </div>
                            </td>
                        </tr>
                    <%
                                } // fin del if estado activo
                            } // fin del for

                            if (!hayActivos) {
                    %>
                        <tr>
                            <td colspan="12" class="text-center text-danger">No hay usuarios activos.</td>
                        </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
