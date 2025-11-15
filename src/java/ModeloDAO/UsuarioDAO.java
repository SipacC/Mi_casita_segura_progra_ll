package ModeloDAO;

import Modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsuarioDAO {

    private final Connection con;

    public UsuarioDAO(Connection con) {
        this.con = con;
    }

    public List<Usuario> listar() {
        List<Usuario> list = new ArrayList<>();
        String sql = "SELECT id_usuario, dpi, nombre, apellido, usuario, rol, contrasena, correo, lote, numero_casa, estado, fecha_creacion FROM usuarios";
        System.out.println("👥 [UsuarioDAO] Listando usuarios...");

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setDpi(rs.getString("dpi"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setUsuario(rs.getString("usuario"));
                u.setRol(rs.getString("rol"));
                u.setContrasena(rs.getString("contrasena"));
                u.setCorreo(rs.getString("correo"));
                u.setLote(rs.getString("lote"));
                u.setNumeroCasa(rs.getString("numero_casa"));
                u.setEstado(rs.getString("estado"));
                u.setFechaCreacion(rs.getDate("fecha_creacion"));
                list.add(u);
            }

        } catch (Exception e) {
            System.err.println(" Error en listar(): " + e.getMessage());
        }
        return list;
    }

    public Usuario list(int id) {
        String sql = "SELECT id_usuario, dpi, nombre, apellido, usuario, rol, contrasena, correo, lote, numero_casa, estado, fecha_creacion " +
                     "FROM usuarios WHERE id_usuario = ?";
        Usuario u = null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setDpi(rs.getString("dpi"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellido(rs.getString("apellido"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setRol(rs.getString("rol"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setCorreo(rs.getString("correo"));
                    u.setLote(rs.getString("lote"));
                    u.setNumeroCasa(rs.getString("numero_casa"));
                    u.setEstado(rs.getString("estado"));
                    u.setFechaCreacion(rs.getDate("fecha_creacion"));
                }
            }

        } catch (Exception e) {
            System.err.println("Error en list(int id): " + e.getMessage());
            e.printStackTrace();
        }
        return u;
    }

    public int add(Usuario u) {
        String sql = "INSERT INTO usuarios(dpi, nombre, apellido, usuario, rol, contrasena, correo, lote, numero_casa, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'activo')";
        int idGenerado = -1;

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getDpi());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellido());
            ps.setString(4, u.getUsuario());
            ps.setString(5, u.getRol());
            ps.setString(6, u.getContrasena());
            ps.setString(7, u.getCorreo());
            ps.setString(8, u.getLote());
            ps.setString(9, u.getNumeroCasa());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
            }

        } catch (Exception e) {
            System.err.println(" Error en add(): " + e.getMessage());
        }
        return idGenerado;
    }

    public boolean edit(Usuario u) {
        try {

            Map<String, Object> campos = new LinkedHashMap<>();
            campos.put("dpi", u.getDpi());
            campos.put("nombre", u.getNombre());
            campos.put("apellido", u.getApellido());
            campos.put("usuario", u.getUsuario());
            campos.put("rol", u.getRol());
            campos.put("contrasena", u.getContrasena());
            campos.put("correo", u.getCorreo());
            campos.put("lote", u.getLote());
            campos.put("numero_casa", u.getNumeroCasa());

            StringBuilder sql = new StringBuilder("UPDATE usuarios SET ");
            List<Object> params = new ArrayList<>();

            for (Map.Entry<String, Object> entry : campos.entrySet()) {
                Object valor = entry.getValue();
                if (valor != null && !valor.toString().trim().isEmpty()) {
                    sql.append(entry.getKey()).append(" = ?, ");
                    params.add(valor);
                }
            }

            if (params.isEmpty()) {
                return false;
            }

            sql.setLength(sql.length() - 2);
            sql.append(" WHERE id_usuario = ?");
            params.add(u.getIdUsuario());

            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            System.err.println("❌ Error en edit(): " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idUsuario) {
        String sql = "UPDATE usuarios SET estado = 'inactivo' WHERE id_usuario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Usuario findByLogin(String usuario, String contrasena) {
        String sql = "SELECT id_usuario, dpi, nombre, apellido, usuario, correo, contrasena, rol, lote, numero_casa, estado, fecha_creacion " +
                     "FROM usuarios WHERE usuario = ? AND contrasena = ?";
        Usuario u = null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setDpi(rs.getString("dpi"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellido(rs.getString("apellido"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setRol(rs.getString("rol"));
                    u.setCorreo(rs.getString("correo"));
                    u.setLote(rs.getString("lote"));
                    u.setNumeroCasa(rs.getString("numero_casa"));
                    u.setEstado(rs.getString("estado"));
                    u.setFechaCreacion(rs.getDate("fecha_creacion"));
                }
            }

        } catch (Exception e) {
            System.err.println("Error en findByLogin(): " + e.getMessage());
        }
        return u;
    }

    public boolean existeUsuarioOCorreoODpi(String usuario, String correo, String dpi) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE usuario = ? OR correo = ? OR dpi = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, correo);
            ps.setString(3, dpi);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            System.err.println("Error en existeUsuarioOCorreoODpi(): " + e.getMessage());
        }
        return false;
    }

    public boolean existeUsuarioOCorreoEdit(String usuario, String correo, int id) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE (usuario = ? OR correo = ?) AND id_usuario <> ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, correo);
            ps.setInt(3, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            System.err.println("Error en existeUsuarioOCorreoEdit(): " + e.getMessage());
        }
        return false;
    }

    public List<Usuario> listarResidentesActivos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, apellido, numero_casa, lote "
                   + "FROM usuarios "
                   + "WHERE rol = 'residente' AND estado = 'activo' "
                   + "ORDER BY nombre, apellido";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setNumeroCasa(rs.getString("numero_casa"));
                u.setLote(rs.getString("lote"));
                lista.add(u);
            }

        } catch (Exception e) {
            System.err.println("Error en listarResidentesActivos(): " + e.getMessage());
        }
        return lista;
    }

    public List<Usuario> listarGuardiasActivos() {
    List<Usuario> lista = new ArrayList<>();
    String sql = "SELECT id_usuario, nombre, apellido, correo " +
                 "FROM usuarios " +
                 "WHERE rol = 'seguridad' AND estado = 'activo' " +
                 "ORDER BY nombre, apellido";

    try (PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("id_usuario"));
            u.setNombre(rs.getString("nombre"));
            u.setApellido(rs.getString("apellido"));
            u.setCorreo(rs.getString("correo"));
            lista.add(u);
        }

    } catch (Exception e) {
        System.err.println("Error en listarGuardiasActivos(): " + e.getMessage());
    }
    return lista;
}

public List<Usuario> buscarDirectorio(String nombre, String apellido, String lote, String numeroCasa) {
    List<Usuario> lista = new ArrayList<>();
    StringBuilder sql = new StringBuilder(
        "SELECT nombre, apellido, correo, lote, numero_casa " +
        "FROM usuarios " +
        "WHERE rol = 'residente' AND estado = 'activo' "
    );

    List<String> condiciones = new ArrayList<>();
    List<Object> parametros = new ArrayList<>();

    if (nombre != null && !nombre.trim().isEmpty()) {
        condiciones.add("nombre ILIKE ?");
        parametros.add("%" + nombre + "%");
    }
    if (apellido != null && !apellido.trim().isEmpty()) {
        condiciones.add("apellido ILIKE ?");
        parametros.add("%" + apellido + "%");
    }
    if (lote != null && !lote.trim().isEmpty()) {
        condiciones.add("lote ILIKE ?");
        parametros.add("%" + lote + "%");
    }
    if (numeroCasa != null && !numeroCasa.trim().isEmpty()) {
        condiciones.add("numero_casa ILIKE ?");
        parametros.add("%" + numeroCasa + "%");
    }

    if (!condiciones.isEmpty()) {
        sql.append("AND (").append(String.join(" OR ", condiciones)).append(") ");
    }

    sql.append("ORDER BY nombre, apellido;");

    try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
        for (int i = 0; i < parametros.size(); i++) {
            ps.setObject(i + 1, parametros.get(i));
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setLote(rs.getString("lote"));
                u.setNumeroCasa(rs.getString("numero_casa"));
                lista.add(u);
            }
        }

    } catch (SQLException e) {
        System.err.println("❌ Error en buscarDirectorio(): " + e.getMessage());
    }

    return lista;
}

public List<String> listarCorreosAdministradores() {
    List<String> correos = new ArrayList<>();
    String sql = "SELECT correo FROM usuarios WHERE rol = 'administrador' AND estado = 'activo'";
    try (PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            correos.add(rs.getString("correo"));
        }
    } catch (SQLException e) {
        System.err.println("Error al listar correos de administradores: " + e.getMessage());
    }
    return correos;
}



}
