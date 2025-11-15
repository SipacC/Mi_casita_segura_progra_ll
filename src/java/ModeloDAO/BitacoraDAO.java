package ModeloDAO;

import Modelo.Bitacora;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO {

    private final Connection con;

    public BitacoraDAO(Connection con) {
        this.con = con;
    }

    public void registrarAccion(int id_usuario, String accion, String modulo) {
        String sql = "INSERT INTO bitacora (id_usuario, accion, modulo) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id_usuario);
            ps.setString(2, accion);
            ps.setString(3, modulo);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Bitacora> listar(Integer idUsuario, String modulo, Date fecha) {
        List<Bitacora> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT b.id_bitacora, " +
            "       b.id_usuario AS id_usuario_actor, " +
            "       ua.nombre AS nombre_actor, ua.apellido AS apellido_actor, " +
            "       uf.nombre AS nombre_afectado, uf.apellido AS apellido_afectado, " +
            "       b.accion, b.modulo, b.fecha_hora " +
            "FROM bitacora b " +
            "JOIN usuarios ua ON b.id_usuario = ua.id_usuario " +
            "LEFT JOIN usuarios uf " +
            "       ON uf.id_usuario = CASE " +
            "           WHEN regexp_replace(b.accion, '[^0-9]', '', 'g') ~ '^[0-9]+$' " +
            "           THEN CAST(regexp_replace(b.accion, '[^0-9]', '', 'g') AS INT) " +
            "           ELSE NULL END " +
            "WHERE 1=1 "
        );

        if (idUsuario != null) {
            sql.append("AND b.id_usuario = ? ");
        }
        if (modulo != null && !modulo.trim().isEmpty()) {
            sql.append("AND b.modulo = ? ");
        }
        if (fecha != null) {
            sql.append("AND CAST(b.fecha_hora AS DATE) = ? ");
        }

        sql.append("ORDER BY b.fecha_hora DESC");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;
            if (idUsuario != null) {
                ps.setInt(index++, idUsuario);
            }
            if (modulo != null && !modulo.trim().isEmpty()) {
                ps.setString(index++, modulo);
            }
            if (fecha != null) {
                ps.setDate(index++, fecha);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Bitacora b = new Bitacora();
                b.setId_bitacora(rs.getInt("id_bitacora"));
                b.setId_usuario(rs.getInt("id_usuario_actor")); // 👈 ahora correcto
                b.setUsuario_actor(rs.getString("nombre_actor") + " " + rs.getString("apellido_actor"));

                String nombreAfectado = rs.getString("nombre_afectado");
                String apellidoAfectado = rs.getString("apellido_afectado");
                if (nombreAfectado != null || apellidoAfectado != null) {
                    b.setUsuario_afectado(
                        (nombreAfectado != null ? nombreAfectado : "") + " " +
                        (apellidoAfectado != null ? apellidoAfectado : "")
                    );
                }

                b.setAccion(rs.getString("accion"));
                b.setModulo(rs.getString("modulo"));
                b.setFecha_hora(rs.getTimestamp("fecha_hora"));

                lista.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
