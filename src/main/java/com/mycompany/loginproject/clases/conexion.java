/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jonat
 */
public class conexion {

    private String base;
    private String host;
    private String usuario;
    private String contra;
    private Connection con;

    public conexion() {
        this.base = "ctndb";
        /* name of the database */
        this.host = "NO";
        this.usuario = "NO";
        this.contra = "NO";
    }

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // fail fast if the driver is missing
            throw new ExceptionInInitializerError(e);
        }
    }

    public Connection getCon() {
        try {
            String url = "jdbc:mariadb://" + host + "/" + base;
            con = DriverManager.getConnection(url, this.usuario, this.contra);
            System.out.println("Conectado");
        } catch (SQLException ex) {
            Logger.getLogger(conexion.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("No Conectado");
        }

        return con;// FIXME redesign exception handling
    }

    public conexion(String base, String host, String usuario, String contra, Connection con) {
        this.base = base;
        this.host = host;
        this.usuario = usuario;
        this.contra = contra;
    }
}
