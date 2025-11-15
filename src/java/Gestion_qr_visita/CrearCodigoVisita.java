package Gestion_qr_visita;

import java.security.SecureRandom;

public class CrearCodigoVisita {
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LONGITUD = 10;

    /**
     * Genera un código aleatorio para usar en el QR de una visita
     * @return String con el código generado
     */
    public static String generarCodigo() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(LONGITUD);
        for (int i = 0; i < LONGITUD; i++) {
            int index = random.nextInt(CARACTERES.length());
            sb.append(CARACTERES.charAt(index));
        }
        return sb.toString();
    }
}
