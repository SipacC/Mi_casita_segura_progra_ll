#include <Servo.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

// Objetos
Servo servoEntrada;
Servo servoSalida;
LiquidCrystal_I2C lcd(0x27, 16, 2);

// Pines
const int pinServoEntrada = 6;  
const int pinServoSalida  = 10;  
const int ledEntrada      = 9;   
const int ledSalida       = 8;   
const int sensorIR        = 7;   

// Ángulos
const int anguloReposo = 0;
const int anguloActivo = 90;

void setup() {
  Serial.begin(115200); 
  servoEntrada.attach(pinServoEntrada);
  servoSalida.attach(pinServoSalida);

  // Configurar LEDs
  pinMode(ledEntrada, OUTPUT);
  pinMode(ledSalida, OUTPUT);

  // Configurar sensor IR
  pinMode(sensorIR, INPUT);

  // Estado inicial
  servoEntrada.write(anguloReposo);
  servoSalida.write(anguloReposo);
  digitalWrite(ledEntrada, HIGH);
  digitalWrite(ledSalida, HIGH);

  // Inicializar LCD
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0,0);
  lcd.print("Sistema listo");
  delay(1500);
  lcd.clear();
}

void loop() {
  int estadoIR = digitalRead(sensorIR);
  if (estadoIR == HIGH) {
    lcd.setCursor(0,0);
    lcd.print("Muestre su QR   ");
  } else {
    lcd.setCursor(0,0);
    lcd.print("Esperando...    ");
  }

  if (Serial.available() > 0) {
    String comando = Serial.readStringUntil('\n');
    comando.trim();

    if (comando == "1") {
      
      digitalWrite(ledEntrada, LOW);
      servoEntrada.write(anguloActivo);

      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print(">> ENTRADA <<");
      lcd.setCursor(0,1);
      lcd.print("Acceso permitido");

      delay(2000);

      servoEntrada.write(anguloReposo);
      digitalWrite(ledEntrada, HIGH);

      lcd.clear();
      lcd.print("Entrada fin");
    }
    else if (comando == "2") {
      
      digitalWrite(ledSalida, LOW);
      servoSalida.write(anguloActivo);

      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print(">> SALIDA <<");
      lcd.setCursor(0,1);
      lcd.print("Acceso permitido");

      delay(2000);

      servoSalida.write(anguloReposo);
      digitalWrite(ledSalida, HIGH);

      lcd.clear();
      lcd.print("Salida fin");
    }
    else if (comando == "0") {
      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print("Acceso denegado");
      lcd.setCursor(0,1);
      lcd.print("Credenciales X");

      delay(2000);
      lcd.clear();
    }
    else {
      
      lcd.clear();
      lcd.setCursor(0,0);
      lcd.print("Comando invalido");
    }
  }

  delay(200); 
}