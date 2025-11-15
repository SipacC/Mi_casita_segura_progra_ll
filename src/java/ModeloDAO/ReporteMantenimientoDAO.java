package ModeloDAO;

import Modelo.ReporteMantenimiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReporteMantenimientoDAO {

    private final Connection con;

    public ReporteMantenimientoDAO(Connection con) {
        this.con = con;
    }

    public boolean insertar(ReporteMantenimiento reporte) {
        String sql = "INSERT INTO reportemantenimiento (id_residente, id_tipo_inconveniente, descripcion, fecha_incidente) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reporte.getIdResidente());
            ps.setInt(2, reporte.getIdTipoInconveniente());
            ps.setString(3, reporte.getDescripcion());
            ps.setTimestamp(4, reporte.getFechaIncidente());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar reporte de mantenimiento: " + e.getMessage());
            return false;
        }
    }
}
