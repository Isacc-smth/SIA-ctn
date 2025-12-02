/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.dao;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author jonat
 */
public class GradeDao {
    /**
     * Guardar los puntajes de un registro en una planilla
     *
     * @param planillaId identificador unico de la planilla
     * @param grades     mapa de registros con sus tareas y puntajes
     */
    public void saveGradesBatch(int planillaId, Map<Integer, Map<Integer, Integer>> grades) throws SQLException {
        if (grades == null || grades.isEmpty()) return;
        String sql = "INSERT INTO puntaje (registro_id, tarea_id, puntos) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE puntos = VALUES(puntos)";
        Connection con = ConnectionPoolListener.getCon();
        PreparedStatement stm = con.prepareStatement(sql);
        try {
            con.setAutoCommit(false);
            for (Map.Entry<Integer, Map<Integer, Integer>> rEntry : grades.entrySet()) {
                int registroId = rEntry.getKey();
                for (Map.Entry<Integer, Integer> tEntry : rEntry.getValue().entrySet()) {
                    int tareaId = tEntry.getKey();
                    Integer puntos = tEntry.getValue(); // may be null

                    stm.setInt(1, registroId);
                    stm.setInt(2, tareaId);

                    if (puntos == null) {
                        stm.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        stm.setInt(3, puntos);
                    }

                    stm.addBatch();
                }
            }
            stm.executeBatch();
            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ignored) {}
            throw e;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignored) {}
            if (stm != null) stm.close();
            if (con != null) con.close();
        }
    }
}
