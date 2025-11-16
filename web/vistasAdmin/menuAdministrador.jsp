<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // Proxies

    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"administrador".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return; // detener ejecución del JSP
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Menú Administrador - Residencial Xibalbá</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/admin.css">
</head>
<body class="admin-page">

<header class="admin-header">
    <div class="container d-flex justify-content-between align-items-center">
        <div class="logo">
            <i class="fas fa-building"></i> Residencial Xibalbá
        </div>
        <div class="user-info">
            <span>Bienvenido, <strong><%= usr.getNombre() %> <%= usr.getApellido() %></strong></span>
            <a class="btn btn-danger btn-sm ms-3" href="<%= request.getContextPath() %>/ControladorLogin?accion=logout">
                <i class="fas fa-sign-out-alt"></i> Cerrar sesión
            </a>
        </div>
    </div>
</header>

<main class="container my-5">
    <section class="hero text-center">
        <h1>Panel de Administración</h1>
        <p>Gestiona usuarios, cámaras y eventos desde un solo lugar.</p>
    </section>

    <section class="dashboard mt-4">
        <div class="row g-4">
            <!-- Usuarios -->
            <div class="col-12 col-md-4">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-users"></i></div>
                    <h3>Mantenimiento de usuarios</h3>
                    <p>Administra residentes, roles y accesos al sistema.</p>
                    <a class="btn btn-primary card-btn" href="<%= request.getContextPath() %>/ControladorAdmin?accion=listar">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>
            <!-- Cámaras -->
            <div class="col-12 col-md-4">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-video"></i></div>
                    <h3>Administrador de Cámaras</h3>
                    <p>Monitorea en tiempo real y gestiona la seguridad.</p>
                    <a class="btn btn-primary card-btn" href="<%= request.getContextPath() %>/ControladorAdmin?accion=camaras">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>
            <!-- Bitácora -->
            <!--<div class="col-12 col-md-4">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-clipboard-list"></i></div>
                    <h3>Bitácora de eventos</h3>
                    <p>Consulta registros de accesos, incidentes y actividades.</p>
                    <a class="btn btn-primary card-btn" href="<%= request.getContextPath() %>/ControladorAdmin?accion=verBitacora">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>
            <!-- Comunicación Interna -->
            <div class="col-12 col-md-3">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-comments"></i></div>
                    <h3>Comunicación Interna</h3>
                    <p>Intercambia mensajes con residentes y personal de seguridad.</p>
                    <a class="btn btn-primary card-btn"
                    href="<%= request.getContextPath() %>/ControladorComunicacionAdmin?accion=menu">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>
        </div>
    </section>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
