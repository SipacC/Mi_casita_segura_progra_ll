<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Paqueteria"%>

<%
    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"seguridad".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    List<Paqueteria> lista = (List<Paqueteria>) request.getAttribute("listaPaqueteria");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Paquetería</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/paqueteria.css">
</head>
<body class="paqueteria-page">

<div class="container mt-4">
    <div class="card shadow-lg border-0 rounded-4">
        <div class="card-body p-4">
            <h2 class="mb-4 text-center">Lista de paquetes</h2>
            <div class="d-flex justify-content-start gap-2 mb-3">
                <a href="<%= request.getContextPath() %>/ControladorSeguridad?accion=menu"
                   class="btn btn-secondary">
                    <i class="fas"></i> Regresar
                </a>
                <a href="<%= request.getContextPath() %>/ControladorSeguridad?accion=nuevoPaquete"
                   class="btn btn-primary">
                    <i class="fas"></i> Registrar paquetería
                </a>
            </div>
            <div class="mb-3">
                <input type="text" id="buscador" class="form-control"
                       placeholder="Buscar por número de guía, residente o casa...">
            </div>
            <div class="table-responsive" id="tabla-contenedor">
                <table class="table table-bordered table-hover align-middle text-center">
                    <thead class="table-dark">
                    <tr>
                        <th>Número de Guía</th>
                        <th>Residente</th>
                        <th>Lote</th>
                        <th>No. Casa</th>
                        <th>Agente Registra</th>
                        <th>Agente Entrega</th>
                        <th>Estado</th>
                        <th>Fecha de Entrega</th>
                        <th>Observaciones</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody id="tabla-paqueteria">
                    <%
                        if (lista == null || lista.isEmpty()) {
                    %>
                    <tr>
                        <td colspan="10" class="text-center text-danger">
                            No hay paquetería pendiente de entregar.
                        </td>
                    </tr>
                    <%
                        } else {
                            for (Paqueteria p : lista) {
                    %>
                    <tr>
                        <td><%= (p.getNumero_guia() != null ? p.getNumero_guia() : "-") %></td>
                        <td><%= (p.getResidente() != null ? p.getResidente() : "-") %></td>
                        <td><%= (p.getLote_residente() != null ? p.getLote_residente() : "-") %></td>
                        <td><%= (p.getCasa_residente() != null ? p.getCasa_residente() : "-") %></td>
                        <td><%= (p.getAgente_registra() != null ? p.getAgente_registra() : "-") %></td>
                        <td><%= (p.getAgente_entrega() != null ? p.getAgente_entrega() : "-") %></td>
                        <td>
                            <% if ("pendiente".equalsIgnoreCase(p.getEstado())) { %>
                                <span class="badge bg-warning text-dark">Pendiente</span>
                            <% } else { %>
                                <span class="badge bg-success">Entregado</span>
                            <% } %>
                        </td>
                        <td>
                            <%= (p.getFecha_entrega() != null ? p.getFecha_entrega() : "-") %>
                        </td>
                        <td><%= (p.getObservaciones() != null ? p.getObservaciones() : "-") %></td>
                        <td>
                            <% if ("pendiente".equalsIgnoreCase(p.getEstado())) { %>
                                <a class="btn btn-success btn-sm"
                                   href="<%= request.getContextPath() %>/ControladorSeguridad?accion=entregarPaquete&id=<%= p.getId_paqueteria() %>"
                                   onclick="return confirm('¿Está seguro de realizar la entrega de paquetes?');">
                                    <i class="fas fa-box-open"></i> Entregar
                                </a>
                            <% } else { %>
                                <button class="btn btn-outline-secondary btn-sm" disabled>
                                    <i class="fas fa-check"></i> Entregado
                                </button>
                            <% } %>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript">
var timer;
document.getElementById("buscador").addEventListener("keyup", function() {
    clearTimeout(timer);
    var texto = this.value.trim();

    timer = setTimeout(function() {
        fetch("ControladorSeguridad?accion=buscarPaquete&texto=" + encodeURIComponent(texto))
            .then(function(res) { return res.text(); })
            .then(function(html) {
                var parser = new DOMParser();
                var doc = parser.parseFromString(html, 'text/html');
                var nuevasFilas = doc.querySelector("#tabla-paqueteria").innerHTML;
                document.querySelector("#tabla-paqueteria").innerHTML = nuevasFilas;
            })
            .catch(function(err) {
                console.error('Error en búsqueda:', err);
            });
    }, 400);
});

document.getElementById("buscador").addEventListener("input", function() {
    if (this.value.trim() === "") {
        fetch("ControladorSeguridad?accion=paqueteria")
            .then(function(res) { return res.text(); })
            .then(function(html) {
                var parser = new DOMParser();
                var doc = parser.parseFromString(html, 'text/html');
                var nuevasFilas = doc.querySelector("#tabla-paqueteria").innerHTML;
                document.querySelector("#tabla-paqueteria").innerHTML = nuevasFilas;
            })
            .catch(function(err) {
                console.error('Error al recargar lista:', err);
            });
    }
});
</script>

</body>
</html>
