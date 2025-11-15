package Gestion_qr_visita;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class CorreoEnviarQrVisita {

    private final String remitente = "sipacchuquiejj@gmail.com";
    private final String password = "fzdp wmxq aixb puxo";

    /**
     * Envía el correo al RESIDENTE que creó la visita (RN6).
     * @param destinatario correo del residente
     * @param nombreVisitante nombre del visitante
     * @param fecha texto de la fecha/hora de generación
     * @param validez texto sobre validez (fecha límite o intentos)
     * @param rutaQR ruta absoluta al archivo PNG generado
     */
    public boolean enviarAlResidente(String destinatario, String nombreVisitante,
                                     String fecha, String validez, String rutaQR) {
        String asunto = "Notificación de accesos creados";
        String cuerpo = "El código QR fue generado exitosamente para la persona "
                + nombreVisitante + " el día " + fecha
                + " para acceder al condominio.\n"
                + "Este código tiene una validez de: " + validez + ".\n\n"
                + "En caso de cualquier irregularidad, por favor contacte al administrador del sistema.";

        return enviarCorreo(destinatario, asunto, cuerpo, rutaQR);
    }

    /**
     * Envía el correo al VISITANTE registrado (RN7).
     * @param destinatario correo del visitante
     * @param nombreVisitante nombre del visitante
     * @param validez texto sobre validez (fecha límite o intentos)
     * @param rutaQR ruta absoluta al archivo PNG generado
     */
    public boolean enviarAlVisitante(String destinatario, String nombreVisitante,
                                     String validez, String rutaQR) {
        String asunto = "Notificación de accesos creados";
        String cuerpo = "¡Hola!\n\n"
                + "Se ha generado exitosamente tu código QR de acceso al residencial.\n"
                + "A continuación, encontrarás los detalles de tu registro:\n\n"
                + "Nombre del visitante: " + nombreVisitante + "\n"
                + "Validez del código QR: " + validez + "\n\n"
                + "Instrucciones importantes:\n"
                + "- Guarda este correo o el código QR adjunto.\n"
                + "- Preséntalo al llegar al residencial para que el personal de seguridad lo escanee y valide tu acceso.\n\n"
                + "¡Gracias por coordinar tu visita con anticipación!";

        return enviarCorreo(destinatario, asunto, cuerpo, rutaQR);
    }

    // Método interno para armar y enviar el correo
    private boolean enviarCorreo(String destinatario, String asunto,
                                 String cuerpo, String rutaQR) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(remitente));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            msg.setSubject(asunto);

            MimeBodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo, "utf-8");

            MimeBodyPart adjunto = new MimeBodyPart();
            DataSource source = new FileDataSource(rutaQR);
            adjunto.setDataHandler(new DataHandler(source));
            adjunto.setFileName("qr_visita.png");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(texto);
            multipart.addBodyPart(adjunto);

            msg.setContent(multipart);

            Transport.send(msg);
            System.out.println("Correo enviado a " + destinatario);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar correo a " + destinatario + ": " + e.getMessage());
            return false;
        }
    }
}
