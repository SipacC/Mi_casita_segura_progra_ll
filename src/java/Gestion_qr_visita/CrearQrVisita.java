package Gestion_qr_visita;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CrearQrVisita {

    /**
     * Genera un archivo PNG con el QR de una visita
     * @param idVisita ID de la visita
     * @param codigo Código aleatorio generado para la visita
     * @return Ruta absoluta al archivo PNG generado
     */
    public static String generarQR(int idVisita, String codigo) {
        try {
            // Contenido dentro del QR
            String contenidoQR = "ID_VISITA:" + idVisita + ";CODIGO:" + codigo;

            // Carpeta donde se guardarán los QR de visitas
            String carpeta = "C:/qrsProyectoVisista/";
            File dir = new File(carpeta);
            if (!dir.exists()) dir.mkdirs();

            // Nombre único del archivo
            String nombreArchivo = "qr_visita_" + idVisita + ".png";
            Path path = FileSystems.getDefault().getPath(carpeta + nombreArchivo);

            // Crear matriz del QR
            BitMatrix matrix = new MultiFormatWriter().encode(
                contenidoQR, BarcodeFormat.QR_CODE, 300, 300
            );

            // Guardar QR en disco
            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

            System.out.println("QR de visita generado en: " + path.toAbsolutePath());

            return path.toString();
        } catch (Exception e) {
            System.err.println("Error al generar QR de visita: " + e.getMessage());
            return null;
        }
    }
}
