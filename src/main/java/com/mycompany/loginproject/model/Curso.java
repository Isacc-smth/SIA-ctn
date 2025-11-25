/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.model;

import java.time.Year;

/**
 * Clase para el modelado de curso
 *
 * @author jonat, Isacc-smth
 */
public class Curso {

    private int id;
    private String especialidad;
    private int promocion;
    private String seccion;

    // Para calcluar el año actual
    private int period = Year.now().getValue(); 

    /**
     * Constructor de curso
     *
     * @param id            identificador unico del curso
     * @param especialidad  a la que corresponde
     * @param promocion     promocion del curso
     * @param seccion       la seccion a la que corresponde el curso
     * */
    public Curso(int id, String especialidad, int promocion, String seccion) {
        this.id = id;
        this.especialidad = especialidad;
        this.promocion = promocion;
        this.seccion = seccion;
    }

    public int getCurso() {
        return period - promocion + 3;
    }
    
    /** 
     * Obtener una cadena referente al curso. Se calcula en base a la promocion y el año actual
     *
     * @return 
     * */
    public String getCursoOrdinal() {
        int cursoInt = period - promocion + 3;
        return switch (cursoInt) {
            case 1 -> "Primero";
            case 2 -> "Segundo";
            case 3 -> "Tercero";
            default -> "Desconocido";
        };
    }

    /**
     * Obtener un nombre estandar para los cursos
     * 
     * @return el formato es "Especialidad {@link #getCursoOrdinal()} Seccion: seccion"
     * */
    @Override
    public String toString() {
        return especialidad + " " + getCursoOrdinal() + " Sección: " + seccion;
    }

    public int getId() {
        return id;
    }

    // getters
    public String getEspecialidad() {
        return especialidad;
    }

    public int getPromocion() {
        return promocion;
    }

    public String getSeccion() {
        return seccion;
    }

    public int getPeriod() {
        return period;
    }

}
