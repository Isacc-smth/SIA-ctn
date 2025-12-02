package com.mycompany.loginproject.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mycompany.loginproject.clases.ConnectionPoolListener;

import java.sql.ResultSet;

/** 
 * Clase para el acceso a la tabla materia de la DB
 *
 * @author Isacc-smth
 * */
public class MateriaDao {


    /**
     * Obtener el nombre y semestre (etapa) de una materia dado un registro Id
     *
     * @param registroId identificador unico del registro
     *
     * @return un array de dos strings con el nombre (index 0 ) y la etapa (index 1)
     *
     * @throws SQLException si ocurre algún error en la conexión o por consulta mal formada
     * */
    public static String[] getNameAndSemester(int registroId) throws SQLException {
        String[] tuple = new String[2];
        String sql = """
            SELECT m.nombre, p.etapa FROM ctndb.materia m JOIN
            ctndb.planilla p on m.id = p.materia_id JOIN
            ctndb.registro r on r.planilla_id = p.id
            where r.id = ?;
        """;

        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, registroId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tuple[0] = rs.getString("nombre");
                    tuple[1] = rs.getString("etapa");
                }
            }
        }
        return tuple;
    }
}
