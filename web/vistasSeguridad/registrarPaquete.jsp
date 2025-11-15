<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Usuario"%>

<%
    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"seguridad".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    List<Usuario> residentes = (List<Usuario>) request.getAttribute("listaResidentes");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registrar paquetería</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/paqueteria.css">
</head>
<body class="paqueteria-page">

<div class="container mt-4">
    <div class="card shadow-lg border-0 rounded-4">
        <div class="card-body p-4">
            <h3 class="mb-4 text-center text-primary">Registrar paquetería</h3>

            <form action="<%= request.getContextPath() %>/ControladorSeguridad?accion=guardarPaquete" method="post">

                <!-- Número de Guía -->
                <div class="mb-3">
                    <label class="form-label">Número de Guía</label>
                    <input type="text" name="numero_guia" class="form-control" placeholder="Ejemplo: GT-20251011-001" required>
                </div>

                <!-- Seleccionar Residente -->
                <div class="mb-3">
                    <label class="form-label">Residente</label>
                    <select id="residenteSelect" name="id_residente" class="form-select" required>
                        <option value="">Seleccione un residente...</option>
                        <% if (residentes != null) {
                            for (Usuario r : residentes) { %>
                                <option value="<%= r.getIdUsuario() %>"
                                        data-casa="<%= r.getNumeroCasa() %>">
                                    <%= r.getNombre() %> <%= r.getApellido() %>
                                </option>
                        <% }} %>
                    </select>
                </div>

                <!-- Número de Casa -->
                <div class="mb-3">
                    <label class="form-label">Número de Casa</label>
                    <input type="text" id="casaInput" name="casa_residente" class="form-control" readonly>
                </div>
                <!-- Observaciones -->
                <div class="mb-3">
                    <label class="form-label">Observaciones</label>
                    <textarea name="observaciones" class="form-control" rows="3" placeholder="Escriba observaciones (opcional)"></textarea>
                </div>

                <!-- Botones -->
                <div class="d-flex justify-content-start gap-2">
                    <a href="<%= request.getContextPath() %>/ControladorSeguridad?accion=paqueteria" 
                       class="btn btn-secondary">
                        <i class="fas"></i>Regresar
                    </a>
                    <button type="reset" class="btn btn-warning">
                        <i class="fas"></i>Limpiar
                    </button>

                    <button type="submit" class="btn btn-success">
                        <i class="fas"></i>Guardar Paquete
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
document.getElementById('residenteSelect').addEventListener('change', function() {
    var selected = this.options[this.selectedIndex];
    document.getElementById('casaInput').value = selected.getAttribute('data-casa') || '';
});
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
