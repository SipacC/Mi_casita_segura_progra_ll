package Gestion_qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CrearQr {

    public static String generarQR(int idUsuario, String codigo) {
        try {
            String contenidoQR = "ID:" + idUsuario + ";CODIGO:" + codigo;

            String carpeta = "C:/qrsProyecto/";
            File dir = new File(carpeta);
            if (!dir.exists()) dir.mkdirs();

            String nombreArchivo = "qr_usuario_" + idUsuario + ".png";
            Path path = FileSystems.getDefault().getPath(carpeta + nombreArchivo);

            BitMatrix matrix = new MultiFormatWriter().encode(
                contenidoQR, BarcodeFormat.QR_CODE, 300, 300
            );

            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

            System.out.println("QR generado en: " + path.toAbsolutePath());

            return path.toString();
        } catch (Exception e) {
            System.err.println("Error al generar QR: " + e.getMessage());
            return null;
        }
    }
}
