/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.loginproject;

import com.mycompany.loginproject.dao.CursoDao;
import com.mycompany.loginproject.dao.PlanillaDao;
import com.mycompany.loginproject.model.Curso;
import com.mycompany.loginproject.model.Planilla;
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

/**
 * Servlet para la pagina de inicio
 *
 * @author jonat, Isacc-smth
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/HomeServlet"})
public class HomeServlet extends HttpServlet {

	/** 
	 * Obtener los cursos asociados al usuario (profesor)
	 *
	 * @param request	la solicitud del cliente
	 * @param response  la respuesta del servidor
	 *
	 * @throws ServletException cuando ocurre un error relacionado al servlet (e.j: errores en las consultas, conexion con la BD etc.)
	 * @throws IOException		cuando ocurre un error de entrada salida (e.j: con el dispatcher para redirigir a esta página)
	 * */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        ArrayList<Curso> cursos;
        try {
            cursos = new CursoDao().consultarCursos(user.getId());
        } catch (SQLException sqle) {
            log("Error loading cursos for user " + user.getId(), sqle);
            throw new ServletException("Unable to load planillas", sqle);
        }
        String cursoIdStr = request.getParameter("cursoId");
        String etapaStr = request.getParameter("etapa");

        Curso selectedCurso = null;
        int selectedEtapa = 1;

        if (cursoIdStr != null && !cursoIdStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(cursoIdStr.trim());
                for (Curso c : cursos) {// TODO get rid of unnecessary loop
                    if (c.getId() == id) {
                        selectedCurso = c;
                        break;
                    }
                }
            } catch (NumberFormatException ex) {
            }
        }
        if (selectedCurso == null && !cursos.isEmpty()) {
            selectedCurso = cursos.get(0);
        }

        if (etapaStr != null && !etapaStr.trim().isEmpty()) {
            try {
                selectedEtapa = Integer.parseInt(etapaStr.trim());
            } catch (NumberFormatException ex) {
            }
        }

        if (selectedCurso != null) {
            ArrayList<Planilla> planillas;
            try {
                planillas = new PlanillaDao()
                        .consultarPlanillas(user.getId(), selectedCurso.getId(), selectedEtapa);
            } catch (SQLException sqle) {
                log("Error loading planillas for user " + user.getId()
                        + ", curso " + selectedCurso.getId()
                        + ", etapa " + selectedEtapa, sqle);

                throw new ServletException("Unable to load planillas", sqle);
            }
            request.setAttribute("planillas", planillas);
        }

        request.setAttribute("cursos", cursos);
        request.setAttribute("selCurso", selectedCurso);
        request.setAttribute("selEtapa", selectedEtapa);

        request.getRequestDispatcher("/Home.jsp").forward(request, response);

    }

}
