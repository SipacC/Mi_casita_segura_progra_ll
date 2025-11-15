<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Modelo.Usuario usr = (Modelo.Usuario) session.getAttribute("usuario");
    if (usr == null || !"administrador".equalsIgnoreCase(usr.getRol())) {
        response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Administrador de Cámaras</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/camaras.css">
    <script src="https://unpkg.com/html5-qrcode" type="text/javascript"></script>
</head>
<body class="camaras-page">

<div class="camera-container container mt-4">
    <h2 id="tituloCamara">📹 Cámara Entrada</h2>

    <div id="reader"></div>
    <div id="mensajes" class="alert mt-3 text-center"></div>

    <div class="d-flex justify-content-center gap-2 mt-4">
        <button id="btnToggle" class="btn btn-primary">Cambiar a Cámara Salida</button>
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/ControladorAdmin?accion=menu">
            ⬅ Volver al Menú
        </a>
    </div>
</div>

<script>

    let tipoCamara = "entrada";
    let host = window.location.hostname;
    let socket = new WebSocket("ws://" + host + ":8080/MiCasitaSegura/qrSocket");

    const mensajesDiv = document.getElementById("mensajes");

        socket.onopen = () => {
        mensajesDiv.className = "alert alert-success";
        mensajesDiv.innerText = "Conectado al servidor";
    };
    socket.onerror = () => {
        mensajesDiv.className = "alert alert-danger";
        mensajesDiv.innerText = "Error en conexión WebSocket";
    };
    socket.onclose = () => {
        mensajesDiv.className = "alert alert-warning";
        mensajesDiv.innerText = "Conexión cerrada con el servidor";
    };
    socket.onmessage = (event) => {
        let data = JSON.parse(event.data);

        if (data.resultado === "valido") {
            mensajesDiv.className = "alert alert-success";
            mensajesDiv.innerText = (tipoCamara === "entrada" ? "Entrada permitida: " : "Salida registrada: ") + data.usuario;
        } else if (data.resultado === "invalido") {
            mensajesDiv.className = "alert alert-danger";
            mensajesDiv.innerText = "QR inválido o desactivado";
        } else if (data.resultado === "duplicado") {
            mensajesDiv.className = "alert alert-warning";
            mensajesDiv.innerText = "Acceso duplicado para " + (data.usuario || "usuario");
        } else if (data.resultado === "sin_intentos") {
            mensajesDiv.className = "alert alert-danger";
            mensajesDiv.innerText = "Sin intentos disponibles para esta visita";
        } else if (data.resultado === "expirado") {
            mensajesDiv.className = "alert alert-danger";
            mensajesDiv.innerText = "QR de visita expirado";
        } else {
            mensajesDiv.className = "alert alert-danger";
            mensajesDiv.innerText = "Error en validación del QR";
        }
    };
    let ultimoQR = "";
    let tiempoUltimo = 0;

    function onScanSuccess(decodedText) {
        let ahora = Date.now();
        if (decodedText === ultimoQR && (ahora - tiempoUltimo) < 2000) {
            return; // ignorar repetidos en < 2s
        }
        ultimoQR = decodedText;
        tiempoUltimo = ahora;

        console.log(`QR detectado (${tipoCamara}): ${decodedText}`);
        socket.send("tipo:" + tipoCamara + ";" + decodedText);
    }

    let html5QrcodeScanner = new Html5QrcodeScanner("reader", { fps: 10, qrbox: 250 });
    html5QrcodeScanner.render(onScanSuccess);

    document.getElementById("btnToggle").addEventListener("click", () => {
        if (tipoCamara === "entrada") {
            tipoCamara = "salida";
            document.getElementById("tituloCamara").innerText = "Cámara Salida";
            document.getElementById("btnToggle").innerText = "Cambiar a Cámara Entrada";
        } else {
            tipoCamara = "entrada";
            document.getElementById("tituloCamara").innerText = "Cámara Entrada";
            document.getElementById("btnToggle").innerText = "Cambiar a Cámara Salida";
        }
        mensajesDiv.className = "alert alert-info";
        mensajesDiv.innerText = `Modo cambiado a ${tipoCamara}`;
    });
</script>

</body>
</html>
