package com.mycompany.loginproject.clases;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

// NOTE: Si el algun punto queres poder enviar desde mas de un email, vas a tener que
// refactorear bastante (no es muy buena idea, a no ser que encuentres un metodo mas facil)
public class SessionManager {
    private static final Session session;
    private static final Properties sessionProps = new Properties();

    private static String host = "smtp.gmail.com";
    private static int port = 587;
    private static final Properties emailProps = new Properties();
    private static String username;
    private static String password;

    private SessionManager() {}

    static {
        try {
            sessionProps.put("mail.smtp.auth", "true");
            sessionProps.put("mail.smtp.starttls.enable", "true");
            sessionProps.put("mail.smtp.host", host);
            sessionProps.put("mail.smtp.port", String.valueOf(port));

            // WARN: Este path tiene que estar en el contenedor o en el equipo, capaz tengas que cambiar si estas en Windows
            emailProps.load(new FileInputStream("/usr/local/share/db_sia.properties"));
            username = emailProps.getProperty("sia_email");
            password = emailProps.getProperty("sia_email_pass");

            session = Session.getInstance(sessionProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("Error while getting email account credentials", e);
        }
    }

    public static Session getSession() {
        return session;
    }
}
