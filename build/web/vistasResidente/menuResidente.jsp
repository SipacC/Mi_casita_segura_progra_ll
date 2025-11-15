<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Menú Residente - Residencial Xibalbá</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/residente.css">
</head>
<body class="admin-page">

<header class="admin-header">
    <div class="container d-flex justify-content-between align-items-center">
        <div class="logo">
            <i class="fas fa-home"></i> Residencial Xibalbá
        </div>
        <div class="user-info">
            <span>Bienvenido residente, <strong><%= usr.getNombre() %> <%= usr.getApellido() %></strong></span>
            <a class="btn btn-danger btn-sm ms-3"
               href="<%= request.getContextPath() %>/ControladorLogin?accion=logout">
                <i class="fas fa-sign-out-alt"></i> Cerrar sesión
            </a>
        </div>
    </div>
</header>

<main class="container my-5">
    <section class="hero text-center">
        <h1>Panel del Residente</h1>
        <p>Administra tus pagos, tarjetas, reservas, visitas y más.</p>
    </section>

    <section class="dashboard mt-4">
        <div class="row g-4">

            <!-- PAGOS -->
            <div class="col-12 col-md-3">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-credit-card"></i></div>
                    <h3>Gestión de pagos</h3>
                    <p>Consulta y registra tus pagos de mantenimiento o multas.</p>
                    <a class="btn btn-primary card-btn"
                       href="<%= request.getContextPath() %>/ControladorResidente?accion=pagos">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>

            <!-- TARJETAS -->
            <div class="col-12 col-md-3">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-id-card"></i></div>
                    <h3>Tarjetas</h3>
                    <p>Gestiona tus tarjetas registradas y revisa su saldo.</p>
                    <a class="btn btn-primary card-btn"
                       href="<%= request.getContextPath() %>/ControladorResidente?accion=tarjetas">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>

            <!-- RESERVAS -->
            <div class="col-12 col-md-3">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-calendar-alt"></i></div>
                    <h3>Reservas</h3>
                    <p>Administra tus reservas de áreas comunes.</p>
                    <a class="btn btn-primary card-btn"
                       href="<%= request.getContextPath() %>/ControladorResidente?accion=reservas">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>

            <!-- VISITAS -->
            <div class="col-12 col-md-3">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-users"></i></div>
                    <h3>Visitas</h3>
                    <p>Registra y controla el acceso de tus visitantes.</p>
                    <a class="btn btn-primary card-btn"
                       href="<%= request.getContextPath() %>/ControladorResidente?accion=visitas">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>

            <!-- COMUNICACIÓN INTERNA -->
            <div class="col-12 col-md-3">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-comments"></i></div>
                    <h3>Comunicación Interna</h3>
                    <p>Contacta al personal de seguridad o administración.</p>
                    <a class="btn btn-primary card-btn"
                       href="<%= request.getContextPath() %>/ControladorComunicacionResidente?accion=menu">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>

            <!-- DIRECTORIO -->
            <div class="col-12 col-md-3">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-address-book"></i></div>
                    <h3>Directorio</h3>
                    <p>Consulta la información de residentes y personal de seguridad.</p>
                    <a class="btn btn-primary card-btn"
                       href="<%= request.getContextPath() %>/ControladorResidente?accion=directorio">
                        <i class="fas fa-arrow-right"></i> Acceder
                    </a>
                </div>
            </div>

            <!-- REPORTE DE MANTENIMIENTO -->
            <div class="col-12 col-md-3">
                <div class="card dashboard-card h-100 text-center">
                    <div class="card-icon"><i class="fas fa-tools"></i></div>
                    <h3>Reporte de Mantenimiento</h3>
                    <p>Envía y consulta reportes de mantenimiento o fallas.</p>
                    <a class="btn btn-primary card-btn"
                       href="<%= request.getContextPath() %>/ControladorResidente?accion=reporteMantenimiento">
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
