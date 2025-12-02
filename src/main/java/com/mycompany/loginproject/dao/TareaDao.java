/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.dao;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.model.Tarea;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Clase para la gestion de operaciones relacionadas a Tareas
 *
 * @author jonat, Isacc-smth
 */
public class TareaDao {

    /**
     * Obtener una lista de tareas de una planilla
     *
     * @param planillaId Identificador de la planilla
     *
     * @return una lista de tareas con los datos solicitados
     *
     * @throws SQLException cuando hay un error de comunicacion con la base de datos o una consulata mal formada
     * */
    public ArrayList<Tarea> consultarTarea(int planillaId) throws SQLException {
        String sql = "SELECT * FROM tarea WHERE planilla_id = ?"; // TODO change order
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, planillaId);
            try (ResultSet rs = stm.executeQuery()) {
                ArrayList<Tarea> tareas = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int planilla_id = rs.getInt("planilla_id");
                    int instrumento_id = rs.getInt("instrumento_id");
                    LocalDate fecha = rs.getObject("fecha", LocalDate.class);
                    int total = rs.getInt("total");
                    String titulo = rs.getString("titulo");

                    Tarea t = new Tarea(id, planilla_id, instrumento_id, fecha, total, titulo);
                    tareas.add(t);
                }
                return tareas;
            }
        }
    }

    /**
     * Obtener una tarea por su id
     *
     * @param id Identificador unico de la tarea
     *
     * @return una instancia de Tarea con los datos solicitados
     *
     * @throws SQLException cuando hay un error de comunicacion con la base de datos o una consulata mal formada
     * */
    public Tarea findById(int id) throws SQLException {
        String sql = "SELECT * FROM tarea WHERE id = ?";
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int planilla_id = rs.getInt("planilla_id");
                    int instrumento_id = rs.getInt("instrumento_id");
                    LocalDate fecha = rs.getObject("fecha", LocalDate.class);
                    int total = rs.getInt("total");
                    String titulo = rs.getString("titulo");
                    return new Tarea(id, planilla_id, instrumento_id, fecha, total, titulo);
                } else {
                    return null;
                }
            }
        }
    }

    /** 
     * Insertar una tarea en la base de datos
     *
     * @param tarea la tarea a insertar
     *
     * @throws SQLException cuando hay un error de comunicacion con la base de datos o una consulata mal formada
     * */
    public void insertarTarea(Tarea tarea) throws SQLException {
        String sql = "INSERT INTO tarea (planilla_id, instrumento_id, fecha, total, titulo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, tarea.getPlanillaId());
            stmt.setInt(2, tarea.getInstrumentoId());
            stmt.setDate(3, java.sql.Date.valueOf(tarea.getFecha()));
            stmt.setInt(4, tarea.getTotal());
            stmt.setString(5, tarea.getTitulo());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Inserting tarea failed, no rows affected.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    tarea.setId(keys.getInt(1));
                }
            }
        }
    }

//    public void update(Tarea tarea) throws SQLException {
//        String sql = "UPDATE tarea SET planilla_id = ?, instrumento_id = ?, fecha = ?, total = ?, titulo = ? WHERE id = ?";
//        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setInt(1, tarea.getPlanillaId());
//            ps.setInt(2, tarea.getInstrumentoId());
//            ps.setDate(3, java.sql.Date.valueOf(tarea.getFecha()));
//            ps.setInt(4, tarea.getTotal());
//            ps.setString(5, tarea.getTitulo());
//            ps.setInt(6, tarea.getId());
//            int affected = ps.executeUpdate();
//            if (affected == 0) {
//                throw new SQLException("Updating tarea failed, no rows affected.");
//            }
//        }
//    }

    /**
     * Borrar una tarea por su id
     *
     * @param id Identificador unico de la tarea
     *
     * @throws SQLException cuando hay un error de comunicacion con la base de datos o una consulata mal formada
     * */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM tarea WHERE id = ?";
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, id);
            stm.executeUpdate();
        }
    }
 
    /**
     * Modiificar los datos de una tarea
     *
     * @param tarea la tarea con los datos a modificar
     *
     * @return true si los datos se modificaron, false de lo contrario
     *
     * @throws SQLException cuando hay un error de comunicacion con la base de datos o una consulata mal formada
     * */
    public boolean update(Tarea tarea) throws SQLException {

        // No quiero limpiar esto
        String selectSql = "SELECT total FROM tarea WHERE id = ? FOR UPDATE";
        String updateSql = "UPDATE tarea SET planilla_id = ?, instrumento_id = ?, fecha = ?, total = ?, titulo = ? WHERE id = ?"; String deleteGradesSql = "DELETE FROM puntaje WHERE tarea_id = ?";
        try (Connection con = ConnectionPoolListener.getCon()) {
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                int oldTotal = -1;
                try (PreparedStatement psSel = con.prepareStatement(selectSql)) {
                    psSel.setInt(1, tarea.getId());
                    try (ResultSet rs = psSel.executeQuery()) {
                        if (rs.next()) {
                            oldTotal = rs.getInt(1);
                        } else {
                            // no such tarea -> fail (you may choose different behavior)
                            throw new SQLException("Tarea not found for id " + tarea.getId());
                        }
                    }
                }

                try (PreparedStatement psUpd = con.prepareStatement(updateSql)) {
                    psUpd.setInt(1, tarea.getPlanillaId());
                    psUpd.setInt(2, tarea.getInstrumentoId());
                    psUpd.setDate(3, java.sql.Date.valueOf(tarea.getFecha()));
                    psUpd.setInt(4, tarea.getTotal());
                    psUpd.setString(5, tarea.getTitulo());
                    psUpd.setInt(6, tarea.getId());
                    int affected = psUpd.executeUpdate();
                    if (affected == 0) {
                        throw new SQLException("Updating tarea failed, no rows affected.");
                    }
                }

                boolean cleared = false;
                if (oldTotal != tarea.getTotal()) {
                    try (PreparedStatement psDel = con.prepareStatement(deleteGradesSql)) {
                        psDel.setInt(1, tarea.getId());
                        psDel.executeUpdate();
                        cleared = true;
                    }
                }

                con.commit();
                con.setAutoCommit(oldAutoCommit);
                return cleared;
            } catch (SQLException ex) {
                try { con.rollback(); } catch (SQLException ignore) {}
                throw ex;
            }
        }
    }
}
