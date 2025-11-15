package Gestion_factura;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import Modelo.Pago;
import Modelo.Usuario;
import Modelo.TipoPago;
import Modelo.MetodoPago;
import ModeloDAO.TipoPagoDAO;
import ModeloDAO.MetodoPagoDAO;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrearFactura {

    public static String generarFactura(Pago pago, Usuario usuario, String tipoPagoNombre, Connection con) {
        try {
            String carpeta = "C:/facturasProyecto/";
            File dir = new File(carpeta);
            if (!dir.exists()) dir.mkdirs();

            String numeroFactura = "FAC-" + pago.getIdPago() + "-" + usuario.getIdUsuario();
            String nombreArchivo = "factura_" + numeroFactura + ".pdf";
            String rutaPDF = carpeta + nombreArchivo;

            PdfWriter writer = new PdfWriter(rutaPDF);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("FACTURA ELECTRÓNICA")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Residencial Xibalbá")
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()))
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Datos del Residente").setBold());
            document.add(new Paragraph("Nombre: " + usuario.getNombre() + " " + usuario.getApellido()));
            document.add(new Paragraph("Correo: " + usuario.getCorreo()));
            document.add(new Paragraph("Lote/Casa: " + usuario.getLote() + " - " + usuario.getNumeroCasa()));
            document.add(new Paragraph("\n"));

            TipoPagoDAO tipoDAO = new TipoPagoDAO(con);
            TipoPago tipo = tipoDAO.buscarPorId(pago.getIdTipo());
            String nombreTipo = (tipo != null) ? tipo.getNombre() : tipoPagoNombre;

            MetodoPagoDAO metodoDAO = new MetodoPagoDAO(con);
            MetodoPago metodo = metodoDAO.buscarPorId(pago.getIdMetodo());
            String nombreMetodo = (metodo != null) ? metodo.getNombre() : "Desconocido";

            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 3}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addCell(new Cell().add(new Paragraph("Tipo de Pago").setBold()));
            table.addCell(new Cell().add(new Paragraph("Monto").setBold()));
            table.addCell(new Cell().add(new Paragraph("Mora pendiente de pago").setBold()));
            table.addCell(new Cell().add(new Paragraph("Estado").setBold()));
            table.addCell(new Cell().add(new Paragraph("Método de Pago").setBold()));

            table.addCell(nombreTipo);
            table.addCell("Q. " + pago.getMonto());
            table.addCell("Q. " + pago.getMora());
            table.addCell(pago.getEstado());
            table.addCell(nombreMetodo);

            document.add(table);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Se ha realizado el pago de " + nombreTipo + " con éxito.")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold());

            document.close();
            System.out.println("Factura generada en: " + rutaPDF);

            return rutaPDF;
        } catch (Exception e) {
            System.err.println("Error al generar factura: " + e.getMessage());
            return null;
        }
    }
}
