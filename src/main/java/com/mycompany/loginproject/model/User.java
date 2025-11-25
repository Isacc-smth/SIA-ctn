/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.model;

/**
 * Clase para el modelado de los usuarios (profesores)
 *
 * @author jonat, Isacc-smth
 */
public class User {

    private int id;
    private String username;
    private String fullName;
    private int level;

    /**
     * Constructor para usuarios
     *
     * @param id        identificador unico del usuario (profesor en la DB)
     * @param username  nombre del usuario
     * @param fullName  nombre completo del profesor
     * @param level     nivel de privilegios del usuario (admin o normal)
     * */
    public User(int id, String username, String fullName, int level) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.level = level;
    }

    // getters/setters
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public int getLevel() {
        return level;
    }

}
