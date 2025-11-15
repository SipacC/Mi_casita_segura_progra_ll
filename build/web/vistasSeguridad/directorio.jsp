<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Usuario"%>

<%
    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"seguridad".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    List<Usuario> listaUsuarios = (List<Usuario>) request.getAttribute("listaUsuarios");
    String mensaje = (String) request.getAttribute("mensaje");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Directorio General - Seguridad</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-dark text-white">

<div class="container mt-4">
    <div class="card shadow-lg border-0 rounded-4 bg-secondary text-white">
        <div class="card-body p-4">
            <h2 class="text-center mb-4">
                <i class="fas fa-address-book"></i> Directorio General
            </h2>

            <!-- Botones superiores -->
            <div class="d-flex justify-content-start gap-2 mb-3">
                <a href="<%= request.getContextPath() %>/ControladorSeguridad?accion=menu"
                   class="btn btn-outline-light">
                    <i class="fas fa-arrow-left"></i> Regresar
                </a>
                <button id="btnLimpiar" class="btn btn-outline-warning">
                    <i class="fas fa-eraser"></i> Limpiar
                </button>
            </div>

            <!-- Campo de búsqueda -->
            <div class="mb-3">
                <input type="text" id="buscador" class="form-control bg-dark text-white border-light"
                       placeholder="Buscar por nombre, apellido, lote o número de casa...">
            </div>

            <!-- Tabla de resultados -->
            <div class="table-responsive" id="tabla-contenedor">
                <table class="table table-dark table-striped align-middle text-center">
                    <thead class="table-light text-dark">
                        <tr>
                            <th>Nombre Completo</th>
                            <th>Número de Casa</th>
                            <th>Correo Electrónico</th>
                        </tr>
                    </thead>
                    <tbody id="tabla-directorio">
                    <%
                        if (mensaje != null) {
                    %>
                        <tr>
                            <td colspan="3" class="text-center text-warning"><%= mensaje %></td>
                        </tr>
                    <%
                        } else if (listaUsuarios == null || listaUsuarios.isEmpty()) {
                    %>
                        <tr>
                            <td colspan="3" class="text-center text-muted">
                                Busca por nombre, apellido, lote o número de casa.
                            </td>
                        </tr>
                    <%
                        } else {
                            for (Usuario u : listaUsuarios) {
                    %>
                        <tr>
                            <td><%= u.getNombre() %> <%= u.getApellido() %></td>
                            <td><%= (u.getNumeroCasa() != null ? u.getNumeroCasa() : "-") %></td>
                            <td><%= (u.getCorreo() != null ? u.getCorreo() : "-") %></td>
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

<!-- Bootstrap -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Búsqueda dinámica AJAX -->
<script type="text/javascript">
let timer;
const buscador = document.getElementById("buscador");
const tabla = document.querySelector("#tabla-directorio");
const btnLimpiar = document.getElementById("btnLimpiar");

buscador.addEventListener("keyup", function() {
    clearTimeout(timer);
    const texto = this.value.trim();

    timer = setTimeout(() => {
        fetch("ControladorSeguridad?accion=directorio&texto=" + encodeURIComponent(texto))
            .then(res => res.text())
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');
                const nuevasFilas = doc.querySelector("#tabla-directorio").innerHTML;
                tabla.innerHTML = nuevasFilas;
            })
            .catch(err => console.error("Error en búsqueda:", err));
    }, 400);
});

buscador.addEventListener("input", function() {
    if (this.value.trim() === "") {
        tabla.innerHTML = `
            <tr>
                <td colspan="3" class="text-center text-muted">
                    Busca por nombre, apellido, lote o número de casa.
                </td>
            </tr>`;
    }
});

// 🔹 Botón limpiar
btnLimpiar.addEventListener("click", function() {
    buscador.value = "";
    tabla.innerHTML = `
        <tr>
            <td colspan="3" class="text-center text-muted">
                Busca por nombre, apellido, lote o número de casa.
            </td>
        </tr>`;
});
</script>

</body>
</html>
