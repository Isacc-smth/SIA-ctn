/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.dao;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.model.Instrumento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mariadb.jdbc.Statement;

/**
 * Clase auxiliar para obtener los instrumentos evaluativos
 *
 * @author jonat, Isacc-smth
 */

public class InstrumentoDao {

    /**
     * Obtener todos los instrumentos ordenados por nombre
     *
     * @return un ArrayList con todos los instrumentos evaluativos disponibles
     *
     * @throws SQLException cuando hay errores con la conexion o la consulta
     */
    public List<Instrumento> findAll() throws SQLException {
        List<Instrumento> list = new ArrayList<>();
        String sql = "SELECT id, nombre FROM instrumento ORDER BY nombre";

        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stm = con.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Instrumento ins = new Instrumento();
                ins.setId(rs.getInt("id"));
                ins.setNombre(rs.getString("nombre"));
                list.add(ins);
            }
        }
        return list;
    }
}
