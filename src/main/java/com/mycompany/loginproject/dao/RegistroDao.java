/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.dao;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.clases.conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Clase para la gestion de opearaciones relacionadas a Registros (tabla intermedia entre alumno, planilla)
 *
 * @author jonat, Isacc-smth
 */
public class RegistroDao extends conexion {
    /**
     * Obtener un mapa alumnoId -> registroId de una planilla
     *
     * @param planillaId Identificador de la planilla
     * @param alumnoIds  un Set con los identificadores de los alumnos
     *
     * @return un Map (alumnoId -> registroId) para mostrar en la planilla, si el alumno no tiene
     * ningun registroId asosciado, no se muestra.
     *
     * @throws SQLException cuando hay un error de comunicacion con la base de datos o una consulata mal formada
     * */
    public Map<Integer,Integer> getRegistroIdsForPlanilla(int planillaId, Set<Integer> alumnoIds) throws SQLException {
        // Retornar una mapa vacio si el conjunto de alumnos es vacio o null
        if (alumnoIds == null || alumnoIds.isEmpty()) return Collections.emptyMap();

        StringBuilder sql = new StringBuilder( "SELECT id, alumno_id FROM registro WHERE planilla_id = ? AND alumno_id IN (");
        // append placeholders
        String placeholders = alumnoIds.stream().map(x -> "?").collect(Collectors.joining(","));
        sql.append(placeholders).append(")");

        Map<Integer,Integer> result = new HashMap<>();
        Connection con = ConnectionPoolListener.getCon();
        PreparedStatement stm = con.prepareStatement(sql.toString());

        int idx = 1;
        stm.setInt(idx++, planillaId);
        for (Integer a : alumnoIds) stm.setInt(idx++, a);
        ResultSet rs = stm.executeQuery();

        while (rs.next()) {
            int registroId = rs.getInt("id");
            int alumnoId = rs.getInt("alumno_id");
            result.put(alumnoId, registroId);
        }

        rs.close();
        stm.close();
        con.close();
        return result;
    }
}
