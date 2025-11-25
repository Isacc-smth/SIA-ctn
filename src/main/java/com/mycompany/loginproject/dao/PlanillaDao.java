package com.mycompany.loginproject.dao;

import com.mycompany.loginproject.clases.ConnectionPoolListener;
import com.mycompany.loginproject.model.Planilla;
import com.mycompany.loginproject.model.Tarea;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para operaciones relacionadas con planillas
 *
 * @author jonat, Isacc-smth
 */
public class PlanillaDao {

    // Esta clase se usa solamente para el menu de inicio (el que es para elegir las
    // planillas)
    /**
     * Obtener todas las planillas relacionadas con un profesor (usuario) de un
     * curso dado.
     *
     * @param userId     identificador del profesor
     * @param cursoId    identificador del curso
     * @param etapaIndex Indice de la etapa de la que consultar
     *
     * @return un ArrayList con todas las planillas relacionadas a un profesor
     */
    public ArrayList<Planilla> consultarPlanillas(int userId, int cursoId, int etapaIndex) throws SQLException {
        String sql = """
                    SELECT p.id, nombre, curso_id, materia_id, periodo, etapa, profesor_id, COUNT(DISTINCT t.id) AS tareas_count
                    FROM planilla p
                    JOIN materia m ON p.materia_id = m.id
                    LEFT JOIN tarea t ON t.planilla_id = p.id
                    WHERE curso_id = ? AND profesor_id = ? AND etapa = ? AND periodo = 2025
                    GROUP BY p.id, m.nombre, p.curso_id, p.materia_id, p.periodo, p.etapa, p.profesor_id
                """;
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, cursoId);
            stm.setInt(2, userId);
            stm.setInt(3, etapaIndex);
            try (ResultSet rs = stm.executeQuery()) {
                ArrayList<Planilla> planillas = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int curso_id = rs.getInt("curso_id");
                    int materia_id = rs.getInt("materia_id");
                    String nombre = rs.getString("nombre");
                    int periodo = rs.getInt("periodo");
                    String etapa = rs.getString("etapa");
                    int profesor_id = rs.getInt("profesor_id");
                    int tareas_count = rs.getInt("tareas_count");

                    String ultimaTarea = consultarUltimaTarea(id);
                    Planilla p = new Planilla(id, curso_id, materia_id, nombre, periodo, etapa, profesor_id, tareas_count,
                            ultimaTarea);
                    planillas.add(p);
                }
                return planillas;
            }
        }
    }

    /**
     * Consultar todas las planillas de todos los cursos relacionados a un profesor
     * dado
     *
     * @param userId     id del profesor (usuario)
     * @param etapaIndex indice de la etapa a consultar
     */
    public ArrayList<Planilla> consultarPlanillasUser(int userId, int etapaIndex) throws SQLException {
        String sql = """
                SELECT p.id, nombre, curso_id, materia_id, categoria, periodo, etapa, profesor_id
                FROM planilla p
                JOIN materia m ON p.materia_id = m.id
                WHERE profesor_id = ? AND etapa = ? AND periodo = 2025
                GROUP BY p.id, m.nombre, p.curso_id, p.materia_id, p.periodo, p.etapa, p.profesor_id
                """;
        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, userId);
            stm.setInt(2, etapaIndex);
            try (ResultSet rs = stm.executeQuery()) {
                ArrayList<Planilla> planillas = new ArrayList<>();
                while (rs.next()) {
                    int planilla_id = rs.getInt("id");
                    int curso_id = rs.getInt("curso_id");
                    int materia_id = rs.getInt("materia_id");
                    String categoria = rs.getString("categoria");
                    String nombre = rs.getString("nombre");
                    int periodo = rs.getInt("periodo");
                    String etapa = rs.getString("etapa");
                    int profesor_id = rs.getInt("profesor_id");

                    Planilla p = new Planilla(planilla_id, curso_id, materia_id, categoria, nombre, periodo, etapa, profesor_id);
                    planillas.add(p);
                }
                return planillas;
            }
        }
    }

    /** 
     * Encontrar una planilla por su id
     *
     * @param id identificador de una planilla
     *
     * @throws SQLException si hay un error en la conexion o consulta
     * */
    public Planilla findById(int id) throws SQLException { // could create an interface
        String sql = "SELECT p.id, nombre, curso_id, materia_id, categoria, periodo, etapa, profesor_id "
                + "FROM planilla p JOIN materia m ON p.materia_id = m.id "
                + "WHERE p.id = ?";
        Connection con = ConnectionPoolListener.getCon();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return fromResultSet(rs, ps, con);
    }

    /**
     * Filtrar una planilla por su clave compuesta: cursoId, materiaId y etapa.
     *
     * @param cursoId   ID del curso asociado a la planilla
     * @param materiaId ID de la materia asociada a la planilla
     * @param etapa     Etapa de la planilla
     *
     * @return Una instancia de Planilla que coincide con la clave compuesta, o null
     *         si no se encuentra ninguna
     *
     * @see com.mycompany.loginproject.model.Planilla
     */
    public Planilla findByCompositeKey(int cursoId, int materiaId, int etapa) throws SQLException {
        String sql = "SELECT p.id, nombre, curso_id, materia_id, categoria, periodo, etapa, profesor_id "
                + "FROM planilla p JOIN materia m ON p.materia_id = m.id "
                + "WHERE curso_id = ? AND materia_id = ? AND periodo = 2025 AND etapa = ?";// TODO add periodo
                                                                                           // functionality
        try (Connection con = ConnectionPoolListener.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cursoId);
            ps.setInt(2, materiaId);
            ps.setInt(3, etapa);
            try (ResultSet rs = ps.executeQuery()) {
                return fromResultSet(rs, ps, con);
            }
        }
    }

    /**
     * Consultar la última tarea de una planilla. Solamente
     * utilizado para mostrar el título de la última tarea en HomeServlet.
     *
     * @param planillaId ID de la planilla para la cual se desea obtener la última
     *                   tarea
     *
     * @see com.mycompany.loginproject.HomeServlet
     */
    public String consultarUltimaTarea(int planillaId) throws SQLException {
        String sql = "SELECT * FROM tarea WHERE planilla_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection con = ConnectionPoolListener.getCon(); PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, planillaId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString("titulo");
            }
            return "";
        }
    }

    /**
     * Obtener una planilla desde un ResultSet. Ambos parametros se cierran antes de retornar
     * 
     * @param rs ResultSet que contiene los datos de la planilla extraidos de una
     *           consulta
     * @param con la conexion a la base de datos
     *
     * @return Una instancia de Planilla con los datos extraidos del ResultSet, o
     *         null si no hay datos
     */
    public Planilla fromResultSet(ResultSet rs, PreparedStatement stm, Connection con) throws SQLException {
        if (rs.next()) {
            int planilla_id = rs.getInt("id");
            int curso_id = rs.getInt("curso_id");
            int materia_id = rs.getInt("materia_id");
            String categoria = rs.getString("categoria");
            String nombre = rs.getString("nombre");
            int periodo = rs.getInt("periodo");
            String etapa = rs.getString("etapa");// NOTE might cause problems in the future
            int profesor_id = rs.getInt("profesor_id");

            Planilla p = new Planilla(planilla_id, curso_id, materia_id, categoria, nombre, periodo, etapa,
                    profesor_id);
            rs.close();
            con.close();
            return p;
        } else {
            return null;
        }
    }

    /**
     * Clase interna para contener la planilla junto con el nombre de la materia.
     */
    public static class PlanillaInfo {

        private final Planilla planilla;
        private final String materiaNombre;

        /**
         * Constructor de PlanillaInfo
         *
         * @param planilla      representa la planilla asociada
         * @param materiaNombre representa el nombre de la materia asociada
         */
        public PlanillaInfo(Planilla planilla, String materiaNombre) {
            this.planilla = planilla;
            this.materiaNombre = materiaNombre;
        }

        public Planilla getPlanilla() {
            return planilla;
        }

        public String getMateriaNombre() {
            return materiaNombre;
        }
    }

    /** 
     * Obtener las planillas de un curso
     *
     * @param especialidadId identificador de la especialidad
     * @param promocion      la promocion del curso, el numero de curso se calcula automaticamente
     * @param periodo        el periodo del que se desea consultar (sin usar)
     *
     * @return una lista de PlanillaInfo con todas las planillas asociadas a un curso
     * */
    public List<PlanillaInfo> findPlanillasByCourse(int especialidadId, int promocion, String seccion, int periodo)
            throws SQLException, ClassNotFoundException {
        List<PlanillaInfo> out = new ArrayList<>();

        // SQL: join planilla -> curso -> materia
        String sql = """
                SELECT p.id AS planilla_id, p.curso_id, p.materia_id, p.periodo, p.etapa, p.profesor_id, m.nombre AS materia_nombre
                FROM planilla p
                JOIN curso c ON p.curso_id = c.id
                JOIN materia m ON p.materia_id = m.id
                WHERE c.especialidad_id = ? AND c.promocion = ? AND c.seccion = ? AND p.periodo = ?
                ORDER BY m.nombre;
                """;

        try (PreparedStatement ps = ConnectionPoolListener.getCon().prepareStatement(sql)) {

            ps.setInt(1, especialidadId);
            ps.setInt(2, promocion);
            ps.setString(3, seccion);
            ps.setInt(4, periodo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Planilla p = new Planilla();
                    p.setId(rs.getInt("planilla_id"));
                    p.setCursoId(rs.getInt("curso_id"));
                    p.setMateriaId(rs.getInt("materia_id"));
                    p.setPeriodo(rs.getInt("periodo"));
                    p.setEtapa(rs.getString("etapa"));
                    p.setProfesorId(rs.getInt("profesor_id"));
                    String materiaNombre = rs.getString("materia_nombre");
                    out.add(new PlanillaInfo(p, materiaNombre));
                }
            }
        }
        return out;
    }

    // optional: convenience to load planilla + tareas in one call
    // public Planilla findByIdWithTareas(int id) throws SQLException {
    // Planilla p = findById(id);
    // if (p != null) {
    // TareaDao tdao = new TareaDao();
    // p.setTareas(tdao.findByPlanillaId(id));
    // }
    // return p;
    // }
}
