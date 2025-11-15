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
    <title>Nueva Reserva</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
        // Script para actualizar el campo oculto con el nombre del área
        function actualizarNombreArea(select) {
            let nombre = select.options[select.selectedIndex].text;
            document.getElementById("nombre_area").value = nombre;
        }
    </script>
</head>
<body>
<div class="container mt-4">
    <h2 class="text-center">➕ Crear Nueva Reserva</h2>
    <p class="text-center">Residente: 
        <strong><%= usr.getNombre() %> <%= usr.getApellido() %></strong>
    </p>

    <!-- Botón volver -->
    <a href="ControladorResidente?accion=reservas" class="btn btn-secondary mb-3">⬅ Volver</a>

    <!-- Formulario -->
    <form action="ControladorResidente" method="post" class="card p-4 shadow">
        <input type="hidden" name="accion" value="guardarReserva">
        <!-- Campo oculto para el nombre del área -->
        <input type="hidden" name="nombre_area" id="nombre_area">

        <!-- Área común -->
        <div class="mb-3">
            <label class="form-label">Área común:</label>
            <select name="id_area" class="form-select" required onchange="actualizarNombreArea(this)">
                <option value="" disabled selected>-- Selecciona un área --</option>
                <option value="1">Salón de eventos</option>
                <option value="2">Piscina</option>
                <option value="3">Cancha de fútbol</option>
                <option value="4">Gimnasio</option>
                <option value="5">Cancha de baloncesto</option>
                <option value="6">Área de juegos infantiles</option>
                <option value="7">Área de BBQ</option>
                <option value="8">Terraza</option>
                <option value="9">Sala de cine</option>
                <option value="10">Sala de juntas</option>
                <option value="11">Parqueo de visitas</option>
                <option value="12">Área de coworking</option>
                <option value="13">Pista de jogging</option>
                <option value="14">Cancha de tenis</option>
                <option value="15">Roof Garden</option>
            </select>
        </div>

        <!-- Fecha -->
        <div class="mb-3">
            <label class="form-label">Fecha:</label>
            <input type="date" name="fecha" class="form-control" required>
        </div>

        <!-- Hora inicio -->
        <div class="mb-3">
            <label class="form-label">Hora inicio:</label>
            <input type="time" name="hora_inicio" class="form-control" required>
        </div>

        <!-- Hora fin -->
        <div class="mb-3">
            <label class="form-label">Hora fin:</label>
            <input type="time" name="hora_fin" class="form-control" required>
        </div>

        <!-- Botón -->
        <button type="submit" class="btn btn-primary">✅ Guardar reserva</button>
    </form>
</div>
</body>
</html>
