package ModeloDAO;

import Modelo.Pago;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    private final Connection con;
    public PagoDAO(Connection con) {
        this.con = con;
    }

    public List<Pago> listarPorUsuario(int idUsuario) {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT p.*, " +
                    "tp.nombre AS nombre_tipo, " +
                    "mp.nombre AS nombre_metodo, " +
                    "t.nombre_tarjeta AS nombre_tarjeta " +
                    "FROM pago p " +
                    "LEFT JOIN tipopago tp ON p.id_tipo = tp.id_tipo " +
                    "LEFT JOIN metodopago mp ON p.id_metodo = mp.id_metodo " +
                    "LEFT JOIN tarjeta t ON p.id_tarjeta = t.id_tarjeta " +
                    "WHERE p.id_usuario = ? " +
                    "ORDER BY p.fecha_pago DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago p = mapResultSet(rs);
                    p.setNombreTipo(rs.getString("nombre_tipo"));
                    p.setNombreMetodo(rs.getString("nombre_metodo"));
                    p.setNombreTarjeta(rs.getString("nombre_tarjeta"));
                    lista.add(p);
                }
            }

        } catch (Exception e) {
            System.err.println("Error en listarPorUsuario(): " + e.getMessage());
        }

        return lista;
    }


    public List<Pago> listarPorUsuarioFiltro(int idUsuario, String estado, int mes, int anio) {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT p.*, t.nombre AS nombre_tipo " +
                     "FROM pago p " +
                     "JOIN tipopago t ON p.id_tipo = t.id_tipo " +
                     "WHERE p.id_usuario = ? " +
                     "AND (? IS NULL OR p.estado = ?) " +
                     "AND (? = 0 OR EXTRACT(MONTH FROM p.fecha_pago) = ?) " +
                     "AND (? = 0 OR EXTRACT(YEAR FROM p.fecha_pago) = ?) " +
                     "ORDER BY p.fecha_pago DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            ps.setString(2, estado);
            ps.setString(3, estado);
            ps.setInt(4, mes);
            ps.setInt(5, mes);
            ps.setInt(6, anio);
            ps.setInt(7, anio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago p = mapResultSet(rs);
                    p.setNombreTipo(rs.getString("nombre_tipo"));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en listarPorUsuarioFiltro(): " + e.getMessage());
        }
        return lista;
    }

    public List<Pago> listarPorUsuarioFiltroAvanzado(
            int idUsuario,
            String tipo,
            String metodo,
            String estado,
            String periodo, 
            String fechaReal
    ) {
        List<Pago> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT p.*, tp.nombre AS nombre_tipo, mp.nombre AS nombre_metodo, t.nombre_tarjeta AS nombre_tarjeta " +
            "FROM pago p " +
            "LEFT JOIN tipopago tp ON p.id_tipo = tp.id_tipo " +
            "LEFT JOIN metodopago mp ON p.id_metodo = mp.id_metodo " +
            "LEFT JOIN tarjeta t ON p.id_tarjeta = t.id_tarjeta " +
            "WHERE p.id_usuario = ?"
        );

        if (tipo != null && !tipo.trim().isEmpty()) {
            sql.append(" AND LOWER(tp.nombre) LIKE LOWER(?)");
        }
        if (metodo != null && !metodo.trim().isEmpty()) {
            sql.append(" AND LOWER(mp.nombre) LIKE LOWER(?)");
        }
        if (estado != null && !estado.trim().isEmpty()) {
            sql.append(" AND LOWER(p.estado) = LOWER(?)");
        }
        if (periodo != null && !periodo.trim().isEmpty()) {
            sql.append(" AND p.mes_pagado = ? AND p.anio_pagado = ?");
        }
        if (fechaReal != null && !fechaReal.trim().isEmpty()) {
            sql.append(" AND DATE(p.fecha_pago) = ?");
        }

        sql.append(" ORDER BY p.fecha_pago DESC");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, idUsuario);

            if (tipo != null && !tipo.trim().isEmpty())
                ps.setString(paramIndex++, "%" + tipo + "%");

            if (metodo != null && !metodo.trim().isEmpty())
                ps.setString(paramIndex++, "%" + metodo + "%");

            if (estado != null && !estado.trim().isEmpty())
                ps.setString(paramIndex++, estado);

            if (periodo != null && !periodo.trim().isEmpty()) {
                try {
                    String[] partes = periodo.split("-");
                    int anio = Integer.parseInt(partes[0]);
                    int mes = Integer.parseInt(partes[1]);
                    ps.setInt(paramIndex++, mes);
                    ps.setInt(paramIndex++, anio);
                } catch (Exception e) {
                    System.err.println("Período inválido: " + periodo);
                }
            }

            if (fechaReal != null && !fechaReal.trim().isEmpty()) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(fechaReal));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago p = mapResultSet(rs);
                    p.setNombreTipo(rs.getString("nombre_tipo"));
                    p.setNombreMetodo(rs.getString("nombre_metodo"));
                    p.setNombreTarjeta(rs.getString("nombre_tarjeta"));
                    lista.add(p);
                }
            }

        } catch (Exception e) {
            System.err.println("Error en listarPorUsuarioFiltroAvanzado(): " + e.getMessage());
        }

        return lista;
    }

    public int registrarPago(Pago pago) {
        String sql = "INSERT INTO pago " +
                     "(id_usuario, id_tipo, id_metodo, id_tarjeta, fecha_pago, monto, mora, observaciones, estado, mes_pagado, anio_pagado) " +
                     "VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pago.getIdUsuario());
            ps.setInt(2, pago.getIdTipo());
            ps.setInt(3, pago.getIdMetodo());

            if (pago.getIdTarjeta() != null) {
                ps.setInt(4, pago.getIdTarjeta());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setDouble(5, pago.getMonto());
            ps.setDouble(6, pago.getMora());
            ps.setString(7, pago.getObservaciones());
            ps.setString(8, pago.getEstado());
            ps.setInt(9, pago.getMesPagado());
            ps.setInt(10, pago.getAnioPagado());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error en registrarPago(): " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizarEstado(int idPago, String estado) {
        String sql = "UPDATE pago SET estado = ? WHERE id_pago = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, estado);
            ps.setInt(2, idPago);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error en actualizarEstado(): " + e.getMessage());
            return false;
        }
    }

    public int[] getUltimoMesYAnioPagado(int idUsuario, int idTipoPago) {
        String sql = "SELECT mes_pagado, anio_pagado " +
                     "FROM pago " +
                     "WHERE id_usuario = ? AND id_tipo = ? AND estado = 'confirmado' " +
                     "ORDER BY anio_pagado DESC, mes_pagado DESC " +
                     "LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            ps.setInt(2, idTipoPago);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[]{rs.getInt("mes_pagado"), rs.getInt("anio_pagado")};
                }
            }
        } catch (Exception e) {
            System.err.println("Error en getUltimoMesYAnioPagado(): " + e.getMessage());
        }
        return new int[]{0, 0};
    }

    public int[] getMesYAnioCorrespondiente(int idUsuario, int idTipoPago, Date fechaCreacion) {
        int[] ultimo = getUltimoMesYAnioPagado(idUsuario, idTipoPago);
        int mes, anio;

        if (ultimo[0] == 0) {
            LocalDate fc = fechaCreacion.toLocalDate();
            mes = fc.getMonthValue();
            anio = fc.getYear();
        } else {
            mes = (ultimo[0] == 12) ? 1 : ultimo[0] + 1;
            anio = (ultimo[0] == 12) ? ultimo[1] + 1 : ultimo[1];
        }
        return new int[]{mes, anio};
    }

    public double calcularMoraMensual(int mesAPagar, int anioAPagar, LocalDate fechaActual) {
        LocalDate fechaLimite = LocalDate.of(anioAPagar, mesAPagar, 5);
        if (fechaActual.isAfter(fechaLimite)) {
            long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(fechaLimite, fechaActual);
            return diasAtraso * 25.0;
        }
        return 0.0;
    }

    public boolean tienePagosPendientes(int idUsuario, int idTipo, java.sql.Date fechaCreacion) {
        String sql = "SELECT COUNT(*) FROM pago " +
                     "WHERE id_usuario = ? " +
                     "AND id_tipo = ? " +
                     "AND estado = 'pendiente' " +
                     "AND fecha_pago >= ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            ps.setInt(2, idTipo);
            ps.setDate(3, fechaCreacion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en tienePagosPendientes(): " + e.getMessage());
        }
        return false;
    }

    public boolean usuarioTieneMultas(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM pago p " +
                     "INNER JOIN tipopago t ON p.id_tipo = t.id_tipo " +
                     "WHERE p.id_usuario = ? " +
                     "AND LOWER(t.nombre) = 'multa'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en usuarioTieneMultas(): " + e.getMessage());
        }
        return false;
    }

    public Pago obtenerMultaPendientePorUsuario(int idUsuario) {
        String sql = "SELECT * FROM pago p " +
                     "JOIN tipopago t ON p.id_tipo = t.id_tipo " +
                     "WHERE p.id_usuario = ? AND LOWER(t.nombre) = 'multa' " +
                     "AND p.estado = 'pendiente' LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pago p = new Pago();
                    p.setIdPago(rs.getInt("id_pago"));
                    p.setIdUsuario(rs.getInt("id_usuario"));
                    p.setIdTipo(rs.getInt("id_tipo"));
                    p.setMonto(rs.getDouble("monto"));
                    p.setMesPagado(rs.getInt("mes_pagado"));
                    p.setAnioPagado(rs.getInt("anio_pagado"));
                    return p;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en obtenerMultaPendientePorUsuario(): " + e.getMessage());
        }
        return null;
    }

    public List<Pago> obtenerMultasPendientesPorUsuario(int idUsuario) {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pago " +
                    "WHERE id_usuario = ? " +
                    "AND estado = 'pendiente' " +
                    "AND id_tipo = (SELECT id_tipo FROM tipopago WHERE LOWER(nombre) = 'multa')";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pago p = mapResultSet(rs);
                    p.setMesPagado(rs.getInt("mes_pagado"));
                    p.setAnioPagado(rs.getInt("anio_pagado"));

                    lista.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en obtenerMultasPendientesPorUsuario(): " + e.getMessage());
        }
        return lista;
}

private Pago mapResultSet(ResultSet rs) throws SQLException {
    Pago p = new Pago();
    p.setIdPago(rs.getInt("id_pago"));
    p.setIdUsuario(rs.getInt("id_usuario"));
    p.setIdTipo(rs.getInt("id_tipo"));
    p.setIdMetodo(rs.getInt("id_metodo"));
    p.setIdTarjeta((Integer) rs.getObject("id_tarjeta"));
    p.setFechaPago(rs.getTimestamp("fecha_pago"));
    p.setMonto(rs.getDouble("monto"));
    p.setMora(rs.getDouble("mora"));
    p.setObservaciones(rs.getString("observaciones"));
    p.setEstado(rs.getString("estado"));
    p.setMesPagado(rs.getInt("mes_pagado"));
    p.setAnioPagado(rs.getInt("anio_pagado"));
    try {
        p.setNombreTipo(rs.getString("nombre_tipo"));
    } catch (SQLException ignore) {}

    try {
        p.setNombreMetodo(rs.getString("nombre_metodo"));
    } catch (SQLException ignore) {}

    try {
        p.setNombreTarjeta(rs.getString("nombre_tarjeta"));
    } catch (SQLException ignore) {}

    return p;
}

}
