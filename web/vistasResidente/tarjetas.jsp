<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Tarjeta"%>
<%
    List<Tarjeta> tarjetas = (List<Tarjeta>) request.getAttribute("listaTarjetas");
%>
<% if (request.getAttribute("mensaje") != null) { %>
    <div class="alert alert-success">
        <%= request.getAttribute("mensaje") %>
    </div>
<% } %>
<% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger">
        <%= request.getAttribute("error") %>
    </div>
<% } %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Gestionar Tarjetas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2>💳 Mis Tarjetas</h2>

    <div class="d-flex justify-content-between mb-3">
    <a href="<%= request.getContextPath() %>/ControladorResidente?accion=registrarTarjeta" 
       class="btn btn-primary mb-3">➕ Registrar nueva tarjeta</a>
    <a href="<%= request.getContextPath() %>/ControladorResidente?accion=menu" 
       class="btn btn-secondary">⬅️ Regresar al menú</a>
    </div>
    
    <table class="table table-bordered table-hover">
        <thead class="table-dark">
            <tr>
                <th>Alias</th>
                <th>Número</th>
                <th>Vencimiento</th>
                <th>Titular</th>
                <th>CVV</th>
                <th>Tipo</th>
                <th>Saldo (Q)</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
        <%
            if (tarjetas != null && !tarjetas.isEmpty()) {
                for (Tarjeta t : tarjetas) {
        %>
            <tr>
                <td><%= t.getNombreTarjeta() %></td>
                <td><%= t.getNumeroTarjeta() %></td>
                <td><%= t.getFechaVencimiento() %></td>
                <td><%= t.getNombreTitular() %></td>
                <td><%= t.getCvv() %></td>
                <td><%= t.getTipoTarjeta() %></td>
                <td><%= t.getSaldo() %></td>
                <td>
                    <a href="<%= request.getContextPath() %>/ControladorResidente?accion=editarTarjeta&id=<%= t.getIdTarjeta() %>" 
                       class="btn btn-warning btn-sm">✏️ Editar</a>
                    <a href="<%= request.getContextPath() %>/ControladorResidente?accion=eliminarTarjeta&id=<%= t.getIdTarjeta() %>" 
                       class="btn btn-danger btn-sm"
                       onclick="return confirm('¿Seguro que deseas eliminar esta tarjeta?')">🗑️ Eliminar</a>
                </td>
            </tr>
        <%
                }
            } else {
        %>
            <tr><td colspan="8" class="text-center">No tienes tarjetas registradas.</td></tr>
        <%
            }
        %>
        </tbody>
    </table>
</div>
</body>
</html>
