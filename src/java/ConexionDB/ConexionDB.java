package ConexionDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB implements AutoCloseable {
    private static Connection conexion = null;
    private static final String URL = "jdbc:postgresql://localhost:5432/mi_casita_segura_local";
    private static final String USER = "postgres";
    private static final String PASSWORD = "manuel59";
    public Connection openConnection() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("org.postgresql.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[ConexionDB] Nueva conexión abierta (" + conexion.hashCode() + ")");
            } else {
                System.out.println("[ConexionDB] Reutilizando conexión existente (" + conexion.hashCode() + ")");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[ConexionDB] Driver PostgreSQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[ConexionDB] Error SQL: " + e.getMessage());
            try {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[ConexionDB] Conexión reestablecida después del error.");
            } catch (SQLException ex) {
                System.err.println("[ConexionDB] No se pudo reestablecer la conexión: " + ex.getMessage());
            }
        }
        return conexion;
    }
    
    public void closeConnection() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("[ConexionDB] Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("[ConexionDB]  Error al cerrar la conexión: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        closeConnection();
    }
}
