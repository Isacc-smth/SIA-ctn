package com.mycompany.loginproject;

import com.mycompany.loginproject.dao.UserDao;
import com.mycompany.loginproject.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para manejar el inicio de sesion
 *
 * @author jonat, Isacc-smth
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    /** 
     * Obtener credenciales del formulario y verificar si el usuario existe y si es administrador.
     * Si es administrador se le redirige a un enlace especial
     *
     * @param request  solicitud con los parametros (usuario y contraseña)
     * @param response respuesta del servlet al cliente dependiendo de si existe o no
     * */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDao userDao = new UserDao();
        try {
            User user = userDao.findByUsernameAndPassword(username, password);
            if (user != null) {
                // store user in session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                // forward to home.jsp

                int level = user.getLevel();
                switch (level) {
                    case 1:
                        response.sendRedirect(request.getContextPath() + "/HomeServlet");
                        break;
                    case 2:
                        response.sendRedirect(request.getContextPath() + "/AdminServlet");
                        break;
                    default:
                        request.setAttribute("loginError", true);
                        request.getRequestDispatcher("/index.jsp").forward(request, response);
                }

            } else {
                // login failed: send back to login with error flag
                request.setAttribute("loginError", true);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("DB error during login", e);
        }
    }
}
