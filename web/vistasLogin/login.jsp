<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr != null) {
        if ("administrador".equalsIgnoreCase(usr.getRol())) {
            response.sendRedirect(request.getContextPath() + "/ControladorAdmin?accion=menu");
        } else if ("residente".equalsIgnoreCase(usr.getRol())) {
            response.sendRedirect(request.getContextPath() + "/ControladorResidente?accion=menu");
        } else if ("seguridad".equalsIgnoreCase(usr.getRol())) {
            response.sendRedirect(request.getContextPath() + "/ControladorSeguridad?accion=menu");
        }
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Residencial Xibalbá</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- CSS login exclusivo -->
    <link rel="stylesheet" href="css/login.css">
</head>
<body class="login-page">

    <div class="container">
        <div class="login-card">
            <div class="logo">
                <i class="fas fa-building"></i>
                <h1>Residencial Xibalbá</h1>
                <p>Sistema de Administración</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="error-message"><%= request.getAttribute("error") %></div>
            <% } %>

            <form action="ControladorLogin" method="post" autocomplete="off">
                <input type="hidden" name="accion" value="validar">

                <!-- Usuario -->
                <div class="form-group">
                    <label for="usuario">Usuario</label>
                    <div class="input-with-icon">
                        <i class="fas fa-user"></i>
                        <input type="text" id="usuario" name="usuario" class="form-control" placeholder="Ingrese su usuario" required>
                    </div>
                </div>

                <!-- Contraseña -->
                <div class="form-group">
                    <label for="contrasena">Contraseña</label>
                    <div class="input-with-icon">
                        <i class="fas fa-lock"></i>
                        <input type="password" id="contrasena" name="contrasena" class="form-control" placeholder="Ingrese su contraseña" required>
                        <span class="password-toggle" id="passwordToggle"><i class="fas fa-eye"></i></span>
                    </div>
                </div>

                <!-- Botón -->
                <button type="submit" class="btn-login">Entrar</button>
            </form>

            <p class="footer">&copy; 2025 - Residencial Xibalbá</p>
        </div>
    </div>

    <script>
        // Mostrar / ocultar contraseña
        document.getElementById("passwordToggle").addEventListener("click", function() {
            const input = document.getElementById("contrasena");
            const icon = this.querySelector("i");

            if (input.type === "password") {
                input.type = "text";
                icon.classList.remove("fa-eye");
                icon.classList.add("fa-eye-slash");
            } else {
                input.type = "password";
                icon.classList.remove("fa-eye-slash");
                icon.classList.add("fa-eye");
            }
        });
    </script>
</body>
</html>
