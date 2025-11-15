<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Visita"%>
<%
    List<Visita> listaVisitas = (List<Visita>) request.getAttribute("listaVisitas");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Visitas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2 class="mb-3">👥 Listado de Visitas</h2>

    <div class="d-flex justify-content-between mb-3">
        <a href="<%=request.getContextPath()%>/ControladorResidente?accion=registrarVisita" 
           class="btn btn-primary">➕ Registrar nueva visita</a>

        <a href="<%= request.getContextPath() %>/ControladorResidente?accion=menu" 
           class="btn btn-secondary">⬅️ Regresar al menú</a>
   </div>

   <!-- ✅ Mensajes dinámicos -->
   <% if (request.getAttribute("mensaje") != null) { %>
       <div class="alert alert-success alert-dismissible fade show" role="alert">
           <%= request.getAttribute("mensaje") %>
           <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
       </div>
   <% } %>

   <% if (request.getAttribute("error") != null) { %>
       <div class="alert alert-danger alert-dismissible fade show" role="alert">
           <%= request.getAttribute("error") %>
           <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
       </div>
   <% } %>

    <table class="table table-bordered table-hover">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>DPI</th>
                <th>Tipo</th>
                <th>Correo</th>
                <th>Motivo</th> <!-- Nuevo campo -->
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
        <% if (listaVisitas != null && !listaVisitas.isEmpty()) {
               for (Visita v : listaVisitas) { %>
            <tr>
                <td><%= v.getId_visita() %></td>
                <td><%= v.getNombre() %></td>
                <td><%= v.getDpi_visita() %></td>
                <td><%= v.getTipo_visita() %></td>
                <td><%= v.getCorreo_visita() %></td>
                <td><%= v.getMotivo() %></td> <!-- Mostrar motivo -->
                <td>
                    <% if ("activo".equalsIgnoreCase(v.getEstado())) { %>
                        <span class="badge bg-success">Activo</span>
                    <% } else { %>
                        <span class="badge bg-danger">Cancelado</span>
                    <% } %>
                </td>
                <td>
                    <% if ("activo".equalsIgnoreCase(v.getEstado())) { %>
                        <!-- Botón cancelar -->
                        <a href="<%=request.getContextPath()%>/ControladorResidente?accion=cancelarVisita&id=<%=v.getId_visita()%>" 
                           class="btn btn-sm btn-danger mb-1">❌ Cancelar</a>

                        <!-- Botón descargar QR -->
                        <a href="<%=request.getContextPath()%>/ControladorResidente?accion=descargarQRVisita&id=<%=v.getId_visita()%>" 
                           class="btn btn-sm btn-success">⬇️ Descargar QR</a>
                    <% } else { %>
                        <span class="text-muted">No disponible</span>
                    <% } %>
                </td>
            </tr>
        <%   }
           } else { %>
            <tr>
                <td colspan="8" class="text-center">No hay visitas registradas.</td>
            </tr>
        <% } %>
        </tbody>
    </table>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
