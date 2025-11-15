package Gestion_factura;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.File;

import Modelo.Usuario;

public class FacturaServiceCorreo {

    private static final String remitente = "sipacchuquiejj@gmail.com";
    private static final String password = "fzdp wmxq aixb puxo";

    public static boolean enviarFactura(Usuario usuario, String rutaFactura, String detallePago) {
        if (usuario == null || usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
            System.err.println("Usuario o correo no válido para enviar factura.");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(remitente, "Sistema de Facturación - Mi Casita Segura"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(usuario.getCorreo()));
            msg.setSubject("Factura generada: " + detallePago);

            String cuerpo = "<h3>¡Hola " + usuario.getNombre() + " " + usuario.getApellido() + "!</h3>"
                    + "<p>Se ha registrado con éxito el <b>" + detallePago + "</b>.</p>"
                    + "<p><b>Lote/Casa:</b> " + usuario.getLote() + " - " + usuario.getNumeroCasa() + "</p>"
                    + "<p>Adjunto encontrará su factura en formato PDF.</p>"
                    + "<br>"
                    + "<p style='color:gray;font-size:12px;'>Gracias por confiar en el residencial <b>Mi Casita Segura</b>.</p>";

            MimeBodyPart texto = new MimeBodyPart();
            texto.setContent(cuerpo, "text/html; charset=utf-8");

            File archivo = new File(rutaFactura);
            if (!archivo.exists()) {
                System.err.println("No se encontró el archivo PDF: " + rutaFactura);
                return false;
            }
            MimeBodyPart adjunto = new MimeBodyPart();
            DataSource source = new FileDataSource(archivo);
            adjunto.setDataHandler(new DataHandler(source));
            adjunto.setFileName(archivo.getName());

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(texto);
            multipart.addBodyPart(adjunto);

            msg.setContent(multipart);

            Transport.send(msg);
            System.out.println("Correo con factura enviado a " + usuario.getCorreo() 
                               + " (" + archivo.getName() + ")");
            return true;

        } catch (Exception e) {
            System.err.println("Error al enviar correo con factura: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
