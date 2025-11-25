/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package com.mycompany.loginproject.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

/**
 * Filtro que agrega la fecha actual al modelo de la peticion
 * */
@WebFilter(filterName = "DateFilter", urlPatterns = {"/*"})
public class DateFilter implements Filter {

    /** Locale de Paraguay usado para formatear la fecha */
    private static final Locale PY_LOCALE = new Locale.Builder().setLanguage("es").setRegion("PY").build();

    /** Formateador de la fecha "Ej: viernes, 14 de noviembre de 2025"*/
    private static final DateTimeFormatter FMT
            = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", PY_LOCALE);

    /** Zona horaria de Paraguay */
    private static final ZoneId PY_ZONE = ZoneId.of("America/Asuncion");

    /** 
     * Agregar la fecha actual al modelo de la peticion si no es de login
     *
     * @param req la solicitud a procesar
     * @param res la respuesta a enviar
     * @param chain el filtro de seguimiento
     * */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String uri = request.getRequestURI();

        // Exclude login page(s) and static resources:
        if (!uri.contains("/login") && !isStaticResource(uri)) {
            ZonedDateTime now = ZonedDateTime.now(PY_ZONE);
            String nowFormatted = now.format(FMT);
            nowFormatted = capitalizeFirst(nowFormatted, PY_LOCALE);
            request.setAttribute("nowFormatted", nowFormatted);
        }

        chain.doFilter(req, res);
    }

    /** 
     * Verificar si el recurso es un recurso estático
     *
     * @param uri la uri del recurso
     *
     * @return true si el recurso es un recurso estático, false de lo contrario
     * */
    private boolean isStaticResource(String uri) {
        String lower = uri.toLowerCase();
        return lower.endsWith(".css") || lower.endsWith(".js")
                || lower.endsWith(".png") || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg") || lower.endsWith(".gif")
                || lower.endsWith(".woff") || lower.endsWith(".woff2")
                || lower.endsWith(".map") || lower.contains("/static/");
    }

    /**
     * Pasar la primera letra de un String a mayuscula
     *
     * @param s el String a pasar
     * @param locale el Locale del String
     *
     * @return el String con la primera letra en mayuscula
     * */
    private String capitalizeFirst(String s, Locale locale) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase(locale) + s.substring(1);
    }
}
