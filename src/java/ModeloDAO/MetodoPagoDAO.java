package ModeloDAO;

import Modelo.MetodoPago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetodoPagoDAO {

    private final Connection con;

    public MetodoPagoDAO(Connection con) {
        this.con = con;
    }

    public List<MetodoPago> listar() {
        List<MetodoPago> lista = new ArrayList<>();
        String sql = "SELECT * FROM metodopago ORDER BY nombre ASC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MetodoPago mp = new MetodoPago();
                mp.setIdMetodo(rs.getInt("id_metodo"));
                mp.setNombre(rs.getString("nombre"));
                lista.add(mp);
            }

        } catch (Exception e) {
            System.err.println("Error en MetodoPagoDAO.listar(): " + e.getMessage());
        }
        return lista;
    }

    public MetodoPago buscarPorId(int idMetodo) {
    MetodoPago mp = null;
    String sql = "SELECT * FROM metodopago WHERE id_metodo = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idMetodo);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                mp = new MetodoPago();
                mp.setIdMetodo(rs.getInt("id_metodo"));
                mp.setNombre(rs.getString("nombre"));
            }
        }

    } catch (Exception e) {
        System.err.println("Error en MetodoPagoDAO.buscarPorId(): " + e.getMessage());
    }

    return mp;
}

}
