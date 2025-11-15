package ModeloDAO;

import Modelo.Catalogo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {

    private final Connection con;

    public CatalogoDAO(Connection con) {
        this.con = con;
    }

    public List<Catalogo> listarRoles() {
        List<Catalogo> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM Rol ORDER BY nombre";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Catalogo rol = new Catalogo();
                rol.setNombre(rs.getString("nombre"));
                lista.add(rol);
            }

        } catch (Exception e) {
            System.err.println("Error al listar roles: " + e.getMessage());
        }
        return lista;
    }

    public List<Catalogo> listarLotes() {
        List<Catalogo> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM Lote ORDER BY nombre";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Catalogo lote = new Catalogo();
                lote.setNombre(rs.getString("nombre"));
                lista.add(lote);
            }

        } catch (Exception e) {
            System.err.println("Error al listar lotes: " + e.getMessage());
        }
        return lista;
    }

    public List<Catalogo> listarNumerosCasa() {
        List<Catalogo> lista = new ArrayList<>();
        String sql = "SELECT numero FROM NumeroCasa ORDER BY numero";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Catalogo casa = new Catalogo();
                casa.setNombre(rs.getString("numero"));
                lista.add(casa);
            }

        } catch (Exception e) {
            System.err.println("Error al listar números de casa: " + e.getMessage());
        }
        return lista;
    }
}
