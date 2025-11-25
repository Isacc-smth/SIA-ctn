package com.mycompany.loginproject.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.model.StudentRow;

public class StudentRowWithTasksDao {
    public static ArrayList<StudentRow> loadSingleRowFromAllTasks(int registroId) throws SQLException {
        ArrayList<StudentRow> rows = new ArrayList<>();
        String sql = """
                SELECT a.id AS alumno_id, a.nombre, a.apellido, p.tarea_id, t.titulo, t.total, p.puntos
                FROM registro r
                JOIN alumno a ON r.alumno_id = a.id
                LEFT JOIN puntaje p ON p.registro_id = r.id
                JOIN tarea t ON t.id = p.tarea_id
                WHERE r.id = ?
                ORDER BY p.tarea_id;
                """;

        Connection con = ConnectionPoolListener.getCon();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, registroId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            StudentRow row = new StudentRow();
            row.setAlumnoId(rs.getInt("alumno_id"));
            row.setAlumnoNombre(rs.getString("apellido") + ", " + rs.getString("nombre"));
            row.setRegistroId(registroId);
            row.getTitles().put(rs.getInt("tarea_id"), rs.getString("titulo"));
            row.getTotals().put(rs.getInt("tarea_id"), rs.getInt("total"));
            row.getGrades().put(rs.getInt("tarea_id"), rs.getInt("puntos"));
            rows.add(row);
        }

        // Aca no calculamos los totales

        rs.close();
        ps.close();
        con.close();
        return rows;
    }
}
