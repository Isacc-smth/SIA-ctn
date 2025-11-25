/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.loginproject;

import com.mycompany.loginproject.dao.CursoDao;
import com.mycompany.loginproject.dao.GradeDao;
import com.mycompany.loginproject.dao.PlanillaDao;
import com.mycompany.loginproject.dao.RegistroDao;
import com.mycompany.loginproject.dao.StudentRowDao;
import com.mycompany.loginproject.model.StudentRow;
import com.mycompany.loginproject.dao.TareaDao;
import com.mycompany.loginproject.model.Curso;
import com.mycompany.loginproject.model.Planilla;
import com.mycompany.loginproject.model.Tarea;
import com.mycompany.loginproject.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet para obtener y actualizar los datos de la planilla
 *
 * @author jonat, Isacc-smth
 */
@WebServlet(name = "PlanillaServlet", urlPatterns = {"/PlanillaServlet"})
public class PlanillaServlet extends HttpServlet {

    private final int PUNTOS_CREDITO = 13;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        String planillaIdStr = request.getParameter("planillaId");
        String cursoIdStr = request.getParameter("cursoId");
        String materiaIdStr = request.getParameter("materiaId");
        String etapaStr = request.getParameter("etapa");
        int etapa = 1;
        Planilla planilla = null;
        PlanillaDao dao = new PlanillaDao();

        try {
            if (etapaStr != null && !etapaStr.trim().isEmpty()) {
                etapa = Integer.parseInt(etapaStr.trim());
            }

            if (cursoIdStr != null && materiaIdStr != null && !cursoIdStr.isEmpty() && !materiaIdStr.isEmpty()) {

                int cursoId = Integer.parseInt(cursoIdStr.trim());
                int materiaId = Integer.parseInt(materiaIdStr.trim());
                planilla = dao.findByCompositeKey(cursoId, materiaId, etapa);

            } else if (planillaIdStr != null && !planillaIdStr.trim().isEmpty()) {
                int planillaId = Integer.parseInt(planillaIdStr.trim());
                planilla = dao.findById(planillaId);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing planilla identifier");
                return;
            }

            if (planilla == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Planilla not found");
                return;
            }

            if (planilla.getProfesorId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Curso curso = new CursoDao().findById(planilla.getCursoId());

            List<Tarea> tareas = new TareaDao().consultarTarea(planilla.getId());
            request.setAttribute("tareas", tareas);
            // preserve curso/materia/etapa for UI convenience
            request.setAttribute("curso", curso);
            request.setAttribute("cursoId", planilla.getCursoId());
            request.setAttribute("materiaId", planilla.getMateriaId());
            request.setAttribute("etapa", planilla.getEtapaIndex());

            Map<Integer, Integer> tareaMax = new HashMap<>();
            int totalPossiblePoints = 0;
            for (Tarea t : tareas) {
                if (t.getInstrumentoId() != PUNTOS_CREDITO) {
                    tareaMax.put(t.getId(), t.getTotal());
                    totalPossiblePoints += t.getTotal();
                }
            }
            request.setAttribute("totalPossiblePoints", totalPossiblePoints); // you can use this in the JSP
            planilla.computeGradeRanges(totalPossiblePoints);

            // Now load registros + puntajes and build rows
            List<StudentRow> rows = new StudentRowDao().loadRowsForPlanilla(planilla, tareaMax, totalPossiblePoints);
            Map<String, Integer[]> gradeRanges = planilla.getGradeRangesForJsp();
            
            int exigencia = (int) Math.round(100 * planilla.getExigencia());
            request.setAttribute("exigencia", exigencia);
            
            request.setAttribute("gradeRanges", gradeRanges);
            request.setAttribute("planilla", planilla);
            request.setAttribute("rows", rows);

            request.getRequestDispatcher("/Planilla.jsp").forward(request, response);
        } catch (NumberFormatException nfe) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid numeric parameter");
        } catch (SQLException sqle) {
            log("Error loading planilla for user " + user.getId() + " params: planillaId=" + planillaIdStr
                    + " cursoId=" + cursoIdStr + " materiaId=" + materiaIdStr, sqle);
            throw new ServletException("Unable to load planilla", sqle);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        // Accept planilla by planillaId or composite key (same logic as doGet)
        String planillaIdStr = request.getParameter("planillaId");
        String cursoIdStr = request.getParameter("cursoId");
        String materiaIdStr = request.getParameter("materiaId");
        String etapaStr = request.getParameter("etapa");
        int etapa = 1;
        Planilla planilla = null;
        PlanillaDao dao = new PlanillaDao();

        try {
            if (etapaStr != null && !etapaStr.trim().isEmpty()) {
                etapa = Integer.parseInt(etapaStr.trim());
            }

            if (cursoIdStr != null && materiaIdStr != null
                    && !cursoIdStr.isEmpty() && !materiaIdStr.isEmpty()) {

                int cursoId = Integer.parseInt(cursoIdStr.trim());
                int materiaId = Integer.parseInt(materiaIdStr.trim());
                planilla = dao.findByCompositeKey(cursoId, materiaId, etapa);

            } else if (planillaIdStr != null && !planillaIdStr.trim().isEmpty()) {
                int planillaId = Integer.parseInt(planillaIdStr.trim());
                planilla = dao.findById(planillaId);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing planilla identifier");
                return;

            }
            if (planilla == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Planilla not found");
                return;
            }

            if (planilla.getProfesorId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Load tareas to know bounds per tarea
            List<Tarea> tareas = new TareaDao().consultarTarea(planilla.getId());
            Map<Integer, Integer> tareaMax = new HashMap<>();
            for (Tarea t : tareas) {
                if (t.getInstrumentoId() != PUNTOS_CREDITO) {
                    tareaMax.put(t.getId(), t.getTotal());
                }
            }

            Map<Integer, Map<Integer, Integer>> gradesByAlumno = new HashMap<>();
            Pattern p = Pattern.compile("^grade_(\\d+)_(\\d+)$");
            Enumeration<String> names = request.getParameterNames();
            List<String> paramErrors = new ArrayList<>();

            while (names.hasMoreElements()) {
                String name = names.nextElement();
                Matcher m = p.matcher(name);
                if (!m.matches()) {
                    continue;
                }
                int alumnoId = Integer.parseInt(m.group(1));
                int tareaId = Integer.parseInt(m.group(2));
                String raw = request.getParameter(name);

                // If the parameter is not present at all (null), skip it.
                // If it's present but empty/whitespace => treat as explicit NULL (user cleared the field).
                if (raw == null) {
                    // parameter not present in form (shouldn't normally happen), skip
                    continue;
                }

                Integer puntos = null;
                String trimmed = raw.trim();
                if (trimmed.isEmpty()) {
                    // explicit null (user cleared field) -> keep puntos == null
                } else {
                    // try parse numeric value
                    try {
                        puntos = Integer.parseInt(trimmed);
                    } catch (NumberFormatException nfe) {
                        paramErrors.add("Valor no numérico para alumno " + alumnoId + " tarea " + tareaId);
                        // skip this entry (don't save it)
                        continue;
                    }

                    // validate bounds if tarea known
                    Integer max = tareaMax.get(tareaId);
                    if (max != null) {
                        if (puntos < 0) {
                            puntos = 0;
                        }
                        if (puntos > max) {
                            paramErrors.add("Puntos ajustados a máximo (" + max + ") para alumno " + alumnoId + " tarea " + tareaId);
                            puntos = max;
                        }
                    }
                }

                // store the value (may be null)
                gradesByAlumno.computeIfAbsent(alumnoId, k -> new HashMap<>())
                        .put(tareaId, puntos);
            }

            // If no grade_* parameters were present at all (no inputs), nothing to save
            if (gradesByAlumno.isEmpty()) {
                session.setAttribute("flashMessage", "No hay calificaciones para guardar.");
                response.sendRedirect(request.getContextPath() + "/PlanillaServlet?planillaId=" + planilla.getId());
                return;
            }
            // Map alumnoId -> registro_id for this planilla (only for alumnos we have grades for)
            Set<Integer> alumnoIds = gradesByAlumno.keySet();
            RegistroDao registroDao = new RegistroDao();
            Map<Integer, Integer> alumnoToRegistro = registroDao.getRegistroIdsForPlanilla(planilla.getId(), alumnoIds);

            // Build grades keyed by registro_id (puntaje table uses registro_id)
            Map<Integer, Map<Integer, Integer>> gradesByRegistro = new HashMap<>();
            for (Map.Entry<Integer, Map<Integer, Integer>> e : gradesByAlumno.entrySet()) {
                int alumnoId = e.getKey();
                Integer registroId = alumnoToRegistro.get(alumnoId);
                if (registroId == null) {
                    // No registro found for that alumno in this planilla: warn and skip
                    paramErrors.add("Alumno " + alumnoId + " no está registrado en esta planilla; calificación omitida.");
                    continue;
                }
                gradesByRegistro.put(registroId, e.getValue());
            }

            if (gradesByRegistro.isEmpty()) {
                session.setAttribute("flashMessage", "No se encontraron registros válidos para guardar (ver advertencias).");
                session.setAttribute("flashErrors", paramErrors);
                response.sendRedirect(request.getContextPath() + "/PlanillaServlet?planillaId=" + planilla.getId());
                return;
            }

            // Persist in a single transaction using GradeDao (puntaje table)
            GradeDao gradeDao = new GradeDao();
            try {
                gradeDao.saveGradesBatch(planilla.getId(), gradesByRegistro);
            } catch (SQLException sqle) {
                log("Error saving grades for planilla " + planilla.getId(), sqle);
                throw new ServletException("Error saving calificaciones", sqle);
            }

            // optionally recalculate totals/percentages server-side (if you persist them)
            // planillaDao.recalculateTotals(planilla.getId()); // implement if needed
            // Build flash message
            String msg = "Cambios guardados correctamente.";
            if (!paramErrors.isEmpty()) {
                session.setAttribute("flashErrors", paramErrors);
                msg += " (con advertencias)";
            }
            session.setAttribute("flashMessage", msg);

            // PRG redirect back to GET view
            response.sendRedirect(request.getContextPath() + "/PlanillaServlet?planillaId=" + planilla.getId());
        } catch (NumberFormatException nfe) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid numeric parameter");
        } catch (SQLException sqle) {
            log("Database error in PlanillaServlet.doPost", sqle);
            throw new ServletException("Database error", sqle);
        }
    }
}
