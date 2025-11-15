<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Error en el sistema</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-5">

    <div class="alert alert-danger text-center">
        <h2>⚠️ Ha ocurrido un error</h2>
        <p>
            <% 
                String error = (String) request.getAttribute("error");
                if (error != null) { 
            %>
                <strong><%= error %></strong>
            <% 
                } else { 
            %>
                Ocurrió un error inesperado. Inténtalo nuevamente.
            <% } %>
        </p>
        <a href="<%= request.getContextPath() %>/ControladorResidente?accion=pagos" class="btn btn-secondary mt-3">
            ⬅ Volver a Pagos
        </a>
    </div>

</body>
</html>
