/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.loginproject.dao;

import com.mycompany.loginproject.PlanillaServlet;
import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.model.Planilla;
import com.mycompany.loginproject.model.StudentRow;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Clase para obtener una vista para la tabla de {@link com.mycompany.loginproject.PlanillaServlet}
 *
 * @author jonat, Isacc-smth
 */
public class StudentRowDao {

	// No suman al total de la planilla
    private static final int PUNTOS_CREDITO = 13;

	/** 
	 * Obtener Las filas de los alumnos para mostrar en PlanillaServlet
	 *
	 * @param planilla			  la planilla de donde se quiere obtener
	 * @param tareaMax			  un mapa con los totales de puntos de las tareas 
	 *								(id -> total de puntos)
	 * @param totalPossiblePoints el total de puntos de la planilla
	 *
	 * @return un ArrayList con las filas, listas para mostrar en PlanillaServlet
	 *
	 * @throws SQLException cuando hay un error con la conexion a la BD o la consulta no es correcta
	 *
	 * @see PlanillaServlet
	 * */
    public List<StudentRow> loadRowsForPlanilla(Planilla planilla,
            Map<Integer, Integer> tareaMax,
            int totalPossiblePoints) throws SQLException {
        List<StudentRow> rows = new ArrayList<>();
        // SQL: get registros (students) and any puntaje (left join)
        // We join registro -> alumno and left join puntaje (to get tarea_id and puntos)
        String sql = """
                SELECT r.id AS registro_id, a.id AS alumno_id, a.nombre, a.apellido, 
                a.correo_encargado, a.correo_encargado2, p.tarea_id, p.puntos
                FROM registro r
                JOIN alumno a ON r.alumno_id = a.id
                LEFT JOIN puntaje p ON p.registro_id = r.id 
                WHERE r.planilla_id = ? 
                ORDER BY a.apellido, a.nombre, r.id;
                """;
        // We'll group by registro_id
        try (PreparedStatement stm = ConnectionPoolListener.getCon().prepareStatement(sql)) {
            stm.setInt(1, planilla.getId());
            try (ResultSet rs = stm.executeQuery()) {
                Map<Integer, StudentRow> map = new LinkedHashMap<>(); // keep order
                
                while (rs.next()) {
                    int registroId = rs.getInt("registro_id");
                    StudentRow row = map.get(registroId);
                    if (row == null) {
                        row = new StudentRow();
                        row.setRegistroId(registroId);
                        row.setAlumnoId(rs.getInt("alumno_id"));
                        String nombre = rs.getString("nombre");
                        String apellido = rs.getString("apellido");
                        row.setAlumnoNombre((apellido == null ? "" : apellido) + ", " + (nombre == null ? "" : nombre));
                        row.setCorreoTutor1(rs.getString("correo_encargado"));
                        row.setCorreoTutor2(rs.getString("correo_encargado2"));
                        
                        // Initialize grades map with all tareas -> null so UI can render empty cells
                        for (Integer tareaId : tareaMax.keySet()) {
                            row.getGrades().put(tareaId, null);
                        }

                        // Como no se cargan los puntos credito, hacerlo aqui
                        row.getGrades().put(PUNTOS_CREDITO, null);
                        
                        map.put(registroId, row);
                    }

                    // tarea_id may be NULL due to LEFT JOIN; getInt + wasNull works but we'll use getObject for puntos
                    int tareaId = rs.getInt("tarea_id");
                    if (!rs.wasNull() && tareaId > 0) {
                        // use getObject to obtain an Integer that is null for SQL NULL
                        Integer puntos = rs.getObject("puntos", Integer.class);
                        // Put the puntos value (may be null) into the map
                        row.getGrades().put(tareaId, puntos);
                    }
                }

                // compute totals & percentages for each row
                for (StudentRow r : map.values()) {
                    int sum = 0;
                    for (Map.Entry<Integer, Integer> e : r.getGrades().entrySet()) {
                        Integer puntos = e.getValue();
                        if (puntos != null) {
                            sum += puntos;
                        }
                    }
                    r.setTotal(sum);
                    // compute porcentaje = round(sum * 100 / totalPossiblePoints)
                    int porcentaje = 0;
                    if (totalPossiblePoints > 0) {
                        double raw = (sum * 100.0) / totalPossiblePoints;
                        porcentaje = (int) Math.round(raw);
                    }
                    r.setPorcentaje(porcentaje);
                    
                    int nota;
                    if (planilla != null) {
                        // ensure ranges computed (caller should have called computeGradeRanges)
                        nota = planilla.getNotaForSum(sum);
                    } else {
                        // fallback (if Planilla not provided): use exigencia-based calculation or default
                        nota = porcentaje; // or previous fallback; adjust to your needs
                    }

                    r.setNota(nota);
                    rows.add(r);
                }
            }
        }
        return rows;
    }

	/** 
	 * Obtener una sola fila por su registroId
	 *
	 * @param registroId identificador del registro
	 *
	 * @return una fila única de un alumno, mismo formato que {@link #loadRowsForPlanilla(Planilla, Map, int)}
	 *
	 * @throws SQLException cuando hay un error con la conexion a la BD o la consulta no es correcta
	 * */
    public StudentRow loadSingleRowByRegistroId(int registroId) throws SQLException {
        String sql = """
                   SELECT a.id AS alumno_id, a.nombre, a.apellido, p.tarea_id, p.puntos
                   FROM registro r
                   JOIN alumno a ON r.alumno_id = a.id
                   LEFT JOIN puntaje p ON p.registro_id = r.id
                   WHERE r.id = ?
                   ORDER BY p.tarea_id;
                   """;

        try (Connection con = ConnectionPoolListener.getCon(); PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, registroId);
            try (ResultSet rs = stm.executeQuery()) {
                StudentRow row = null;
                while (rs.next()) {
                    if (row == null) {
                        row = new StudentRow();
                        row.setRegistroId(registroId);
                        row.setAlumnoId(rs.getInt("alumno_id"));
                        String nombre = rs.getString("nombre");
                        String apellido = rs.getString("apellido");
                        row.setAlumnoNombre(apellido + ", " + nombre);
                    }

                    int tareaId = rs.getInt("tarea_id");
                    if (!rs.wasNull()) {
                        Integer puntos = rs.getObject("puntos", Integer.class);
                        row.getGrades().put(tareaId, puntos);
                    }
                }

                if (row != null) {
                    int total = row.getGrades().values().stream()
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .sum();
                    row.setTotal(total);
                }
                return row;
            }
        }
    }

	/** 
	 * Obtener los nombres de los estudiantes asociados con una planilla
	 *
	 * @param planillaId el id de la planilla a la cual estan asociados los alumnos
	 *
	 * @return un ArrayList con los nombres de los alumnos
	 *
	 * @throws SQLException
	 * */
    public ArrayList<String> getStudentNames(int planillaId) throws SQLException {
        ArrayList<String> names = new ArrayList<>();
        String sql = """ 
            SELECT a.nombre, a.apellido from alumno a 
            JOIN registro r on a.id = r.alumno_id 
            JOIN planilla p ON p.id = r.planilla_id 
            WHERE p.id = ?;
            """;
        Connection con = ConnectionPoolListener.getCon();
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = null;
        try {
            stmt.setInt(1, planillaId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                String fullName = String.format("%s %s", rs.getString("apellido"), rs.getString("nombre"));
                names.add(fullName);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        }
        return names;
    }
}

