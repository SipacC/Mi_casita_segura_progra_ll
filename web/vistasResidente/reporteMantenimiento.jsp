<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>

<%
    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    List<?> listaTipos = (List<?>) request.getAttribute("listaTipos");
    Integer idSeleccionado = (Integer) request.getAttribute("idSeleccionado");
    String mensaje = (String) request.getAttribute("mensaje");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reporte de Mantenimiento</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>

<body class="bg-light">
<div class="container mt-5">
    <div class="card shadow border-0 rounded-4">
        <div class="card-body">
            <h4 class="text-center text-primary mb-4">
                <i class="fas fa-tools"></i> Reporte de Mantenimiento
            </h4>

            <% if (mensaje != null) { %>
                <div id="mensajeAlerta" class="alert alert-info text-center"><%= mensaje %></div>
            <% } %>

            <form action="<%= request.getContextPath() %>/ControladorResidente" method="get">
                <input type="hidden" name="accion" value="guardarReporteMantenimiento">

                <div class="mb-3">
                    <label class="form-label fw-bold">Tipo de Incidente</label>
                    <select class="form-select" name="id_tipo_inconveniente" required>
                        <option value="">Seleccione un tipo...</option>
                        <% 
                            if (listaTipos != null && !listaTipos.isEmpty()) {
                                for (Object obj : listaTipos) {
                                    int id = 0;
                                    String nombre = "";

                                    try {
                                        // Detecta dinámicamente si el objeto es TipoInconveniente o CatalogoIncidente
                                        Class<?> clazz = obj.getClass();
                                        id = (int) clazz.getMethod("getIdTipoInconveniente").invoke(obj);
                                        nombre = (String) clazz.getMethod("getNombre").invoke(obj);
                                    } catch (NoSuchMethodException e) {
                                        try {
                                            id = (int) obj.getClass().getMethod("getIdTipoIncidente").invoke(obj);
                                            nombre = (String) obj.getClass().getMethod("getNombre").invoke(obj);
                                        } catch (Exception ex) {
                                            continue;
                                        }
                                    }

                                    String selected = (idSeleccionado != null && idSeleccionado == id)
                                            ? "selected" : "";
                        %>
                            <option value="<%= id %>" <%= selected %>><%= nombre %></option>
                        <% 
                                }
                            } else {
                        %>
                            <option disabled>No hay tipos disponibles</option>
                        <% } %>
                    </select>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Descripción</label>
                    <textarea class="form-control" name="descripcion" rows="3" required></textarea>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Fecha del incidente</label>
                    <input type="datetime-local" class="form-control" name="fecha_incidente" required>
                </div>

                <div class="text-center">
                    <button type="submit" class="btn btn-success">
                        <i class="fas fa-save"></i> Guardar Reporte
                    </button>
                    <a href="<%= request.getContextPath() %>/ControladorResidente?accion=menu"
                       class="btn btn-outline-primary ms-2">
                        <i class="fas fa-arrow-left"></i> Regresar
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
document.addEventListener("DOMContentLoaded", () => {
    const alerta = document.getElementById("mensajeAlerta");
    if (alerta) {
        setTimeout(() => {
            alerta.style.transition = "opacity 0.5s ease";
            alerta.style.opacity = "0";
            setTimeout(() => alerta.remove(), 500);
        }, 5000);
    }
});
</script>
</body>
</html>
