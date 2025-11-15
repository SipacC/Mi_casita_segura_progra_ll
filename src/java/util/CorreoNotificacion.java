package util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class CorreoNotificacion {

    public static boolean enviar(String destinatario, String asunto, String cuerpo) {
        final String remitente = "sipacchuquiejj@gmail.com";   // 🔹 tu correo
        final String clave = "fzdp wmxq aixb puxo";            // 🔹 app password (16 caracteres)

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(cuerpo);

            Transport.send(message);

            System.out.println("Correo enviado a " + destinatario);
            return true;
        } catch (Exception e) {
            System.err.println("Error enviando correo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
