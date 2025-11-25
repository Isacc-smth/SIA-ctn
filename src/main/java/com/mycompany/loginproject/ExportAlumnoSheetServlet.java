package com.mycompany.loginproject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.mycompany.loginproject.dao.MateriaDao;
import com.mycompany.loginproject.dao.StudentRowWithTasksDao;
import com.mycompany.loginproject.model.StudentRow;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ExportAlumnoSheetServlet")
public class ExportAlumnoSheetServlet extends HttpServlet {
    public static final Font BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String registroIdParam = request.getParameter("registroID");
        if (registroIdParam.isBlank() || registroIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter: registroID");
        }

        int registroId;
        try {
            registroId = Integer.parseInt(registroIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid parameter: " + e);
            return;
        }

        ArrayList<StudentRow> rows = new ArrayList<>();
        try {
             rows = StudentRowWithTasksDao.loadSingleRowFromAllTasks(registroId);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQL error: " + e);
        }

        if (rows.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No tasks found with that registroId: " + registroId);

        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
            rows.get(0).getAlumnoNombre() + " General" + ".pdf\""); // Alumno nombre is the same for all rows
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try (OutputStream out = response.getOutputStream()) {
            generatePdfWithAllTasks(rows, registroId, out);
        } catch (DocumentException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Pdf Generation failed: " + e.getMessage());
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQL Query error: " + e.getMessage());
        }
    }

    public static void generatePdfWithAllTasks(ArrayList<StudentRow> rows, int registroId, OutputStream out)
        throws DocumentException, SQLException {
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();
        buildPdf(document, rows, registroId);
        document.close();
    }

    private static void buildPdf(Document document, ArrayList<StudentRow> rows, int registroId) 
        throws DocumentException, SQLException {
        // Name is the same for all rows del Tutor/a
        String[] materiaSemester = MateriaDao.getNameAndSemester(registroId);
        Paragraph studentName = new Paragraph(rows.get(0).getAlumnoNombre(), BOLD); 
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        Paragraph materiaName = new Paragraph("Disciplina: " + materiaSemester[0]);
        Paragraph semester = new Paragraph("Etapa: " + materiaSemester[1]);
        semester.setSpacingAfter(20f);

        document.add(studentName);
        document.add(materiaName);
        document.add(new Paragraph("Fecha: " + date.format(new Date())));
        document.add(semester);
        document.add(new LineSeparator());

        PdfPTable table = new PdfPTable(4);
        String[] headers = { "Instrumento Evaluativo", "Total de Puntos", "Puntos Logrados", "Estado" };

        for (String header : headers) {
            Phrase phrase = new Phrase(header, BOLD);
            PdfPCell cell = new PdfPCell(phrase);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        for (StudentRow row : rows) {
            // HACK: All Maps have the same key
            for (Integer key : row.getGrades().keySet()) {
                table.addCell(row.getTitles().get(key));
                table.addCell(String.valueOf(row.getTotals().get(key)));
                table.addCell(String.valueOf(row.getGrades().get(key)));
                String logrado = row.getGrades().get(key) > row.getTotals().get(key) * 0.7 ? "Logrado" : "No Logrado";
                table.addCell(logrado);
            }
        }

        table.setSpacingBefore(20f);
        table.setSpacingAfter(20f);
        Paragraph sign = new Paragraph("Firma del Tutor/a");
        sign.setSpacingAfter(20f);
        Paragraph line = new Paragraph("_____________");

        document.add(table);
        document.add(line);
        document.add(sign);
    }
}
