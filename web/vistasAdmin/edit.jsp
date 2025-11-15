<%@page import="Modelo.Usuario"%>
<%@page import="ModeloDAO.UsuarioDAO"%>
<%@page import="Modelo.Catalogo"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    Usuario usr = (Usuario) session.getAttribute("usuario");
    if (usr == null) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    UsuarioDAO dao = new UsuarioDAO();
    Object idObj = request.getAttribute("idper");
    String idStr = (idObj != null) ? idObj.toString() : request.getParameter("id");
    int id = Integer.parseInt(idStr);
    Usuario u = dao.list(id);

    List<Catalogo> roles = (List<Catalogo>) request.getAttribute("roles");
    List<Catalogo> lotes = (List<Catalogo>) request.getAttribute("lotes");
    List<Catalogo> casas = (List<Catalogo>) request.getAttribute("casas");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Usuario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/add.css">
</head>
<body class="edit-page bg-light">

<div class="container d-flex justify-content-center align-items-center min-vh-100">
    <div class="card shadow-lg border-0 rounded-4" style="max-width: 600px; width: 100%;">
        <div class="card-body p-4">
            <h2 class="text-center text-primary fw-bold mb-4">Modificar Usuario</h2>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger text-center">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <form action="ControladorAdmin" method="get" autocomplete="off">
                <input type="hidden" name="accion" value="Actualizar">
                <input type="hidden" name="txtid" value="<%= u.getIdUsuario() %>">

                <div class="alert alert-info mb-4">
                    Deja en blanco cualquier campo que no desees modificar.
                </div>

                <!-- DPI -->
                <div class="mb-3">
                    <label for="txtDpi" class="form-label">DPI actual: <strong><%= u.getDpi() %></strong></label>
                    <input type="text" id="txtDpi" name="txtDpi" class="form-control"
                           placeholder="Nuevo DPI (opcional)"
                           maxlength="13"
                           onkeypress="return event.charCode >= 48 && event.charCode <= 57"
                           oninput="if(this.value.length > 13) this.value = this.value.slice(0,13);">
                </div>

                <div class="row">
                    <!-- Nombre -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Nombre actual: <strong><%= u.getNombre() %></strong></label>
                        <input type="text" name="txtNombre" class="form-control"
                               placeholder="Nuevo nombre (opcional)"
                               pattern="[A-Za-zÁÉÍÓÚáéíóúÑñ ]+"
                               title="Solo letras y espacios permitidos">
                    </div>

                    <!-- Apellido -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Apellido actual: <strong><%= u.getApellido() %></strong></label>
                        <input type="text" name="txtApellido" class="form-control"
                               placeholder="Nuevo apellido (opcional)"
                               pattern="[A-Za-zÁÉÍÓÚáéíóúÑñ ]+"
                               title="Solo letras y espacios permitidos">
                    </div>
                </div>

                <div class="row">
                    <!-- Usuario -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Usuario actual: <strong><%= u.getUsuario() %></strong></label>
                        <input type="text" name="txtUsuario" class="form-control"
                               placeholder="Nuevo usuario (opcional)"
                               pattern="^[A-Za-z0-9_]{4,20}$"
                               title="Solo letras, números o guiones bajos (mínimo 4 caracteres)">
                    </div>

                    <!-- Contraseña -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Contraseña actual: <strong><%= u.getContrasena() %></strong></label>
                        <input type="text" name="txtContrasena" class="form-control"
                               placeholder="Nueva contraseña (opcional)"
                               minlength="3" title="Debe tener al menos 6 caracteres">
                    </div>
                </div>

                <div class="row">
                    <!-- Correo -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Correo actual: <strong><%= u.getCorreo() %></strong></label>
                        <input type="email" name="txtCorreo" class="form-control"
                               placeholder="Nuevo correo (opcional)">
                    </div>

                    <!-- Rol -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Rol actual: <strong><%= u.getRol() %></strong></label>
                        <select name="txtRol" id="rolSelect" class="form-select">
                            <option value="">Mantener rol actual</option>
                            <% if (roles != null) {
                                   for (Catalogo r : roles) { %>
                                <option value="<%= r.getNombre() %>"><%= r.getNombre() %></option>
                            <%   }
                               } %>
                        </select>
                    </div>
                </div>

                <div class="row">
                    <!-- Lote -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">Lote actual: <strong><%= u.getLote() %></strong></label>
                        <select name="txtLote" id="loteSelect" class="form-select">
                            <option value="">Mantener lote actual</option>
                            <% if (lotes != null) {
                                   for (Catalogo l : lotes) { %>
                                <option value="<%= l.getNombre() %>"><%= l.getNombre() %></option>
                            <%   }
                               } %>
                        </select>
                    </div>

                    <!-- Número de casa -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">No. Casa actual: <strong><%= u.getNumeroCasa() %></strong></label>
                        <select name="txtNumeroCasa" id="casaSelect" class="form-select">
                            <option value="">Mantener número actual</option>
                            <% if (casas != null) {
                                   for (Catalogo c : casas) { %>
                                <option value="<%= c.getNombre() %>"><%= c.getNombre() %></option>
                            <%   }
                               } %>
                        </select>
                    </div>
                </div>

                <div class="d-flex justify-content-between mt-4">
                    <a href="ControladorAdmin?accion=listar"
                       onclick="return confirm('¿Está seguro de cancelar la edición?');"
                       class="btn btn-danger px-4">Cancelar</a>
                    <button type="submit" class="btn btn-success px-4">Actualizar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Deshabilitar lote y número de casa según rol
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
</script>

</body>
</html>
