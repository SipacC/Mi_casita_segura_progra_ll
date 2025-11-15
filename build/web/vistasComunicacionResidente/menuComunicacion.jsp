<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Modelo.Usuario"%>

<%
    // Validar sesión y rol
    Usuario usr = (Usuario) session.getAttribute("usuario");
    if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    // Obtener mensajes de éxito o error enviados desde el controlador
    String mensajeExito = (String) request.getAttribute("mensajeExito");
    String mensajeError = (String) request.getAttribute("mensajeError");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Comunicación Interna</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>

<body class="bg-light">
<div class="container mt-5">

    <h3 class="text-primary mb-4 text-center">
        <i class="fas fa-comments"></i> Comunicación Interna
    </h3>

    <div class="alert alert-info text-center">
        Bienvenido <strong><%= usr.getNombre() %></strong>.  
        Desde aquí puedes comunicarte con el personal de seguridad o reportar un incidente.
    </div>

    <!-- 🔹 Mostrar mensajes de resultado del reporte -->
    <% if (mensajeExito != null) { %>
        <div class="alert alert-success alert-dismissible fade show text-center" role="alert">
            <i class="fas fa-check-circle"></i> <%= mensajeExito %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } else if (mensajeError != null) { %>
        <div class="alert alert-danger alert-dismissible fade show text-center" role="alert">
            <i class="fas fa-exclamation-circle"></i> <%= mensajeError %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <div class="row mt-4 justify-content-center">

        <!-- Opción 1: Reportar Incidente -->
        <div class="col-md-5 mb-3">
            <div class="card shadow-sm border-0 h-100 text-center">
                <div class="card-body">
                    <i class="fas fa-bullhorn fa-3x text-danger mb-3"></i>
                    <h5 class="card-title">Reportar Incidente</h5>
                    <p class="card-text text-muted">
                        Envía un reporte de emergencia o actividad sospechosa al personal de seguridad.
                    </p>
                    <a href="ControladorComunicacionResidente?accion=reportarIncidente"
                       class="btn btn-danger">
                        <i class="fas fa-exclamation-triangle"></i> Reportar
                    </a>
                </div>
            </div>
        </div>

        <!-- Opción 2: Consulta General tiene buenas notificacines -->
        <div class="col-md-5 mb-3">
            <div class="card shadow-sm border-0 h-100 text-center">
                <div class="card-body">
                    <i class="fas fa-comments fa-3x text-success mb-3"></i>
                    <h5 class="card-title">Consulta General</h5>
                    <p class="card-text text-muted">
                        Comunícate directamente con un guardia de seguridad.
                    </p>
                    <a href="ControladorComunicacionResidente?accion=menuChats
"
                       class="btn btn-success">
                        <i class="fas fa-comment-dots"></i> Ir al chat
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="mt-4 text-end">
        <a href="<%= request.getContextPath() %>/ControladorResidente?accion=menu"
           class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Regresar
        </a>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
