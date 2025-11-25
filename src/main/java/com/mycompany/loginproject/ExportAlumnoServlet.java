package com.mycompany.loginproject;

// TODO: Add all grades and instruments in a bigger sheet
// I need to somehow get all the tasks (YIKES)

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.mycompany.loginproject.dao.MateriaDao;
import com.mycompany.loginproject.dao.StudentRowDao;
import com.mycompany.loginproject.model.StudentRow;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ExportAlumnoServlet")
public class ExportAlumnoServlet extends HttpServlet {
    public static final Font BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String registroIdParam = request.getParameter("registroId");
        String tareaIdParam = request.getParameter("tareaId");
        String tareaTotalParam = request.getParameter("tareaTotal");
        String tareaTitulo = request.getParameter("titulo");

        if (registroIdParam == null || registroIdParam.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter: registroId");
            return;
        }

        if (tareaIdParam == null || tareaIdParam.isBlank() || tareaTotalParam == null || tareaTotalParam.isBlank()) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST, 
                "A required parameter is missing (required: regristroId, tareaId, tareaTotal, titulo)"
            );
        }

        int registroId;
        Integer tareaId = null;
        Integer tareaTotal = null;
        try {
            registroId = Integer.parseInt(registroIdParam);
            tareaId = Integer.parseInt(tareaIdParam);
            tareaTotal = Integer.parseInt(tareaTotalParam);
        } catch (NumberFormatException nfe) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid numeric parameter(s)");
            return;
        }

        StudentRowDao dao = new StudentRowDao();
        try {
            StudentRow row = dao.loadSingleRowByRegistroId(registroId);
            if (row == null) {
                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "No record found for the given registroId"
                );
                return;
            }

            response.setContentType("application/pdf");
            response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + row.getAlumnoNombre() + registroId + ".pdf\""
            );
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            OutputStream out = response.getOutputStream();
            generatePdf(
                row, 
                registroId,
                tareaId,
                tareaTitulo,
                tareaTotal, 
                out
            );

        } catch (SQLException sqle) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB Query failure: " + sqle.getMessage());
        } catch (DocumentException de) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "PDF generation failed: " + de.getMessage());
        }
    }

    private static void buildPdfContent(Document document, int registroId, StudentRow row, Integer idTarea, String tituloTarea,
        Integer totalTarea)
            throws DocumentException, SQLException {

        String[] materiaSemester = MateriaDao.getNameAndSemester(registroId);
        Paragraph studentName = new Paragraph(row.getAlumnoNombre(), BOLD); 
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
        table.setSpacingBefore(20f);

        String[] headers = { "Instrumento Evaluativo", "Total de Puntos", "Puntos Logrados", "Estado" };
        ArrayList<String> contents = new ArrayList<String>();
        contents.add(tituloTarea);
        contents.add(String.valueOf(totalTarea));

        if (idTarea != null && totalTarea != null) {
            contents.add(String.valueOf(row.getGrades().get(idTarea)));
            contents.add(row.getGrades().get(idTarea) >= totalTarea * 0.7 ? "Logrado" : "No Logrado");
        }


        for (String header : headers) {
            Phrase phrase = new Phrase(header, BOLD);
            PdfPCell cell = new PdfPCell(phrase);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        for (String content : contents) {
            table.addCell(new PdfPCell(new Phrase(content)));
        }

        table.setSpacingBefore(20f);
        table.setSpacingAfter(20f);
        document.add(table);
        Paragraph sign = new Paragraph("Firma del Tutor/a");
        sign.setSpacingAfter(20f);
        Paragraph line = new Paragraph("_____________");
        document.add(line);
        document.add(sign);
    }

    public static void generatePdf(StudentRow row, int registroId, Integer idTarea, String tituloTarea,
        Integer totalTarea, OutputStream out)
            throws DocumentException, SQLException {

        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();
        buildPdfContent(document, registroId ,row, idTarea, tituloTarea, totalTarea);
        document.close();
    }

    public static byte[] generatePdf(StudentRow row, int registroId, Integer idTarea, String tituloTarea, 
        Integer totalTarea)
            throws DocumentException, SQLException {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            buildPdfContent(document, registroId, row, idTarea, tituloTarea, totalTarea);
            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            // Shouldn't occur with ByteArrayOutputStream
            throw new DocumentException("Error generating PDF: " + e.getMessage());
        }
    }
}
