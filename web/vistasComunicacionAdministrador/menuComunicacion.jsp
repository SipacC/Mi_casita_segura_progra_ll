<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Modelo.Usuario"%>
<%
    Usuario usr = (Usuario) session.getAttribute("usuario");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Comunicación Interna - Administración</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>

<body class="bg-light">
<div class="container mt-4">

    <h3 class="mb-3 text-primary">
        <i class="fas fa-comments"></i> Comunicación Interna
    </h3>

    <div class="alert alert-info">
        Bienvenido <strong><%= usr.getNombre() %></strong>.  
        Aquí podrás comunicarte con residentes y agentes de seguridad.
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body text-center">
            <h5 class="card-title">Panel de Comunicación</h5>
            <p class="text-muted">Próximamente podrás revisar los mensajes de residentes y agentes, así como enviar notificaciones globales.</p>
        </div>
    </div>

    <div class="mt-4 text-end">
        <a href="<%= request.getContextPath() %>/ControladorAdmin?accion=menu"
           class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Regresar al Menú Principal
        </a>
    </div>

</div>
</body>
</html>
