/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.loginproject.model;

import java.time.LocalDate;

/**
 * Clase para el modelado de tareas
 *
 * @author jonat, Isacc-smth
 */
public class Tarea {

    private int id;
    private int planillaId;
    private int instrumentoId;
    private LocalDate fecha;
    private int total;
    private String titulo;

    public Tarea() {}
    
    /** 
     * Constructor de Tarea
     *
     * @param id          identificador de id
     * @param planillaId  identificador de la planilla
     * @param fecha       fecha de la tarea
     * @param total       el total de puntos de la tarea
     * @param titulo      el titulo/nombre de la tarea
     * */
    public Tarea(int id, int planillaId, int instrumentoId, LocalDate fecha, int total, String titulo) {
        this.id = id;
        this.planillaId = planillaId;
        this.instrumentoId = instrumentoId;
        this.fecha = fecha;
        this.total = total;
        this.titulo = titulo;
    }

    // getters
    public int getId() {
        return id;
    }

    public int getPlanillaId() {
        return planillaId;
    }

    public int getInstrumentoId() {
        return instrumentoId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getTotal() {
        return total;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlanillaId(int planillaId) {
        this.planillaId = planillaId;
    }

    public void setInstrumentoId(int instrumentoId) {
        this.instrumentoId = instrumentoId;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    
}
