package ModeloDAO;

import Modelo.Incidente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IncidenteDAO {

    private final Connection con;

    public IncidenteDAO(Connection con) {
        this.con = con;
    }
    public boolean registrar(Incidente incidente) {
        String sql = "INSERT INTO incidente (id_residente, id_tipo_incidente, descripcion, fecha_reporte) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, incidente.getIdResidente());
            ps.setInt(2, incidente.getIdTipoIncidente());
            ps.setString(3, incidente.getDescripcion());
            ps.setTimestamp(4, incidente.getFechaReporte());

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("[IncidenteDAO] Error al registrar incidente: " + e.getMessage());
            return false;
        }
    }
}
