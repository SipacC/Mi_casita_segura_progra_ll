<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"seguridad".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Menú Seguridad - Residencial Xibalbá</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/seguridad.css">
</head>
<body class="admin-page bg-dark text-white">

<header class="admin-header bg-dark text-white py-3 shadow">
    <div class="container d-flex justify-content-between align-items-center">
        <div class="logo fs-5">
            <i class="fas fa-shield-alt"></i> Residencial Xibalbá - Seguridad
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
    <section class="hero text-center mb-5">
        <h1 class="fw-bold">Panel de Seguridad</h1>
        <p class="text-secondary">Administra la paquetería, el directorio general y la comunicación interna.</p>
    </section>

    <section class="dashboard">
        <div class="row g-4 justify-content-center">

            <!-- Directorio General -->
            <div class="col-12 col-md-4">
                <div class="card h-100 text-center border-info shadow">
                    <div class="card-body">
                        <div class="card-icon text-info mb-3"><i class="fas fa-address-book fa-3x"></i></div>
                        <h3 class="card-title text-info">Directorio General</h3>
                        <p class="text-muted">Consulta información de residentes y personal activo.</p>
                        <a class="btn btn-info text-white" 
                           href="<%= request.getContextPath() %>/ControladorSeguridad?accion=directorio">
                            <i class="fas fa-arrow-right"></i> Acceder
                        </a>
                    </div>
                </div>
            </div>

            <!-- Módulo de Paquetería -->
            <div class="col-12 col-md-4">
                <div class="card h-100 text-center border-warning shadow">
                    <div class="card-body">
                        <div class="card-icon text-warning mb-3"><i class="fas fa-box fa-3x"></i></div>
                        <h3 class="card-title text-warning">Paquetería</h3>
                        <p class="text-muted">Registra la recepción y entrega de paquetes.</p>
                        <a class="btn btn-warning text-white" 
                           href="<%= request.getContextPath() %>/ControladorSeguridad?accion=paqueteria">
                            <i class="fas fa-arrow-right"></i> Acceder
                        </a>
                    </div>
                </div>
            </div>

            <!-- Comunicación Interna -->
            <div class="col-12 col-md-4">
                <div class="card h-100 text-center border-primary shadow">
                    <div class="card-body">
                        <div class="card-icon text-primary mb-3"><i class="fas fa-headset fa-3x"></i></div>
                        <h3 class="card-title text-primary">Comunicación Interna</h3>
                        <p class="text-muted">Contacta con residentes o administración.</p>
                        <a class="btn btn-primary text-white"
                           href="<%= request.getContextPath() %>/ControladorComunicacionSeguridad?accion=menu">
                            <i class="fas fa-arrow-right"></i> Acceder
                        </a>
                    </div>
                </div>
            </div>

        </div>
    </section>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
