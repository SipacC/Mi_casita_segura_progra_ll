package Gestion_reserva;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.File;

import Modelo.Usuario;

public class CorreoEnviarReserva {

    private final String remitente = "sipacchuquiejj@gmail.com";
    private final String password = "fzdp wmxq aixb puxo"; // ⚠️ ideal moverlo a configuración externa

    /**
     * Envía un correo con el comprobante de reserva adjunto
     * @param usuario residente al que se enviará el comprobante
     * @param rutaPDF ruta absoluta del PDF generado
     */
    public boolean enviarCorreo(Usuario usuario, String rutaPDF) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(remitente, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente, "Sistema de Reservas - Mi Casita Segura"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(usuario.getCorreo()));
            message.setSubject("Comprobante de Reserva");

            String cuerpo = "¡Hola " + usuario.getNombre() + " " + usuario.getApellido() + "!\n\n"
                    + "Su reserva se ha registrado con éxito.\n"
                    + "Adjunto encontrará el comprobante en formato PDF.\n\n"
                    + "Gracias por usar el sistema de reservas del residencial Mi Casita Segura.\n";

            MimeBodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo, "utf-8");

            File archivo = new File(rutaPDF);
            if (!archivo.exists()) {
                System.err.println("No se encontró el archivo PDF: " + rutaPDF);
                return false;
            }

            MimeBodyPart adjunto = new MimeBodyPart();
            adjunto.attachFile(archivo);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(texto);
            multipart.addBodyPart(adjunto);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("📧 Correo de reserva enviado a " + usuario.getCorreo());
            return true;

        } catch (Exception e) {
            System.err.println("Error al enviar correo de reserva: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
