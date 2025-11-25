/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.model;

/**
 * Clase para el modelado de especialidades
 *
 * @author jonat, Isacc-smth
 */
public class Especialidad {

    private int id;
    private String nombre;

    public Especialidad() {}

    /** 
     * Constructor de Especialidad
     *
     * @param id     identificador unico de la especialidad
     * @param nombre de la especialidad
     * */
    public Especialidad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
