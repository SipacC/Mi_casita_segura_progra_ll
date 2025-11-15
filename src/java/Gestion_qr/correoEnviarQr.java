package Gestion_qr;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class correoEnviarQr {

    private final String remitente = "sipacchuquiejj@gmail.com";
    private final String password = "fzdp wmxq aixb puxo";

    public boolean enviarConQR(String destinatario, String nombreUsuario, String usuario, String contrasena, String rutaQR) {
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
            msg.setSubject("Creación de usuario para el sistema - Residencial Xibalbá");

            String cuerpo = "¡Hola " + nombreUsuario + "!\n\n"
                    + "Se ha creado exitosamente tu usuario para el sistema de la residencial.\n\n"
                    + "A continución, encotraras los detalles de tu registro:\n"
                    + "   • Usuario: " + usuario + "\n"
                    + "   • Contraseña: " + contrasena + "\n\n"
                    + "Adjunto encontrarás tu código QR para el acceso.\n\n"
                    + "Por favor, guarda este correo o el código QR adjunto.\n\n"
                    + "Preséntalo al llegar al residencial para que el personal de seguridad lo escanee y valide tu acceso.\n\n"
                    + "Atentamente,\n"
                    + "Residencial Xibalbá";

            MimeBodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo, "utf-8");

            MimeBodyPart adjunto = new MimeBodyPart();
            DataSource source = new FileDataSource(rutaQR);
            adjunto.setDataHandler(new DataHandler(source));
            adjunto.setFileName("qr_usuario.png");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(texto);
            multipart.addBodyPart(adjunto);

            msg.setContent(multipart);

            Transport.send(msg);
            System.out.println("Correo enviado correctamente a " + destinatario);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
            return false;
        }
    }
}
