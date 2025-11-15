<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.time.LocalDate"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registrar Visita</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
        function toggleCampos() {
            let tipo = document.getElementById("tipo").value;
            document.getElementById("campoFecha").style.display = (tipo === "Visita") ? "block" : "none";
            document.getElementById("campoIntentos").style.display = (tipo === "Por intentos") ? "block" : "none";
        }

        // Evitar doble submit
        function bloquearBoton(form) {
            const btn = document.getElementById("btnRegistrar");
            btn.disabled = true;
            btn.innerHTML = "⏳ Registrando...";
            return true;
        }
    </script>
</head>
<body>
<div class="container mt-4">
    <h2 class="mb-3">➕ Registrar Nueva Visita</h2>

    <form action="<%=request.getContextPath()%>/ControladorResidente?accion=guardarVisita"
          method="post" class="card p-4 shadow-lg"
          onsubmit="return bloquearBoton(this)">

        <div class="mb-3">
            <label class="form-label">Nombre del Visitante *</label>
            <input type="text" name="nombre" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">DPI del Visitante *</label>
            <input type="text" name="dpi" class="form-control" maxlength="13" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Tipo de Visita *</label>
            <select name="tipo" id="tipo" class="form-select" onchange="toggleCampos()" required>
                <option value="">-- Seleccione --</option>
                <option value="Visita">Visita</option>
                <option value="Por intentos">Por intentos</option>
            </select>
        </div>

        <div class="mb-3" id="campoFecha" style="display:none;">
            <label class="form-label">Fecha de Visita *</label>
            <input type="date" name="fechaValidaHasta" class="form-control"
                   min="<%= LocalDate.now() %>">
        </div>

        <div class="mb-3" id="campoIntentos" style="display:none;">
            <label class="form-label">Número de Intentos *</label>
            <input type="number" name="intentos" min="2" class="form-control">
        </div>

        <div class="mb-3">
            <label class="form-label">Correo del Visitante *</label>
            <input type="email" name="correo" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Motivo de la Visita *</label>
            <textarea name="motivo" class="form-control" rows="3" required></textarea>
        </div>

        <div class="d-flex justify-content-between">
            <a href="<%=request.getContextPath()%>/ControladorResidente?accion=visitas" class="btn btn-secondary">↩ Volver</a>
            <button type="submit" id="btnRegistrar" class="btn btn-success">✅ Registrar Visita</button>
        </div>
    </form>
</div>
</body>
</html>
