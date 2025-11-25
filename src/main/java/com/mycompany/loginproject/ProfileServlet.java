/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.loginproject;

import com.mycompany.loginproject.dao.ProfesorDao;
import com.mycompany.loginproject.model.Profesor;
import com.mycompany.loginproject.model.User;
import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jonat
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/ProfileServlet"})
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        try {
            Profesor profesor = new ProfesorDao().findById(user.getId());
            req.setAttribute("profesor", profesor);

            req.getRequestDispatcher("/Profile.jsp").forward(req, resp);
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error: " + e);
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        Profesor profesor;

        ProfesorDao profesorDao = new ProfesorDao();
        try {
            profesor = new ProfesorDao().findById(user.getId());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error: " + e);
            return;
        }
        // Read editable fields from the form.
        String correo = req.getParameter("correo");
        String telefono = req.getParameter("telefono");
        String celular = req.getParameter("celular");
        String usuario = req.getParameter("usuario");

        
        List<String> errors = new ArrayList<>();
        // basic validation
        
        if (usuario == null || usuario.trim().isEmpty()) {
            errors.add("El nombre de usuario no puede estar vacío.");
        }

        // telefono and celular optional but if provided should be numbers
        try {
            if (telefono != null && !telefono.trim().isEmpty()) profesor.setTelefono(Integer.valueOf(telefono.trim()));
        } catch (NumberFormatException ex) {
            errors.add("Teléfono inválido: debe contener sólo dígitos.");
        }
        try {
            if (celular != null && !celular.trim().isEmpty()) profesor.setCelular(Integer.valueOf(celular.trim()));
        } catch (NumberFormatException ex) {
            errors.add("Celular inválido: debe contener sólo dígitos.");
        }

        profesor.setUsuario(usuario);
        if (correo != null) profesor.setCorreo(correo);

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("profesor", profesor);
            req.getRequestDispatcher("/Profile.jsp").forward(req, resp);
            return;
        }
        
        try {
            boolean ok = profesorDao.update(profesor);
            if (ok) {
                Profesor refreshed = profesorDao.findById(profesor.getId());
                session.setAttribute("flashMessage", "Datos guardados correctamente.");
                resp.sendRedirect(req.getContextPath() + "/ProfileServlet");
            } else {
                errors.add("No se pudieron guardar los datos. Intente de nuevo más tarde.");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher("/Profile.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error: " + e);
            return;
        }
    }
}
