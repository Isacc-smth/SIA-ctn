/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.dao;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase para gestionar operaciones relacionadas con los usuarios (profesores) de la base de datos
 *
 * @author jonat
 */
public class UserDao {

    /**
     * Obtener un usuario con un nombre y contraseña dadas
     * 
     * @param username nombre del usuario
     * @param password contraseña del usuario
     *
     * @return una clase User con sus datos si el usuario está en la base de datos, null si no se lo encuenta
     * */
    public User findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM profesor WHERE usuario = ? AND contrasenia = SHA2(?, 256)";
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, username);
            stm.setString(2, password);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String user = rs.getString("usuario");
                    String fullName = rs.getString("nombre") + " " + rs.getString("apellido");
                    int level = rs.getInt("nivel");
                    return new User(id, user, fullName, level);
                }
                return null;
            }
        }
    }
}
