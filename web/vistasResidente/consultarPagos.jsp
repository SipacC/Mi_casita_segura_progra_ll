<<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.TipoPago"%>
<%@page import="Modelo.Pago"%>
<%
    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }

    List<TipoPago> listaTipos = (List<TipoPago>) request.getAttribute("listaTipos");
    String tipoPago = (String) request.getAttribute("tipoPago");
    Double montoBase = (Double) request.getAttribute("montoBase");
    Integer mesAPagar = (Integer) request.getAttribute("mesAPagar");
    Integer anioAPagar = (Integer) request.getAttribute("anioAPagar");
    Double mora = (Double) request.getAttribute("mora");
    Double totalPago = (Double) request.getAttribute("totalPago");
    Integer idTipo = (Integer) request.getAttribute("idTipo");

    List<Pago> listaMultas = (List<Pago>) request.getAttribute("listaMultas");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Consultar Pagos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/consultarPagos.css">
</head>
<body class="consultar-pagos-page">
    <div class="container">
    <h2>Consultar Pagos</h2>
    <p><strong>Residente:</strong> <%= usr.getNombre() %> <%= usr.getApellido() %></p>

    <form action="<%= request.getContextPath() %>/ControladorResidente" method="post" class="mb-4 d-flex align-items-end gap-2">
        <input type="hidden" name="accion" value="consultarPagos">

        <div class="flex-grow-1">
            <label for="tipo_pago" class="form-label">Seleccione el tipo de pago:</label>
            <select name="tipo_pago" id="tipo_pago" class="form-select" required>
                <option value="">-- Seleccione --</option>
                <% if (listaTipos != null) {
                       for (TipoPago tp : listaTipos) { %>
                    <option value="<%= tp.getNombre() %>" 
                        <%= (tipoPago != null && tipoPago.equalsIgnoreCase(tp.getNombre())) ? "selected" : "" %>>
                        <%= tp.getNombre() %> (Q <%= tp.getMonto() %>)
                    </option>
                <%   }
                   } %>
            </select>
        </div>

        <button type="submit" class="btn btn-primary">Consultar</button>
        <button type="button" class="btn btn-danger" onclick="confirmarCancelacion()">Cancelar</button>

        <script>
        function confirmarCancelacion() {
            if (confirm('¿Desea cancelar el proceso de pago?')) {
                window.location.href = '<%= request.getContextPath() %>/ControladorResidente?accion=pagos';
            } else {
                return false;
            }
        }
        </script>

    </form>

    <% if ("Multa".equalsIgnoreCase(tipoPago) && listaMultas != null && !listaMultas.isEmpty()) { %>
        <div class="card p-3 mb-3">
            <h5>Multas pendientes</h5>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>ID Multa</th>
                        <th>Monto</th>
                        <th>Mes</th>
                        <th>Año</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Pago m : listaMultas) { %>
                    <tr>
                        <td><%= m.getIdPago() %></td>
                        <td>Q <%= m.getMonto() %></td>
                        <td><%= m.getMesPagado() %></td>
                        <td><%= m.getAnioPagado() %></td>
                        <td>
                            <a href="<%= request.getContextPath() %>/ControladorResidente?accion=registrarPago
&id_tipo=<%= idTipo %>
&tipo_pago=Multa
&monto=<%= m.getMonto() %>
&mora=0
&mes=<%= m.getMesPagado() %>
&anio=<%= m.getAnioPagado() %>
&id_pago=<%= m.getIdPago() %>" 
                               class="btn btn-success btn-sm">Registrar</a>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>

    <% } else if (tipoPago != null && totalPago != null) { %>
        <div class="card p-3 mb-3">
            <h5>Resultado</h5>
            <p><strong>Tipo de pago:</strong> <%= tipoPago %></p>
            <p><strong>Monto base:</strong> Q <%= montoBase %></p>
            <p><strong>Mes a pagar:</strong> <%= mesAPagar %>/<%= anioAPagar %></p>
            <p><strong>Mora:</strong> Q <%= mora %></p>
            <hr>
            <p><strong>Total a pagar:</strong> <span class="text-success">Q <%= totalPago %></span></p>


            <a href="<%= request.getContextPath() %>/ControladorResidente?accion=registrarPago
&id_tipo=<%= (idTipo != null ? idTipo : -1) %>
&tipo_pago=<%= tipoPago %>
&monto=<%= (totalPago != null ? totalPago : montoBase) %>
&mora=<%= (mora != null ? mora : 0) %>
&mes=<%= (mesAPagar != null ? mesAPagar : 0) %>
&anio=<%= (anioAPagar != null ? anioAPagar : 0) %>" 
               class="btn btn-success">Confimar registro de pago</a>
        </div>
    <% } else if (tipoPago != null) { %>
        <div class="alert alert-warning">
            No tienes pagos pendientes de tipo <%= tipoPago %>.
        </div>
    <% } %>
    </div>
</body>
</html>
