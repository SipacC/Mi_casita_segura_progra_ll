package ModeloDAO;

import Modelo.TipoInconveniente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoInconvenienteDAO {

    private final Connection con;

    public TipoInconvenienteDAO(Connection con) {
        this.con = con;
    }

    public List<TipoInconveniente> listar() {
        List<TipoInconveniente> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_inconveniente, nombre FROM tipoinconveniente ORDER BY nombre ASC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoInconveniente tipo = new TipoInconveniente();
                tipo.setIdTipoInconveniente(rs.getInt("id_tipo_inconveniente"));
                tipo.setNombre(rs.getString("nombre"));
                lista.add(tipo);
            }

        } catch (SQLException e) {
            System.err.println("Error en listar() de TipoInconvenienteDAO: " + e.getMessage());
        }
        return lista;
    }
}
