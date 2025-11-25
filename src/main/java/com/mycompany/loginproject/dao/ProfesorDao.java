package com.mycompany.loginproject.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.model.Profesor;

/**
 * Clase para acceso a profesores de la DB
 *
 * @author jonat, Isacc-smth
 */
public class ProfesorDao {

    /**
     * Obtener los datos de un profesor por su id
     *
     * @param id identificador unico del docente
     *
     * @return una instancia de Profesor con los datos solicitados
     *
     * @see com.mycompany.loginproject.model.Profesor
     * */
    public Profesor findById(int id) throws SQLException {
        final String sql = """
                    SELECT id, nombre, apellido, usuario, contrasenia, ci, telefono, celular, correo 
                    FROM profesor WHERE id = ?
                    """;
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Profesor p = new Profesor();
                    p.setId(rs.getInt("id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setApellido(rs.getString("apellido"));
                    p.setUsuario(rs.getString("usuario"));
                    p.setContrasenia(rs.getString("contrasenia"));
                    int ci = rs.getInt("ci");
                    if (!rs.wasNull()) {
                        p.setCi(ci);
                    }
                    int tel = rs.getInt("telefono");
                    if (!rs.wasNull()) {
                        p.setTelefono(tel);
                    }
                    int cel = rs.getInt("celular");
                    if (!rs.wasNull()) {
                        p.setCelular(cel);
                    }
                    p.setCorreo(rs.getString("correo"));
                    return p;
                }
                return null;
            }
        }
    }

    /**
     * Actualizar los datos de un profesor
     *
     * @param p el profesor con los datos a modificar
     *
     * @return true si los datos se modificaron, false de lo contraio
     * */
    public boolean update(Profesor p) {
        final String sql = """
                        UPDATE profesor SET
                        nombre = ?, apellido = ?, usuario = ?, contrasenia = ?, ci = ?, 
                        telefono = ?, celular = ?, correo = ? 
                        WHERE id = ?;
                        """;
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellido());
            ps.setString(3, p.getUsuario());
            ps.setString(4, p.getContrasenia());
            if (p.getCi() != null) {
                ps.setInt(5, p.getCi());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            if (p.getTelefono() != null) {
                ps.setInt(6, p.getTelefono());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            if (p.getCelular() != null) {
                ps.setInt(7, p.getCelular());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, p.getCorreo());
            ps.setInt(9, p.getId());
            int affected = ps.executeUpdate();
            return affected == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
