package websocket;

import com.fazecast.jSerialComm.SerialPort;

public class ArduinoSerial {

    private static SerialPort arduinoPort;

    public static void conectar(String puerto, int baudRate) {
        try {
            if (arduinoPort != null && arduinoPort.isOpen()) {
                System.out.println("El puerto " + puerto + " ya estaba abierto, no se vuelve a abrir.");
                return;
            }

            arduinoPort = SerialPort.getCommPort(puerto);
            arduinoPort.setComPortParameters(baudRate, 8, 1, 0);
            arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

            if (arduinoPort.openPort()) {
                System.out.println("Conectado al Arduino en " + puerto + " a " + baudRate + " baudios.");
            } else {
                System.out.println("Error al conectar con Arduino en " + puerto);
            }

        } catch (Exception e) {
            System.err.println("Excepción al intentar conectar con Arduino: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void enviar(String data) {
        try {
            if (arduinoPort != null && arduinoPort.isOpen()) {
                data = data + "\n"; 
                byte[] buffer = data.getBytes();
                arduinoPort.writeBytes(buffer, buffer.length);
                System.out.println("Enviado a Arduino: " + data);
            } else {
                System.out.println("No se pudo enviar. El puerto no está abierto.");
            }
        } catch (Exception e) {
            System.err.println("Error enviando datos a Arduino: " + e.getMessage());
        }
    }

    public static void cerrar() {
        try {
            if (arduinoPort != null && arduinoPort.isOpen()) {
                arduinoPort.closePort();
                System.out.println("🔌 Puerto Arduino cerrado.");
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar el puerto Arduino: " + e.getMessage());
        }
    }
}
