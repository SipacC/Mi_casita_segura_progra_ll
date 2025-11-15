package websocket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ArduinoListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Aplicación iniciada, conectando a Arduino...");
        ArduinoSerial.conectar("COM3", 115200); //
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Aplicación detenida, cerrando puerto Arduino...");
        ArduinoSerial.cerrar();
    }
}
