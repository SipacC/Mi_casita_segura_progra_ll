<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    String errorAuth0 = (String) request.getAttribute("errorAuth0");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Acceso Denegado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8d7da;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .denegado-container {
            max-width: 500px;
            background: #fff;
            border: 2px solid #f5c6cb;
            border-radius: 12px;
            padding: 2rem;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        h2 {
            color: #721c24;
        }
    </style>
</head>
<body>
    <div class="denegado-container">
        <h2>Acceso Denegado</h2>
        <p>No tienes permisos para acceder a esta sección.</p>

        <% if (errorAuth0 != null) { %>
            <div class="alert alert-danger mt-3">
                <strong>Error:</strong> <%= errorAuth0 %>
            </div>
        <% } %>

        <a href="<%= request.getContextPath() %>/ControladorLogin?accion=login" class="btn btn-danger mt-3">
            Volver al Login
        </a>
    </div>
</body>
</html>
