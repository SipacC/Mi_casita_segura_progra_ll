package Gestion_qr_visita;

import java.io.File;

public class ArchivoQRUtil {

    /**
     * Elimina físicamente un archivo QR del disco.
     * @param ruta -> ruta absoluta del archivo (ej: C:\qrsProyectoVisita\qr_visita_3.png)
     */
    public static void eliminarArchivo(String ruta) {
        if (ruta != null) {
            try {
                File f = new File(ruta);
                if (f.exists()) {
                    if (f.delete()) {
                        System.out.println("✅ Archivo eliminado: " + ruta);
                    } else {
                        System.err.println("⚠ No se pudo eliminar el archivo: " + ruta);
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Error al eliminar archivo: " + e.getMessage());
            }
        }
    }
}
