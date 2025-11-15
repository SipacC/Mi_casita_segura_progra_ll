<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="Modelo.Tarjeta" %>
<%
    Tarjeta t = (Tarjeta) request.getAttribute("tarjeta");
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
    <title>Editar Tarjeta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2>✏️ Editar Tarjeta</h2>

    <form action="<%= request.getContextPath() %>/ControladorResidente?accion=actualizarTarjeta" method="post">
        <input type="hidden" name="id_tarjeta" value="<%= t.getIdTarjeta() %>">

        <div class="mb-3">
            <label class="form-label">Alias de la tarjeta</label>
            <input type="text" name="nombre_tarjeta" placeholder="Dejar en blanco para mantener el actual" class="form-control">
        </div>
        <div class="mb-3">
            <label class="form-label">Número de tarjeta</label>
            <input type="text" name="numero_tarjeta" maxlength="20" placeholder="Dejar en blanco para mantener el actual" class="form-control">
        </div>
        <div class="mb-3">
            <label class="form-label">Fecha de vencimiento</label>
            <input type="date" name="fecha_vencimiento" class="form-control">
        </div>
        <div class="mb-3">
            <label class="form-label">CVV</label>
            <input type="text" name="cvv" maxlength="4" placeholder="Dejar en blanco para mantener el actual" class="form-control">
        </div>
        <div class="mb-3">
            <label class="form-label">Nombre del titular</label>
            <input type="text" name="nombre_titular" placeholder="Dejar en blanco para mantener el actual" class="form-control">
        </div>
        <div class="mb-3">
            <label class="form-label">Tipo de tarjeta</label>
            <select name="tipo_tarjeta" class="form-select">
                <option value="">Dejar en blanco para mantener actual</option>
                <option value="Crédito">Crédito</option>
                <option value="Débito">Débito</option>
            </select>
        </div>
        <div class="mb-3">
            <label class="form-label">Saldo (Q)</label>
            <input type="number" step="0.01" name="saldo" placeholder="Dejar en blanco para mantener el actual" class="form-control">
        </div>

        <button type="submit" class="btn btn-primary">Actualizar</button>
        <a href="<%= request.getContextPath() %>/ControladorResidente?accion=tarjetas" class="btn btn-secondary">Cancelar</a>
    </form>
</div>
</body>
</html>
