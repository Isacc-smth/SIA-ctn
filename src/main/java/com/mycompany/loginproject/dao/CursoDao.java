package com.mycompany.loginproject.dao;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.model.Curso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author jonat
 */
public class CursoDao {

    /**
     * consultar los cursos asociados con un profesor (usuario)
     *
     * @param userId identificador unico del profesor
     *
     * @return una lista de cursos con los datos solicitados
     *
     * @throws SQLException cuando hay un error de comunicacion con la base de datos o una consulata mal formada
     * */
    public ArrayList<Curso> consultarCursos(int userId) throws SQLException {
        ArrayList<Curso> cursos = new ArrayList<>();
        String sql = """
                SELECT c.id, nombre AS especialidad, promocion, seccion 
                FROM curso c JOIN especialidad e ON c.especialidad_id = e.id 
                WHERE c.id IN (SELECT curso_id FROM planilla WHERE profesor_id = ?);
                """;
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, userId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int curso_id = rs.getInt("id");
                    String especialidad = rs.getString("especialidad");
                    int promocion = rs.getInt("promocion");
                    String seccion = rs.getString("seccion");

                    Curso c = new Curso(curso_id, especialidad, promocion, seccion);
                    cursos.add(c);
                }
            }
        }
        return cursos;
    }

    /** 
     * Encontrar un curso por su id
     *
     * @param id identificador unico del curso
     *
     * @return el curso con el id indicado
     *
     * @throws SQLException cuando hay un error de comunicacion con la base de datos o una consulata mal formada
     * */
    public Curso findById(int id) throws SQLException {
        String sql = """
                SELECT c.id, nombre AS especialidad, promocion, seccion 
                FROM curso c JOIN especialidad e ON c.especialidad_id = e.id 
                WHERE c.id = ?
                """;
        Connection con = ConnectionPoolListener.getCon();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return fromResultSet(rs, con);
    }

    /** 
     * Obtener un curso a partir de un {@link ResultSet} y una {@link Connection}. Ambos
     * se cierran antes de retornar el curso
     *
     * @param rs el {@link ResultSet} de la consulta
     * @param con la {@link Connection} de la base de datos
     *
     * @return el curso obtenido
     *
     * @throws SQLException cuando hay un error de comunicacion con la base de datos o una consulata mal formada
     * */
    public Curso fromResultSet(ResultSet rs, Connection con) throws SQLException {
        if (rs.next()) {
            int curso_id = rs.getInt("id");
            String especialidad = rs.getString("especialidad");
            int promocion = rs.getInt("promocion");
            String seccion = rs.getString("seccion");

            Curso c = new Curso(curso_id, especialidad, promocion, seccion);
            return c;
        } else {
            return null;
        }
    }
}
