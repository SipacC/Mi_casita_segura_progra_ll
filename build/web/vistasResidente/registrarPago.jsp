<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.MetodoPago"%>
<%@page import="Modelo.Tarjeta"%>
<%
    String tipoPago = (String) request.getAttribute("tipoPago");
    Double monto = (Double) request.getAttribute("monto");
    Double mora = (Double) request.getAttribute("mora");
    Integer mes = (Integer) request.getAttribute("mes");
    Integer anio = (Integer) request.getAttribute("anio");
    String nombreUsuario = (String) request.getAttribute("nombreUsuario");
    String apellidoUsuario = (String) request.getAttribute("apellidoUsuario");
    Integer idTipo = (Integer) request.getAttribute("idTipo");

    // 👇 Si es multa, debe venir el id_pago
    Integer idPagoMulta = (Integer) request.getAttribute("idPagoMulta");

    List<MetodoPago> listaMetodos = (List<MetodoPago>) request.getAttribute("listaMetodos");

    // 👇 Lista de tarjetas registradas (si existen)
    List<Tarjeta> listaTarjetas = (List<Tarjeta>) request.getAttribute("listaTarjetas");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registrar Pago</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/registrarPago.css">
    <script>
        function toggleCamposTarjeta() {
            var metodo = document.getElementById("metodo_pago").value;
            var seccionTarjeta = document.getElementById("seccionTarjeta");
            if (metodo !== "" && document.getElementById("metodo_pago")
                .options[document.getElementById("metodo_pago").selectedIndex].text.toLowerCase().includes("tarjeta")) {
                seccionTarjeta.style.display = "block";
            } else {
                seccionTarjeta.style.display = "none";
            }
        }
    </script>
</head>
<body class="registrar-pago-page">
    <div class="container">
        <h2>Registrar Pago</h2>

        <%-- 🔹 Mostrar mensaje de error si existe --%>
        <%
            String error = (String) request.getAttribute("error");
            if (error != null && !error.isEmpty()) {
        %>
            <div class="alert alert-danger mt-3" role="alert">
                <%= error %>
            </div>
        <%
            }
        %>

        <div class="card p-3 mb-3">
            <h5>Datos del Pago a registrar</h5>
            <p><strong>Usuario:</strong> <%= nombreUsuario %> <%= apellidoUsuario %></p>
            <p><strong>Tipo de pago:</strong> <%= tipoPago %></p>
            <p><strong>Monto:</strong> Q <%= monto %></p>
            <p><strong>Mora:</strong> Q <%= mora %></p>
            <p><strong>Mes:</strong> <%= mes %></p>
            <p><strong>Año:</strong> <%= anio %></p>
        </div>
    
        <form action="<%= request.getContextPath() %>/ControladorResidente" method="post">
            <input type="hidden" name="accion" value="guardarpago">

            <input type="hidden" name="id_tipo" value="<%= idTipo %>">
            <input type="hidden" name="tipo_pago" value="<%= tipoPago %>">
            <input type="hidden" name="monto" value="<%= monto %>">
            <input type="hidden" name="mora" value="<%= mora %>">
            <input type="hidden" name="mes" value="<%= mes %>">
            <input type="hidden" name="anio" value="<%= anio %>">

            <%-- 👇 Si es Multa, mandamos también el id_pago existente --%>
            <% if ("Multa".equalsIgnoreCase(tipoPago) && idPagoMulta != null) { %>
                <input type="hidden" name="id_pago" value="<%= idPagoMulta %>">
            <% } %>

            <div class="mb-3">
                <label for="metodo_pago" class="form-label">Método de pago:</label>
                <select name="metodo_pago" id="metodo_pago" class="form-select" required onchange="toggleCamposTarjeta()">
                    <option value="">Seleccione un tipo de pago</option>
                    <% if (listaMetodos != null) {
                           for (MetodoPago mp : listaMetodos) { %>
                        <option value="<%= mp.getIdMetodo() %>"><%= mp.getNombre() %></option>
                    <%   }
                       } %>
                </select>
            </div>

            <div id="seccionTarjeta" class="card p-3 mb-3" style="display:none;">
                <h6>Datos de la Tarjeta</h6>

                <% if (listaTarjetas != null && !listaTarjetas.isEmpty()) { %>
                    <div class="mb-3">
                        <label for="id_tarjeta" class="form-label">Seleccione una tarjeta registrada:</label>
                        <select name="id_tarjeta" id="id_tarjeta" class="form-select">
                            <option value="">-- Seleccione una tarjeta --</option>
                            <% for (Tarjeta t : listaTarjetas) {
                                   String numTarjeta = t.getNumeroTarjeta();
                                   String ultimos4 = (numTarjeta != null && numTarjeta.length() >= 4)
                                       ? numTarjeta.substring(numTarjeta.length() - 4)
                                       : "XXXX";
                            %>
                                <option value="<%= t.getIdTarjeta() %>">
                                    <%= t.getNombreTarjeta() %> (**** **** **** <%= ultimos4 %>)
                                </option>
                            <% } %>
                        </select>
                    </div>
                <% } %>

                <div class="mb-3">
                    <label for="numero_tarjeta" class="form-label">Número de tarjeta:</label>
                    <input type="text" name="numero_tarjeta" id="numero_tarjeta" class="form-control"
                           maxlength="19" placeholder="XXXX XXXX XXXX XXXX">
                </div>

                <div class="row">
                    <div class="col-md-3 mb-3">
                        <label for="cvv" class="form-label">CVV:</label>
                        <input type="text" name="cvv" id="cvv" maxlength="4" class="form-control" placeholder="XXX">
                    </div>
                    <div class="col-md-5 mb-3">
                        <label for="fecha_vencimiento" class="form-label">Fecha de vencimiento:</label>
                        <input type="date" name="fecha_vencimiento" id="fecha_vencimiento" class="form-control">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="nombre_titular" class="form-label">Nombre del titular:</label>
                        <input type="text" name="nombre_titular" id="nombre_titular" class="form-control"
                               placeholder="Nombre como aparece en la tarjeta">
                    </div>
                </div>
            </div>

            <button type="submit" class="btn btn-success">Registrar Pago</button>
            <button type="button" class="btn btn-danger" onclick="confirmarCancelacion()">Cancelar</button>
            <script>
            function confirmarCancelacion() {
                if (confirm('¿Desea cancelar el pago?')) {
                    window.location.href = '<%= request.getContextPath() %>/ControladorResidente?accion=pagos';
                } else {
                    return false;
                }
            }
            </script>
        </form>
    </div>
</body>
</html>



        
