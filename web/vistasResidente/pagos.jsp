<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Pago"%>
<%
    List<Pago> pagos = (List<Pago>) request.getAttribute("listaPagos");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestionar Pagos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/pagos.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/chatresidente.css">

</head>

<body class="pagos-page">
<div class="container mt-4">
    <h2 class="text-center mb-4 text-primary fw-bold">Gestionar Pagos</h2>

    <div class="d-flex justify-content-start mb-4">
        <a href="<%=request.getContextPath()%>/ControladorResidente?accion=menu" 
           class="btn btn-secondary me-2">Volver al menú</a>

        <a href="<%=request.getContextPath()%>/ControladorResidente?accion=consultarPagos" 
           class="btn btn-primary">Pagar Servicio</a>
    </div>

    <!-- 🔹 Formulario de filtros -->
    <form class="card p-3 shadow-sm mb-4" method="get" action="<%=request.getContextPath()%>/ControladorResidente">
        <input type="hidden" name="accion" value="pagosFiltrados">
        <div class="row g-3 align-items-end">
            
            <div class="col-md-3">
                <label class="form-label fw-semibold">Tipo de Pago</label>
                <input type="text" name="tipo" class="form-control" placeholder="Ej: Mantenimiento, Multa...">
            </div>

            <div class="col-md-3">
                <label class="form-label fw-semibold">Método</label>
                <input type="text" name="metodo" class="form-control" placeholder="Ej: Tarjeta, Efectivo...">
            </div>

            <div class="col-md-2">
                <label class="form-label fw-semibold">Estado</label>
                <select name="estado" class="form-select">
                    <option value="">Todos</option>
                    <option value="confirmado">Confirmado</option>
                    <option value="pendiente">Pendiente</option>
                    <option value="rechazado">Rechazado</option>
                </select>
            </div>

            <div class="col-md-2">
                <label class="form-label fw-semibold">Período Pagado</label>
                <!-- ✅ Solo permite seleccionar Mes/Año -->
                <input type="month" name="periodo" class="form-control">
            </div>

            <div class="col-md-2">
                <label class="form-label fw-semibold">Fecha de Registro</label>
                <!-- ✅ Permite seleccionar Día/Mes/Año -->
                <input type="date" name="fecha" class="form-control">
            </div>

            <div class="col-md-12 text-end mt-3">
                <button type="submit" class="btn btn-success px-4">Filtrar</button>
            </div>
        </div>
    </form>

    <!-- 🔹 Tabla de resultados -->
    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle text-center">
            <thead class="table-primary">
                <tr>
                    <th>No. de Pago</th>
                    <th>Tipo</th>
                    <th>Método</th>
                    <th>Tarjeta</th>
                    <th>Monto (Q)</th>
                    <th>Mora (Q)</th>
                    <th>Mes/Año Pagado</th>
                    <th>Fecha Registro</th>
                    <th>Observaciones</th>
                    <th>Estado</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (pagos != null && !pagos.isEmpty()) {
                        for (Pago p : pagos) {
                %>
                <tr>
                    <td><%= p.getIdPago() %></td>
                    <td><%= p.getNombreTipo() != null ? p.getNombreTipo() : "—" %></td>
                    <td><%= p.getNombreMetodo() != null ? p.getNombreMetodo() : "—" %></td>
                    <td><%= p.getNombreTarjeta() != null ? p.getNombreTarjeta() : "—" %></td>
                    <td>Q. <%= String.format("%.2f", p.getMonto()) %></td>
                    <td>Q. <%= String.format("%.2f", p.getMora()) %></td>
                    <td><%= p.getMesPagado() %>/<%= p.getAnioPagado() %></td>
                    <td><%= p.getFechaPago() != null ? p.getFechaPago() : "—" %></td>
                    <td><%= p.getObservaciones() != null ? p.getObservaciones() : "" %></td>
                    <td>
                        <% if ("confirmado".equalsIgnoreCase(p.getEstado())) { %>
                            <span class="badge bg-success">Confirmado</span>
                        <% } else if ("pendiente".equalsIgnoreCase(p.getEstado())) { %>
                            <span class="badge bg-warning text-dark">Pendiente</span>
                        <% } else if ("rechazado".equalsIgnoreCase(p.getEstado())) { %>
                            <span class="badge bg-danger">Rechazado</span>
                        <% } else { %>
                            <span class="badge bg-secondary"><%= p.getEstado() %></span>
                        <% } %>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="10" class="text-center text-danger fw-bold">No tienes pagos registrados.</td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
