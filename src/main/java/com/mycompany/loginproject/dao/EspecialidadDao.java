/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.dao;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.clases.conexion;
import com.mycompany.loginproject.model.Especialidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para el acceso a la tabla especialidad de la DB
 *
 * @author jonat, Isacc-smth
 */
public class EspecialidadDao extends conexion {

    public EspecialidadDao() {
    }

    /**
     * Obtener todos las especialidades 
     *
     * @return un {@link ArrayList} de especialidades
     *
     * @throws SQLException si ocurre algún error en la conexión o por consulta mal formada
     * */
    public List<Especialidad> findAll() throws SQLException {
        String sql = "SELECT id, nombre FROM especialidad ORDER BY nombre";
        List<Especialidad> list = new ArrayList<>();

        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Especialidad e = new Especialidad(rs.getInt("id"), rs.getString("nombre"));
                list.add(e);
            }
            return list;
        }
    }

    /**
     * Encontrar una especialidad por su id
     *
     * @param id identificador unico de la especialidad
     *
     * @return la especialidad con el id indicado
     *
     * @throws SQLException si ocurre algún error en la conexión o por consulta mal formada
     * */
    public Especialidad findById(int id) throws SQLException {
        String sql = "SELECT id, nombre FROM especialidad WHERE id = ?";
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Especialidad(rs.getInt("id"), rs.getString("nombre"));
                }
                return null;
            }
        }
    }
}
