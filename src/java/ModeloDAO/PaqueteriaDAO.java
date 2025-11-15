package ModeloDAO;

import Modelo.Paqueteria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class PaqueteriaDAO {

    private final Connection con;

    public PaqueteriaDAO(Connection con) {
        this.con = con;
    }


    public List<Paqueteria> listar() {
        List<Paqueteria> lista = new ArrayList<>();
         String sql =
            "SELECT " +
            "p.id_paqueteria, " +
            "p.numero_guia, " +
            "p.estado, " +
            "p.fecha_registro, " +
            "p.fecha_entrega, " +
            "p.casa_residente, " +
            "p.observaciones, " +
            "CONCAT(r.nombre, ' ', r.apellido) AS residente, " +
            "r.lote AS lote_residente, " +
            "CONCAT(g1.nombre, ' ', g1.apellido) AS agente_registra, " +
            "CONCAT(g2.nombre, ' ', g2.apellido) AS agente_entrega " +
            "FROM Paqueteria p " +
            "LEFT JOIN Usuarios r ON p.id_residente = r.id_usuario " +
            "LEFT JOIN Usuarios g1 ON p.id_agente_registra = g1.id_usuario " +
            "LEFT JOIN Usuarios g2 ON p.id_agente_entrega = g2.id_usuario " +
            "WHERE p.estado = 'pendiente' " +
            "ORDER BY p.id_paqueteria DESC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Paqueteria p = new Paqueteria();

                p.setId_paqueteria(rs.getInt("id_paqueteria"));
                p.setNumero_guia(rs.getString("numero_guia"));
                p.setEstado(rs.getString("estado"));
                p.setFecha_registro(rs.getTimestamp("fecha_registro"));
                p.setFecha_entrega(rs.getTimestamp("fecha_entrega"));
                p.setCasa_residente(rs.getString("casa_residente"));
                p.setObservaciones(rs.getString("observaciones"));

                // 👤 Datos del JOIN
                p.setResidente(rs.getString("residente"));
                p.setLote_residente(rs.getString("lote_residente"));
                p.setAgente_registra(rs.getString("agente_registra"));
                p.setAgente_entrega(rs.getString("agente_entrega"));

                lista.add(p);
            }

        } catch (Exception e) {
            System.err.println("Error al listar paquetería: " + e.getMessage());
        }

        return lista;
    }

    public boolean insertar(Paqueteria p) {
    String sql = "INSERT INTO Paqueteria ("
               + "numero_guia, "
               + "id_residente, "
               + "id_agente_registra, "
               + "id_agente_entrega, "
               + "casa_residente, "
               + "estado, "
               + "observaciones, "
               + "fecha_registro"
               + ") VALUES (?, ?, ?, NULL, ?, 'pendiente', ?, CURRENT_TIMESTAMP)";

    try (PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, p.getNumero_guia());
        ps.setInt(2, p.getId_residente());
        ps.setInt(3, p.getId_agente_registra());
        ps.setString(4, p.getCasa_residente());
        ps.setString(5, p.getObservaciones());

        int filas = ps.executeUpdate();
        return filas > 0;

    } catch (Exception e) {
        System.err.println("Error al insertar paquetería: " + e.getMessage());
        return false;
    }
}

public boolean actualizarEntrega(int idPaqueteria, int idAgenteEntrega) {
    String sql = "UPDATE Paqueteria "
               + "SET estado = 'entregado', "
               + "id_agente_entrega = ?, "
               + "fecha_entrega = CURRENT_TIMESTAMP "
               + "WHERE id_paqueteria = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idAgenteEntrega);
        ps.setInt(2, idPaqueteria);

        int filas = ps.executeUpdate();
        return filas > 0;

    } catch (Exception e) {
        System.err.println("Error al actualizar entrega: " + e.getMessage());
        return false;
    }
}

public List<Paqueteria> buscar(String texto) {
    List<Paqueteria> lista = new ArrayList<>();

    String sql =
        "SELECT p.id_paqueteria, p.numero_guia, p.estado, p.fecha_registro, " +
        "p.fecha_entrega, p.casa_residente, p.observaciones, " +
        "CONCAT(r.nombre, ' ', r.apellido) AS residente, " +
        "r.lote AS lote_residente, " +
        "CONCAT(g1.nombre, ' ', g1.apellido) AS agente_registra, " +
        "CONCAT(g2.nombre, ' ', g2.apellido) AS agente_entrega " +
        "FROM Paqueteria p " +
        "LEFT JOIN Usuarios r ON p.id_residente = r.id_usuario " +
        "LEFT JOIN Usuarios g1 ON p.id_agente_registra = g1.id_usuario " +
        "LEFT JOIN Usuarios g2 ON p.id_agente_entrega = g2.id_usuario " +
        "WHERE p.estado = 'pendiente' AND (" +
        "p.numero_guia ILIKE ? OR " +
        "p.casa_residente ILIKE ? OR " +
        "r.nombre ILIKE ? OR " +
        "r.apellido ILIKE ?) " +
        "ORDER BY p.id_paqueteria DESC " +
        "LIMIT 50";
    try (PreparedStatement ps = con.prepareStatement(sql)) {

        String parametro = "%" + texto + "%";

        for (int i = 1; i <= 4; i++) {
            ps.setString(i, parametro);
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Paqueteria p = new Paqueteria();
            p.setId_paqueteria(rs.getInt("id_paqueteria"));
            p.setNumero_guia(rs.getString("numero_guia"));
            p.setEstado(rs.getString("estado"));
            p.setFecha_registro(rs.getTimestamp("fecha_registro"));
            p.setFecha_entrega(rs.getTimestamp("fecha_entrega"));
            p.setCasa_residente(rs.getString("casa_residente"));
            p.setObservaciones(rs.getString("observaciones"));
            p.setResidente(rs.getString("residente"));
            p.setLote_residente(rs.getString("lote_residente"));
            p.setAgente_registra(rs.getString("agente_registra"));
            p.setAgente_entrega(rs.getString("agente_entrega"));

            lista.add(p);
        }

    } catch (Exception e) {
        System.err.println("Error en búsqueda dinámica de paquetería: " + e.getMessage());
    }

    return lista;
}

public Paqueteria obtenerPorId(int idPaqueteria) {
    Paqueteria p = null;
    String sql = "SELECT id_paqueteria, numero_guia, id_residente FROM Paqueteria WHERE id_paqueteria = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idPaqueteria);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            p = new Paqueteria();
            p.setId_paqueteria(rs.getInt("id_paqueteria"));
            p.setNumero_guia(rs.getString("numero_guia"));
            p.setId_residente(rs.getInt("id_residente"));
        }

    } catch (Exception e) {
        System.err.println("Error al obtener paquetería por ID: " + e.getMessage());
    }

    return p;
}


}
