package ModeloDAO;

import Modelo.CatalogoIncidente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoIncidenteDAO {

    private final Connection con;

    public CatalogoIncidenteDAO(Connection con) {
        this.con = con;
    }
    public List<CatalogoIncidente> listar() {
        List<CatalogoIncidente> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_incidente, nombre FROM catalogo_incidente ORDER BY id_tipo_incidente ASC";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CatalogoIncidente tipo = new CatalogoIncidente();
                tipo.setIdTipoIncidente(rs.getInt("id_tipo_incidente"));
                tipo.setNombre(rs.getString("nombre"));
                lista.add(tipo);
            }

        } catch (SQLException e) {
            System.err.println("[CatalogoIncidenteDAO] Error al listar tipos de incidente: " + e.getMessage());
        }

        return lista;
    }


    public String obtenerNombrePorId(int idTipoIncidente) {
        String nombre = null;
        String sql = "SELECT nombre FROM catalogo_incidente WHERE id_tipo_incidente = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTipoIncidente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombre = rs.getString("nombre");
                }
            }
        } catch (Exception e) {
            System.err.println("[CatalogoIncidenteDAO] Error al obtener nombre por ID: " + e.getMessage());
        }

        return nombre;
    }

}
