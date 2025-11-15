<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Reserva"%>
<%
    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    // Recuperar atributos enviados desde el Controlador
    String mensaje = (String) request.getAttribute("mensaje");
    String error = (String) request.getAttribute("error");
    String busqueda = (String) request.getAttribute("busqueda");
    List<Reserva> listaReservas = (List<Reserva>) request.getAttribute("listaReservas");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reservas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2 class="text-center">Reservas de Áreas Comunes</h2>
    <p class="text-center">Residente: <strong><%= usr.getNombre() %> <%= usr.getApellido() %></strong></p>

    <!-- Botones volver y crear nueva -->
    <div class="d-flex justify-content-between mb-3">
        <a href="<%= request.getContextPath() %>/vistasResidente/menuResidente.jsp"
           class="btn btn-secondary">⬅ Volver al menú</a>
        <a href="ControladorResidente?accion=crearReserva" class="btn btn-success">Crear nueva reserva</a>
    </div>

    <!-- Mensajes -->
    <% if (mensaje != null) { %>
        <div class="alert alert-success"><%= mensaje %></div>
    <% } %>
    <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
    <% } %>

    <!-- Buscar -->
    <form action="ControladorResidente" method="get" class="row g-2 mb-3">
        <input type="hidden" name="accion" value="reservas">
        <div class="col-md-8">
            <input type="text" class="form-control" name="buscar" placeholder="Buscar por nombre de área"
                   value="<%= (busqueda != null ? busqueda : "") %>">
        </div>
        <div class="col-md-4">
            <button class="btn btn-primary w-100">Buscar</button>
        </div>
    </form>

   
    <table class="table table-bordered table-striped">
        <thead class="table-dark">
        <tr>
            <th>Área</th>
            <th>Fecha</th>
            <th>Hora inicio</th>
            <th>Hora fin</th>
            <th>Estado</th>
            <th>Acciones</th>
        </tr>
        </thead>
        <tbody>
        <%
            if (listaReservas != null && !listaReservas.isEmpty()) {
                for (Reserva res : listaReservas) {
        %>
            <tr>
                <td><%= res.getNombreArea() %></td>
                <td><%= res.getFecha_reserva() %></td>
                <td><%= res.getHora_inicio() %></td>
                <td><%= res.getHora_fin() %></td>
                <td><%= res.getEstado() %></td>
                <td>
                    <% if ("Activa".equalsIgnoreCase(res.getEstado())) { %>
                        <a href="ControladorResidente?accion=cancelarReserva&id=<%= res.getId_reserva() %>"
                           class="btn btn-danger btn-sm"
                           onclick="return confirm('¿Cancelar esta reserva?')">❌ Cancelar</a>
                    <% } %>
                </td>
            </tr>
        <%
                }
            } else {
        %>
            <tr>
                <td colspan="6" class="text-center">No tienes reservas registradas.</td>
            </tr>

            <% if (busqueda != null && !busqueda.trim().isEmpty()) { %>
                <tr>
                    <td colspan="6">
                        <div class="alert alert-warning d-flex justify-content-between align-items-center mt-3">
                            <span>No se encontraron reservas con "<%= busqueda %>".</span>
                            <button class="btn btn-sm btn-primary"
                                    onclick="window.location.href='ControladorResidente?accion=reservas'">
                                Aceptar
                            </button>
                        </div>
                    </td>
                </tr>
            <% } %>
        <%
            }
        %>
        </tbody>
    </table>
</div>
</body>
</html>
