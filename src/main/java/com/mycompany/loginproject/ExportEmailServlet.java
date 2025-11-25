package com.mycompany.loginproject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.itextpdf.text.DocumentException;
import com.mycompany.loginproject.clases.EmailHandler;
import com.mycompany.loginproject.dao.PlanillaDao;
import com.mycompany.loginproject.dao.StudentRowDao;
import com.mycompany.loginproject.model.Planilla;
import com.mycompany.loginproject.model.StudentRow;

import jakarta.jms.Session;
import jakarta.mail.MessagingException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para gestionar envios a los correos de los encargados. Estos emails
 * van a tener pdfs
 * con los puntajes para un instrumento especifico (tareas, examenes, trabajo,
 * practico, etc)
 *
 * @author Isacc-smth
 *
 */
@WebServlet(name = "ExportEmailServlet", urlPatterns = { "/ExportEmailServlet" })
public class ExportEmailServlet extends HttpServlet {

    /**
     * Envia un email con un mensaje por defecto y un pdf adjunto
     *
     * @param request  la solicitud del cliente (el navegador) con los parametros a
     *                 validar
     * @param response la respuesta del servlet que contiene el archivo y envia
     *                 errores en caso de
     *                 que los hhaya
     *
     * @throws IOException             cuando hay errores de entrada/salida
     * @throws NumberFormatException   cuando la solicitud tiene parametros numericos invalidos
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, NumberFormatException {
        String registroIdParam = request.getParameter("registroId");
        String tareaIDParam = request.getParameter("tareaId");
        String tareaTotalParam = request.getParameter("tareaTotal");
        String tituloTarea = request.getParameter("titulo");
        String planillaIdParam = request.getParameter("planillaId");

        HttpSession session = request.getSession(); 

        if (registroIdParam.equals("") || registroIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro faltante:  registroId");
            return;
        }
        if (tareaIDParam.equals("") || tareaIDParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro faltante:  tareaID");
            return;
        }
        if (tareaTotalParam.equals("") || tareaTotalParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro faltante:  tareaTotal");
            return;
        }
        if (tituloTarea.equals("") || tituloTarea == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro faltante: tituloTarea");
            return;
        }
        if (planillaIdParam.equals("") || planillaIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro faltante: planillaId");
        }

        StudentRowDao dao = new StudentRowDao();
        try {
            int registroId = Integer.parseInt(registroIdParam);
            int idTarea = Integer.parseInt(tareaIDParam);
            int totalTarea = Integer.parseInt(tareaTotalParam);

            StudentRow row = dao.loadSingleRowByRegistroId(registroId);
            byte[] pdf = ExportAlumnoServlet.generatePdf(row, registroId, idTarea, tituloTarea, totalTarea);
            ArrayList<String> emails = EmailHandler.getEmailsById(row.getAlumnoId());

            boolean wasSent = false;
            for (String email : emails) {
                if (!email.isBlank() && email != null)
                    EmailHandler.sendEmail(email, pdf);
                wasSent = true;
            }

            if (!wasSent) {
                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No email was sent due to the student not having emails attached (I HAVE TO FIX THAT)");
                return;
            }
        } catch (SQLException e) {
            // Errores relacionados a la base de datos
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "SQL error: " + e.getMessage());
            return;
            // Errores relacionados a la generacion del pdf
        } catch (DocumentException e) {
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Document Generation error: " + e.getMessage());
            return;
            // Errores relacionados con el envio de emails
        } catch (MessagingException e) {
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred while sending the email: " + e.getMessage());
            return;
        }

        Planilla p = null;
        int planillaId = Integer.valueOf(planillaIdParam);
        try {
            p = new PlanillaDao().findById(planillaId);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error: " + e);
            return;
        }
        String email_success = "Se envio el email con éxito";
        
        if (p == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se encontro la planilla");
            return;
        }

        session.setAttribute("flashMessage", email_success);
        response.sendRedirect(
            request.getContextPath() 
            + "/PlanillaServlet?pllanillaId=" + planillaId
            + "&cursoId=" + p.getCursoId()
            + "&materiaId=" + p.getMateriaId()
            + "&etapa" + p.getEtapa()
        );
    }
}
