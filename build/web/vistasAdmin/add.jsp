<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Usuario"%>
<%@page import="Modelo.Catalogo"%>

<%
    Usuario usr = (Usuario) session.getAttribute("usuario");
    if (usr == null) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Agregar Usuario</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/add.css" rel="stylesheet">
</head>
<body class="add-page">

<div class="container mt-5">
    <div class="card shadow-lg border-0 rounded-4">
        <div class="card-body p-4">
            <h2 class="mb-3 text-center">Crear Usuario</h2>
            <hr>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger text-center">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <form action="ControladorAdmin" method="get" autocomplete="off">
                <input type="hidden" name="accion" value="Agregar">

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label">DPI</label>
                        <input type="text" name="txtDpi" id="dpi" class="form-control"
                               maxlength="13" pattern="[0-9]{13}" title="Debe contener exactamente 13 números" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Nombre</label>
                        <input type="text" name="txtNombre" id="nombre" class="form-control"
                               pattern="[A-Za-zÁÉÍÓÚáéíóúñÑ ]+" title="Solo letras" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Apellido</label>
                        <input type="text" name="txtApellido" id="apellido" class="form-control"
                               pattern="[A-Za-zÁÉÍÓÚáéíóúñÑ ]+" title="Solo letras" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Usuario</label>
                        <input type="text" name="txtUsuario" class="form-control"
                               pattern="[A-Za-z0-9!@#\\$%\\^&\\*\\-_\\.]+"
                               title="Solo letras, números y signos permitidos" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Correo electrónico</label>
                        <input type="email" name="txtCorreo" class="form-control" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Contraseña</label>
                        <input type="text" name="txtContrasena" class="form-control"
                               pattern="[A-Za-z0-9!@#\\$%\\^&\\*\\-_\\.]+"
                               title="Solo letras, números y signos permitidos" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Rol</label>
                        <select name="txtRol" id="rolSelect" class="form-select" required>
                            <option value="">Seleccionar rol</option>
                            <%
                                List<Catalogo> roles = (List<Catalogo>) request.getAttribute("roles");
                                if (roles != null) {
                                    for (Catalogo r : roles) {
                            %>
                                <option value="<%= r.getNombre() %>"><%= r.getNombre() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Lote</label>
                        <select name="txtLote" id="loteSelect" class="form-select" required>
                            <option value="">Seleccionar lote</option>
                            <%
                                List<Catalogo> lotes = (List<Catalogo>) request.getAttribute("lotes");
                                if (lotes != null) {
                                    for (Catalogo l : lotes) {
                            %>
                                <option value="<%= l.getNombre() %>"><%= l.getNombre() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Número de casa</label>
                        <select name="txtNumeroCasa" id="casaSelect" class="form-select" required>
                            <option value="">Seleccionar número de casa</option>
                            <%
                                List<Catalogo> casas = (List<Catalogo>) request.getAttribute("casas");
                                if (casas != null) {
                                    for (Catalogo c : casas) {
                            %>
                                <option value="<%= c.getNombre() %>"><%= c.getNombre() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <input type="hidden" name="txtEstado" value="activo">
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4 flex-wrap">
                    <a href="ControladorAdmin?accion=listar"
                       onclick="return confirm('¿Está seguro de cancelar? Se perderán los datos.');"
                       class="btn btn-danger">Cancelar</a>
                    <button type="submit" class="btn btn-success">Guardar Usuario</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    const rolSelect = document.getElementById("rolSelect");
    const loteSelect = document.getElementById("loteSelect");
    const casaSelect = document.getElementById("casaSelect");

    function toggleCampos() {
        const rol = rolSelect.value.toLowerCase();
        const deshabilitar = (rol === "seguridad" || rol === "administrador");
        loteSelect.disabled = deshabilitar;
        casaSelect.disabled = deshabilitar;
        if (deshabilitar) {
            loteSelect.value = "";
            casaSelect.value = "";
        }
    }

    toggleCampos();
    rolSelect.addEventListener("change", toggleCampos);

    // Validaciones en tiempo real
    document.getElementById("dpi").addEventListener("input", function() {
        this.value = this.value.replace(/[^0-9]/g, '').slice(0, 13);
    });

    document.getElementById("nombre").addEventListener("input", function() {
        this.value = this.value.replace(/[^A-Za-zÁÉÍÓÚáéíóúñÑ ]/g, '');
    });

    document.getElementById("apellido").addEventListener("input", function() {
        this.value = this.value.replace(/[^A-Za-zÁÉÍÓÚáéíóúñÑ ]/g, '');
    });
</script>

</body>
</html>
