<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Usuario"%>

<%
    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
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
    <title>Directorio Residencial</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

    <style>
        body { background-color: #f8f9fa; }
        .card { border-radius: 1rem; }
        input.form-control { border-radius: 0.5rem; }
        table tr:hover { background-color: #f3f3f3; }
    </style>
</head>
<body>

<div class="container mt-4">
    <div class="card shadow-lg border-0 rounded-4">
        <div class="card-body p-4">
            <h2 class="text-center mb-4"><i class="fas fa-address-book"></i> Directorio Residencial</h2>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <a href="<%= request.getContextPath() %>/ControladorResidente?accion=menu" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Regresar
                </a>

                <!-- 🔘 Botón Limpiar -->
                <button id="btnLimpiar" class="btn btn-outline-danger">
                    <i class="fas fa-eraser"></i> Limpiar
                </button>
            </div>

            <!-- 🔍 Campo de búsqueda -->
            <div class="mb-3">
                <input type="text" id="buscador" class="form-control"
                       placeholder="Buscar por nombre, apellido, lote o número de casa...">
            </div>

            <!-- 📋 Tabla de resultados -->
            <div class="table-responsive" id="tabla-contenedor">
                <table class="table table-hover align-middle text-center">
                    <thead class="table-dark">
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
                            <td colspan="3" class="text-center text-muted"><%= mensaje %></td>
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
                            <td><%= u.getNumeroCasa() != null ? u.getNumeroCasa() : "-" %></td>
                            <td><%= u.getCorreo() != null ? u.getCorreo() : "-" %></td>
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

<!-- ⚙️ AJAX búsqueda dinámica -->
<script type="text/javascript">
let timer;
const buscador = document.getElementById("buscador");
const tabla = document.querySelector("#tabla-directorio");
const btnLimpiar = document.getElementById("btnLimpiar");

// 🔍 Búsqueda automática
buscador.addEventListener("keyup", function() {
    clearTimeout(timer);
    const texto = this.value.trim();

    timer = setTimeout(() => {
        fetch("ControladorResidente?accion=directorio&texto=" + encodeURIComponent(texto))
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

// 🧹 Limpiar campo y tabla
btnLimpiar.addEventListener("click", function() {
    buscador.value = "";
    tabla.innerHTML = `
        <tr>
            <td colspan="3" class="text-center text-muted">
                Busca por nombre, apellido, lote o número de casa.
            </td>
        </tr>`;
    buscador.focus();
});
</script>

</body>
</html>
