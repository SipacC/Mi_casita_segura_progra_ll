<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
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
    <title>Registrar Tarjeta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2>➕ Registrar Nueva Tarjeta</h2>

    <form action="<%= request.getContextPath() %>/ControladorResidente?accion=guardarTarjeta" method="post">
        <div class="mb-3">
            <label class="form-label">Alias de la tarjeta</label>
            <input type="text" name="nombre_tarjeta" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Número de tarjeta</label>
            <input type="text" name="numero_tarjeta" maxlength="20" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Fecha de vencimiento</label>
            <input type="date" name="fecha_vencimiento" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">CVV</label>
            <input type="text" name="cvv" maxlength="4" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Nombre del titular</label>
            <input type="text" name="nombre_titular" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Tipo de tarjeta</label>
            <select name="tipo_tarjeta" class="form-select" required>
                <option value="Crédito">Crédito</option>
                <option value="Débito">Débito</option>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">Saldo inicial (Q)</label>
            <input type="number" step="0.01" name="saldo" class="form-control" required>
        </div>
        <button type="submit" class="btn btn-success">Guardar</button>
        <a href="<%= request.getContextPath() %>/ControladorResidente?accion=tarjetas" class="btn btn-secondary">Cancelar</a>
    </form>
</div>
</body>
</html>
