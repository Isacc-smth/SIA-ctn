package com.mycompany.loginproject.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mycompany.loginproject.dao.StudentRowDao;
import com.mycompany.loginproject.model.StudentRow;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

/** 
 * Helper class for sending emails
 *
 * @author Isacc-smth
 * */
public class EmailHandler {

    /** 
     * Método auxiliar para obtener los correos asociados con un alumno
     * NOTA: los correos pueden ser null, se tiene que validar desde el caller
     *
     * @param idAlumno identificador del alumno
     *
     * @return Los emails de los encargados en un {@link java.util.ArrayList}
     *
     * */

    // NOTE: Se tiene que verificar desde el caller si las cadenas son vacias o null
    public static ArrayList<String> getEmailsById(int idAlumno) throws SQLException {
        ArrayList<String> emails = new ArrayList<>();
        String sql = """
                    SELECT a.correo_encargado, a.correo_encargado2 FROM alumno a
                    WHERE a.id = ?;
                    """;

        try (Connection con = ConnectionPoolListener.getCon();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idAlumno);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    emails.add(rs.getString("correo_encargado"));
                    emails.add(rs.getString("correo_encargado2"));
                }
            }
        }
        return emails;
    }

    /** 
     * Send an email with an attachment and a default message
     *
     * @param toAdress the recipient's email adress
     * @param pdf      as an array of bytes
     *
     * @throws MessagingException when the email fails to send
     * */
    public static void sendEmail(String toAdress, byte[] pdf) throws MessagingException {
            Session session = SessionManager.getSession();

            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress());
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAdress));

            // FIX: Cambiar el mensaje por defecto, pueden preguntar esta parte a la coordi
            mimeMessage.setSubject("Prueba de Email");
            String msg = "Si recibiste este correo, LAS CREDENCIALES SON CORRECTAS!!!";

            MimeBodyPart body = new MimeBodyPart();
            body.setContent(msg, "text/html; charset=utf-8");
            MimeBodyPart attachment = new MimeBodyPart();
            DataSource dataSource = new ByteArrayDataSource(pdf, "application/pdf");
            attachment.setDataHandler(new DataHandler(dataSource));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(body);
            multipart.addBodyPart(attachment);

            mimeMessage.setContent(multipart);
            Transport.send(mimeMessage);
    }
}
