/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Clase para obtener una vista con los alumnos y sus puntajes
 *
 * @author jonat, Isacc-smth
 */
public class StudentRow {
    private int registroId; // registro.id
    private int alumnoId;
    private String alumnoNombre;
    private Map<Integer, Integer> grades = new HashMap<>(); // tareaId -> puntos
    private int total; // sum of puntos
    private int porcentaje; // rounded percentage of totalPossiblePoints
    private int nota; // final grade (can be different from porcentaje)
    private Map<Integer, String> titles = new HashMap<>(); // tareaId -> titulos
    private Map<Integer, Integer> totals = new HashMap<>(); // tareaId -> total

    private String correoTutor1;
    private String correoTutor2;

    /** 
     * Constructor de StudentRow
     *
     * @param registroId    identificador del registro (tabla intermedia entre planilla y alumno).
     * @param alumnoId      identificador del alumno.
     * @param alumnoNombre  nombre del alumno.
     * */
    public StudentRow(int registroId, int alumnoId, String alumnoNombre) {
        this.registroId = registroId;
        this.alumnoId = alumnoId;
        this.alumnoNombre = alumnoNombre;
    }

    public Map<Integer, String> getTitles() {
        return titles;
    }

    public void setTitles(Map<Integer, String> titles) {
        this.titles = titles;
    }

    public StudentRow() {
    }


    public int getRegistroId() {
        return registroId;
    }

    public void setRegistroId(int registroId) {
        this.registroId = registroId;
    }

    public int getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(int alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    public Map<Integer, Integer> getGrades() {
        return grades;
    }

    public void setGrades(Map<Integer, Integer> grades) {
        this.grades = grades;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof StudentRow))
            return false;
        StudentRow that = (StudentRow) o;
        return registroId == that.registroId &&
                alumnoId == that.alumnoId &&
                Objects.equals(alumnoNombre, that.alumnoNombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registroId, alumnoId, alumnoNombre);
    }

    @Override
    public String toString() {
        // In order to have go
        return "StudentRow{" +
                "registroId=" + registroId +
                ", alumnoId=" + alumnoId +
                ", alumnoNombre='" + alumnoNombre + '\'' +
                ", grades=" + grades +
                ", total=" + total +
                ", porcentaje=" + porcentaje +
                ", nota=" + nota +
                '}';
    }

    public String getCorreoTutor1() {
        return correoTutor1;
    }

    public void setCorreoTutor1(String correoTutor1) {
        this.correoTutor1 = correoTutor1;
    }

    public String getCorreoTutor2() {
        return correoTutor2;
    }

    public void setCorreoTutor2(String correoTutor2) {
        this.correoTutor2 = correoTutor2;
    }

    public Map<Integer, Integer> getTotals() {
        return totals;
    }
}
