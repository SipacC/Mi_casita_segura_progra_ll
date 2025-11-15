package ModeloDAO;

import Modelo.Tarjeta;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TarjetaDAO {

    private final Connection con;

    public TarjetaDAO(Connection con) {
        this.con = con;
    }

    public List<Tarjeta> listarPorUsuario(int idUsuario) {
        List<Tarjeta> lista = new ArrayList<>();
        String sql = "SELECT * FROM tarjeta WHERE id_usuario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSet(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("Error en listarPorUsuario(): " + e.getMessage());
        }
        return lista;
    }

    public Tarjeta buscarPorNombreTarjeta(int idUsuario, String nombreTarjeta) {
        String sql = "SELECT * FROM tarjeta WHERE id_usuario = ? AND LOWER(nombre_tarjeta) = LOWER(?)";
        Tarjeta t = null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, nombreTarjeta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t = mapResultSet(rs);
                }
            }

        } catch (Exception e) {
            System.err.println("Error en buscarPorNombreTarjeta(): " + e.getMessage());
        }
        return t;
    }

    public boolean agregar(Tarjeta t) {
        String sql = "INSERT INTO tarjeta (id_usuario, nombre_tarjeta, numero_tarjeta, fecha_vencimiento, cvv, nombre_titular, tipo_tarjeta, saldo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getIdUsuario());
            ps.setString(2, t.getNombreTarjeta());
            ps.setString(3, t.getNumeroTarjeta());
            ps.setDate(4, new java.sql.Date(t.getFechaVencimiento().getTime()));
            ps.setString(5, t.getCvv());
            ps.setString(6, t.getNombreTitular());
            ps.setString(7, t.getTipoTarjeta());
            ps.setDouble(8, t.getSaldo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error en agregar(): " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Tarjeta t) {
        try {

            Map<String, Object> campos = new LinkedHashMap<>();
            campos.put("nombre_tarjeta", t.getNombreTarjeta());
            campos.put("numero_tarjeta", t.getNumeroTarjeta());
            campos.put("fecha_vencimiento", t.getFechaVencimiento());
            campos.put("cvv", t.getCvv());
            campos.put("nombre_titular", t.getNombreTitular());
            campos.put("tipo_tarjeta", t.getTipoTarjeta());
            campos.put("saldo", t.getSaldo());

            StringBuilder sql = new StringBuilder("UPDATE tarjeta SET ");
            List<Object> params = new ArrayList<>();

            for (Map.Entry<String, Object> entry : campos.entrySet()) {
                Object valor = entry.getValue();
                if (valor != null && !valor.toString().trim().isEmpty()) {
                    sql.append(entry.getKey()).append(" = ?, ");
                    if (valor instanceof java.util.Date) {
                        params.add(new java.sql.Date(((java.util.Date) valor).getTime()));
                    } else {
                        params.add(valor);
                    }
                }
            }

            if (params.isEmpty()) {
                return false;
            }
            sql.setLength(sql.length() - 2);
            sql.append(" WHERE id_tarjeta = ?");
            params.add(t.getIdTarjeta());

            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            System.err.println("Error en actualizar(): " + e.getMessage());
            return false;
        }
    }


    public boolean eliminar(int idTarjeta) {
        String sql = "DELETE FROM tarjeta WHERE id_tarjeta = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTarjeta);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Error en eliminar(): " + e.getMessage());
            return false;
        }
    }

    public Tarjeta buscarPorId(int idTarjeta) {
        String sql = "SELECT * FROM tarjeta WHERE id_tarjeta = ?";
        Tarjeta t = null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTarjeta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t = mapResultSet(rs);
                }
            }

        } catch (Exception e) {
            System.err.println("Error en buscarPorId(): " + e.getMessage());
        }
        return t;
    }

    public Tarjeta buscarPorUsuarioYId(int idUsuario, int idTarjeta) {
        String sql = "SELECT * FROM tarjeta WHERE id_usuario = ? AND id_tarjeta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idTarjeta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en buscarPorUsuarioYId(): " + e.getMessage());
        }
        return null;
    }

    public Tarjeta obtenerTarjetaPrincipal(int idUsuario) {
        String sql = "SELECT * FROM tarjeta WHERE id_usuario = ? ORDER BY id_tarjeta ASC LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en obtenerTarjetaPrincipal(): " + e.getMessage());
        }
        return null;
    }

    public boolean tieneSaldoSuficiente(int idTarjeta, double montoRequerido) {
        String sql = "SELECT saldo FROM tarjeta WHERE id_tarjeta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTarjeta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double saldo = rs.getDouble("saldo");
                    return saldo >= montoRequerido;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en tieneSaldoSuficiente(): " + e.getMessage());
        }
        return false;
    }

    public boolean actualizarSaldo(int idTarjeta, double nuevoSaldo) {
        String sql = "UPDATE tarjeta SET saldo = ? WHERE id_tarjeta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, nuevoSaldo);
            ps.setInt(2, idTarjeta);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error en actualizarSaldo(): " + e.getMessage());
        }
        return false;
    }

    private Tarjeta mapResultSet(ResultSet rs) throws SQLException {
        Tarjeta t = new Tarjeta();
        t.setIdTarjeta(rs.getInt("id_tarjeta"));
        t.setIdUsuario(rs.getInt("id_usuario"));
        t.setNombreTarjeta(rs.getString("nombre_tarjeta"));
        t.setNumeroTarjeta(rs.getString("numero_tarjeta"));
        t.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
        t.setCvv(rs.getString("cvv"));
        t.setNombreTitular(rs.getString("nombre_titular"));
        t.setTipoTarjeta(rs.getString("tipo_tarjeta"));
        t.setSaldo(rs.getDouble("saldo"));
        return t;
    }
}
