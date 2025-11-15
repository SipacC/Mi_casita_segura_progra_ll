package ModeloDAO;

import Modelo.TipoPago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoPagoDAO {

    private final Connection con;

    public TipoPagoDAO(Connection con) {
        this.con = con;
    }

    public List<TipoPago> listar() {
        List<TipoPago> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipopago ORDER BY nombre ASC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoPago tp = new TipoPago();
                tp.setIdTipo(rs.getInt("id_tipo"));
                tp.setNombre(rs.getString("nombre"));
                tp.setMonto(rs.getDouble("monto"));
                lista.add(tp);
            }

        } catch (Exception e) {
            System.err.println("Error en TipoPagoDAO.listar(): " + e.getMessage());
        }
        return lista;
    }

    public TipoPago buscarPorId(int idTipo) {
    String sql = "SELECT * FROM tipopago WHERE id_tipo = ?";
    TipoPago tp = null;

    try (PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idTipo);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                tp = new TipoPago();
                tp.setIdTipo(rs.getInt("id_tipo"));
                tp.setNombre(rs.getString("nombre"));
                tp.setMonto(rs.getDouble("monto"));
            }
        }

    } catch (Exception e) {
        System.err.println("Error en buscarPorId(): " + e.getMessage());
    }
    return tp;
}
 public TipoPago buscarPorNombre(String nombre) {
    String sql = "SELECT * FROM tipopago WHERE LOWER(nombre) = LOWER(?)";
    TipoPago tp = null;

    try (PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, nombre);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                tp = new TipoPago();
                tp.setIdTipo(rs.getInt("id_tipo"));
                tp.setNombre(rs.getString("nombre"));
                tp.setMonto(rs.getDouble("monto"));
            }
        }

    } catch (Exception e) {
        System.err.println("Error en buscarPorNombre(): " + e.getMessage());
    }
    return tp;
}


}
