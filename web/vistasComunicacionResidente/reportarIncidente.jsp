<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.CatalogoIncidente"%>
<%@page import="Modelo.Usuario"%>

<%
    // Validar sesión
    Usuario usr = (Usuario) session.getAttribute("usuario");
    if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    // Obtener lista de tipos de incidente enviada desde el controlador
    List<CatalogoIncidente> tipos = (List<CatalogoIncidente>) request.getAttribute("tiposIncidentes");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reportar Incidente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <script>
        // RN7: habilitar botón Guardar solo cuando todos los campos están completos
        function validarCampos() {
            const tipo = document.getElementById("tipoIncidente").value.trim();
            const fecha = document.getElementById("fechaHora").value.trim();
            const desc = document.getElementById("descripcion").value.trim();
            document.getElementById("btnGuardar").disabled = !(tipo && fecha && desc);
        }
    </script>
</head>

<body class="bg-light">
<div class="container mt-4">
    <h3 class="text-primary mb-4">
        <i class="fas fa-bullhorn"></i> Reportar Incidente
    </h3>

    <form action="ControladorComunicacionResidente" method="post" class="card shadow-sm border-0 p-4 bg-white">
        <input type="hidden" name="accion" value="guardarIncidente">

        <!-- Tipo de incidente -->
        <div class="mb-3">
            <label for="tipoIncidente" class="form-label fw-bold">Tipo de incidente</label>
            <select class="form-select" id="tipoIncidente" name="idTipoIncidente" onchange="validarCampos()" required>
                <option value="">-- Seleccione --</option>
                <% if (tipos != null) {
                       for (CatalogoIncidente t : tipos) { %>
                    <option value="<%= t.getIdTipoIncidente() %>"><%= t.getNombre() %></option>
                <% } } %>
            </select>
        </div>

        <!-- Fecha y hora -->
        <div class="mb-3">
            <label for="fechaHora" class="form-label fw-bold">Fecha y hora del incidente</label>
            <input type="datetime-local" class="form-control" id="fechaHora" name="fechaHora"
                   onchange="validarCampos()" required>
        </div>

        <!-- Descripción -->
        <div class="mb-3">
            <label for="descripcion" class="form-label fw-bold">Descripción</label>
            <textarea id="descripcion" name="descripcion" class="form-control" rows="4" maxlength="200"
                      placeholder="Describa brevemente lo ocurrido..." onkeyup="validarCampos()" required></textarea>
            <div class="form-text text-muted">nn</div>
        </div>

        <!-- Botones -->
        <div class="text-center mt-4">
            <button type="submit" id="btnGuardar" class="btn btn-success px-4" disabled>
                <i class="fas fa-save"></i> Guardar
            </button>
            <a href="ControladorComunicacionResidente?accion=menu" class="btn btn-secondary px-4 ms-2">
                <i class="fas fa-arrow-left"></i> Regresar
            </a>
        </div>
    </form>
</div>
</body>
</html>
