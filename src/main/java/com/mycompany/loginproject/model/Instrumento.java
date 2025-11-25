package com.mycompany.loginproject.model;

/**
 * Clase para el modelado de instrumentos evaluativos
 *
 * @author jonat, Isacc-smth
 */

public class Instrumento {
    private int id;
    private String nombre;

    public Instrumento() {}

    /** 
     * Constructor de Instrumento. Esta entidad se usa para 
     * clasificar las tareas de los examenes, trabajos practicos, etc.
     *
     * @param id     identificador del instrumento evaluativo
     * @param nombre nombre del instrumento
     * */
    public Instrumento(int id, String nombre) {
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
}
