package Gestion_reserva;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import Modelo.Reserva;
import Modelo.Usuario;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrearReservaPDF {

    public static String generarReserva(Reserva reserva, Usuario usuario) {
        try {
            String carpeta = "C:/pdfReserva/";
            File dir = new File(carpeta);
            if (!dir.exists()) dir.mkdirs();

            String nombreArchivo = "reserva_" + reserva.getId_reserva() + ".pdf";
            String rutaPDF = carpeta + nombreArchivo;

            PdfWriter writer = new PdfWriter(rutaPDF);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("COMPROBANTE DE RESERVA")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Residencial Mi Casita Segura")
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Fecha de emisión: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()))
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Residente: " + usuario.getNombre() + " " + usuario.getApellido()));
            document.add(new Paragraph("Correo: " + usuario.getCorreo()));
            document.add(new Paragraph("Área común: " + reserva.getNombreArea()));
            document.add(new Paragraph("Fecha reservada: " + reserva.getFecha_reserva()));
            document.add(new Paragraph("Horario: " + reserva.getHora_inicio() + " - " + reserva.getHora_fin()));
            document.add(new Paragraph("Estado: " + reserva.getEstado()));
            document.add(new Paragraph("\n"));

            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 4}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.addCell(new Cell().add(new Paragraph("Número de Reserva").setBold()));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(reserva.getId_reserva()))));
            table.addCell(new Cell().add(new Paragraph("Área Común").setBold()));
            table.addCell(new Cell().add(new Paragraph(reserva.getNombreArea())));
            table.addCell(new Cell().add(new Paragraph("Fecha").setBold()));
            table.addCell(new Cell().add(new Paragraph(reserva.getFecha_reserva().toString())));
            table.addCell(new Cell().add(new Paragraph("Hora inicio").setBold()));
            table.addCell(new Cell().add(new Paragraph(reserva.getHora_inicio().toString())));
            table.addCell(new Cell().add(new Paragraph("Hora fin").setBold()));
            table.addCell(new Cell().add(new Paragraph(reserva.getHora_fin().toString())));
            document.add(table);

            document.add(new Paragraph("\nGracias por usar el sistema de reservas. 🎉")
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();

            System.out.println("Reserva generada en: " + rutaPDF);
            return rutaPDF;
        } catch (Exception e) {
            System.err.println("Error al generar reserva: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
