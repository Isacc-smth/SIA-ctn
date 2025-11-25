/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.loginproject;

import com.mycompany.loginproject.dao.EspecialidadDao;
import com.mycompany.loginproject.model.Especialidad;
import com.mycompany.loginproject.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author jonat
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/AdminServlet"})
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        try {
            List<Especialidad> especialidades = new EspecialidadDao().findAll();
            request.setAttribute("especialidades", especialidades);

            // optionally, set selected especialidad if a request param was provided
            String selId = request.getParameter("especialidad");
            if (selId != null && !selId.isEmpty()) {
                try {
                    int id = Integer.parseInt(selId);
                    Especialidad sel = new EspecialidadDao().findById(id);
                    if (sel != null) {
                        request.setAttribute("selEspecialidad", sel);
                    }
                } catch (NumberFormatException ignored) {
                }
            }

        } catch (Exception ex) {
            throw new ServletException("Error loading especialidades", ex);
        }

        // forward to JSP page (adjust path to your JSP)
        request.getRequestDispatcher("/Admin.jsp").forward(request, response);
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
