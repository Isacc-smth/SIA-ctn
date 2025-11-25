package com.mycompany.loginproject;

import com.mycompany.loginproject.clases.FinalGradeSheet;
import com.mycompany.loginproject.dao.EspecialidadDao;
import com.mycompany.loginproject.dao.PlanillaDao;
import com.mycompany.loginproject.dao.PlanillaDao.PlanillaInfo;
import com.mycompany.loginproject.dao.StudentRowDao;
import com.mycompany.loginproject.dao.TareaDao;
import com.mycompany.loginproject.model.Planilla;
import com.mycompany.loginproject.model.StudentRow;
import com.mycompany.loginproject.model.Tarea;
import com.mycompany.loginproject.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Servlet para exportar todas las planillas de un curso específico en un archivo Excel
 *
 * @author jonat, Isacc-smth
 */

@WebServlet(name = "ExportCoursePlanillasServlet", urlPatterns = {"/ExportCoursePlanillasServlet"})
public class ExportCoursePlanillasServlet extends HttpServlet {

    private static final int PUNTOS_CREDITO = 13;

    // helper: sanitize sheet name to 31 chars and remove invalid chars
    private String safeSheetName(String name, int maxLength) {
        if (name == null) name = "Sheet";
        // remove invalid characters for sheet name: : \ / ? * [ ]
        String s = name.replaceAll("[:\\\\/?*\\[\\]]", " ");
        s = s.trim();
        if (s.length() > maxLength) s = s.substring(0, maxLength);
        if (s.isEmpty()) s = "Sheet";
        return s;
    }

    /**
    * Exportar todas las planillas de un curso específico en un archivo Excel con una hoja por materia.
    *
    * @param request   utilizado para obtener los parámetros del formulario
    * @param response  utilizado para enviar la respuesta al cliente
    *
    * @throws ServletException si ocurre un error relacionado con el servlet
    * @throws IOException      si ocurre un error de Entrada/Salida
    * */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // session + admin permission check
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        // ADJUST this check to match your app's admin level
        if (user == null || user.getLevel() < 2) { // example: nivel >= 2 allowed
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        String espIdStr = request.getParameter("especialidad");
        String cursoStr = request.getParameter("curso");
        String seccion = request.getParameter("seccion");
        String periodoStr = request.getParameter("periodo");

        if (espIdStr == null || cursoStr == null || seccion == null || periodoStr == null ||
            espIdStr.isEmpty() || cursoStr.isEmpty() || seccion.isEmpty() || periodoStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters. Required: especialidad, curso, seccion, periodo");
            return;
        }

        try {
            int especialidadId = Integer.parseInt(espIdStr.trim());
            int curso = Integer.parseInt(cursoStr.trim()); // 1..3 as user enters
            int periodo = Integer.parseInt(periodoStr.trim()); // period (year)
            // compute promocion
            int promocion = periodo - curso + 3;

            PlanillaDao planillaDao = new PlanillaDao();
            // get all planillas for the course
            List<PlanillaInfo> planillas = planillaDao.findPlanillasByCourse(especialidadId, promocion, seccion, periodo);
            ArrayList<String> sheetNames = new ArrayList<>();

            if (planillas == null || planillas.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No planillas found for the selected course");
                return;
            }

            // optional: get especialidad name for nicer filename
            String especialidadName = null;
            try {
                EspecialidadDao espDao = new EspecialidadDao();
                var esp = espDao.findById(especialidadId);
                if (esp != null) especialidadName = esp.getNombre();
            } catch (Exception ex) {
                // ignore - just fallback to id in filename
            }

            // Build workbook with one sheet per planilla (subject)
            try (XSSFWorkbook wb = new XSSFWorkbook()) {
                for (PlanillaInfo pi : planillas) {
                    Planilla p = pi.getPlanilla();
                    String materiaNombre = pi.getMateriaNombre();

                    // load tareas for this planilla
                    List<Tarea> tareas = new TareaDao().consultarTarea(p.getId());
                    Map<Integer, Integer> tareaMax = new java.util.HashMap<>();
                    int totalPossiblePoints = 0;
                    for (Tarea t : tareas) {
                        // Si son puntos créditos, se omite en la suma y la escala
                        if (t.getInstrumentoId() != PUNTOS_CREDITO) {
                            tareaMax.put(t.getId(), t.getTotal());
                            totalPossiblePoints += t.getTotal();
                        }
                    }

                    // compute grade ranges for this planilla BEFORE loading student rows
                    p.computeGradeRanges(totalPossiblePoints);

                    // load student rows for this planilla
                    List<StudentRow> rows = new StudentRowDao().loadRowsForPlanilla(p, tareaMax, totalPossiblePoints);

                    String sheetName = safeSheetName(materiaNombre != null ? materiaNombre : ("Materia-" + p.getMateriaId()), 31);
                    // avoid duplicate names: if same name already exists, append index.
                    String baseName = sheetName;
                    int dup = 1;
                    while (wb.getSheet(sheetName) != null) {
                        sheetName = safeSheetName(baseName + "-" + dup, 31);
                        dup++;
                    }

                    Sheet sheet = wb.createSheet(sheetName);
                    sheetNames.add(sheetName);

                    // header
                    int col = 0;
                    Row header = sheet.createRow(0);
                    header.createCell(col++).setCellValue("#");
                    header.createCell(col++).setCellValue("Alumno");
                    header.createCell(col++).setCellValue("Total (" + totalPossiblePoints + ")");
                    header.createCell(col++).setCellValue("Porcentaje");
                    header.createCell(col++).setCellValue("Nota");
                    for (Tarea t : tareas) {
                        String tareaTitle = t.getTitulo() != null ? t.getTitulo() : "Tarea";
                        header.createCell(col++).setCellValue(tareaTitle + " (TP:" + t.getTotal() + ")");
                    }

                    // data rows
                    int r = 1;
                    for (StudentRow sr : rows) {
                        Row excelRow = sheet.createRow(r++);
                        int c = 0;
                        excelRow.createCell(c++).setCellValue(r - 1);
                        excelRow.createCell(c++).setCellValue(sr.getAlumnoNombre() != null ? sr.getAlumnoNombre() : "");
                        excelRow.createCell(c++).setCellValue(sr.getTotal());
                        excelRow.createCell(c++).setCellValue(sr.getPorcentaje());
                        excelRow.createCell(c++).setCellValue(sr.getNota());
                        Map<Integer, Integer> grades = sr.getGrades();
                        for (Tarea t : tareas) {
                            Integer val = grades != null ? grades.get(t.getId()) : null;
                            if (val != null) {
                                excelRow.createCell(c++).setCellValue(val);
                            } else {
                                excelRow.createCell(c++).setBlank();
                            }
                        }
                    }

                    // autosize a limited number of columns (avoid very expensive autosize on many columns)
                    int autosizeLimit = Math.min(8 + tareas.size(), 40);
                    for (int i = 0; i < autosizeLimit; i++) {
                        try {
                            sheet.autoSizeColumn(i);
                        } catch (Exception ignore) {}
                    }

                } // end for each planilla
                
                OutputStream out = response.getOutputStream();
                FinalGradeSheet.generate(wb, planillas, sheetNames, out);

                // prepare response filename
                String safeEspecialidad = (especialidadName != null && !especialidadName.trim().isEmpty())
                        ? especialidadName.replaceAll("[^A-Za-z0-9 _-]", "").replaceAll("\\s+", "_")
                        : ("especialidad" + especialidadId);
                String filenameBase = "Planillas_" + safeEspecialidad + "_P" + promocion + "_S" + seccion + "_PER" + periodo;
                String filename = URLEncoder.encode(filenameBase + ".xlsx", "UTF-8").replaceAll("\\+", "%20");

                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + URLEncoder.encode(filenameBase + ".xlsx", "UTF-8"));

                wb.write(out);
                out.flush();
            } // end workbook try

        } catch (NumberFormatException nfe) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid numeric parameter");
        } catch (SQLException | ClassNotFoundException ex) {
            log("DB error generating course planillas", ex);
            throw new ServletException("Database error", ex);
        } catch (Exception ex) {
            log("Error generating excel", ex);
            throw new ServletException("Unexpected error", ex);
        }
    }
}
