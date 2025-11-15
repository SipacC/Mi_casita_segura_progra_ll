package Controlador;

import ConexionDB.ConexionDB;
import java.sql.Connection;


import Modelo.Usuario;
import Modelo.MetodoPago;
import Modelo.Pago;
import Modelo.QrVisita;
import Modelo.TipoPago;
import Modelo.Reserva;
import Modelo.Tarjeta;
import Modelo.Visita;
import ModeloDAO.MetodoPagoDAO;
import ModeloDAO.PagoDAO;
import ModeloDAO.QrVisitaDAO;
import ModeloDAO.TipoPagoDAO;
import ModeloDAO.UsuarioDAO;
import ModeloDAO.TarjetaDAO;
import ModeloDAO.ReservaDAO;
import ModeloDAO.VisitaDAO;

import Gestion_reserva.CrearReservaPDF;
import Gestion_reserva.CorreoEnviarReserva;
import Gestion_qr_visita.CrearCodigoVisita;
import Gestion_qr_visita.CrearQrVisita;
import Gestion_qr_visita.RegistrarQrAccesoVisita;
import Gestion_qr_visita.ArchivoQRUtil;
import Gestion_qr_visita.CorreoEnviarQrVisita;
import Gestion_bitacora.RegistroBitacora;
import Gestion_factura.CrearFactura;
import Gestion_factura.FacturaServiceCorreo;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.io.OutputStream;

public class ControladorResidente extends HttpServlet {

    private ConexionDB conexionDB;
    private Connection con;

    String menuResidente = "vistasResidente/menuResidente.jsp";
    String pagos = "vistasResidente/pagos.jsp";
    String registrarPago = "vistasResidente/registrarPago.jsp";
    String consultarPago = "vistasResidente/consultarPagos.jsp";
    String reservas = "vistasResidente/reservas.jsp";
    String crearReserva = "vistasResidente/crearReserva.jsp";
    String visitas = "vistasResidente/visitas.jsp";
    String registrarVisita = "vistasResidente/registrarVisita.jsp";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usr = (Usuario) (session != null ? session.getAttribute("usuario") : null);

        if (usr == null || !"residente".equalsIgnoreCase(usr.getRol())) {
            response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
            return;
        }

        conexionDB = new ConexionDB();
        con = conexionDB.openConnection();

        PagoDAO pagoDAO = new PagoDAO(con);
        TipoPagoDAO tipoDAO = new TipoPagoDAO(con);
        TarjetaDAO tarjetaDAO = new TarjetaDAO(con);
        ReservaDAO reservaDAO = new ReservaDAO(con);
        VisitaDAO visitaDAO = new VisitaDAO(con);

        String action = request.getParameter("accion");
        if (action == null || action.isEmpty()) {
            action = "menu";
        }

        String acceso = menuResidente;
        System.out.println("valor de accion = [" + request.getParameter("accion") + "]");
        switch (action.toLowerCase()) {
            case "menu":
                acceso = menuResidente;
                break;

            case "tarjetas":
                request.setAttribute("listaTarjetas", tarjetaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = "vistasResidente/tarjetas.jsp";
                break;

            case "registrartarjeta":
                acceso = "vistasResidente/registrarTarjeta.jsp";
                break;

            case "guardartarjeta":
                Tarjeta t = new Tarjeta();
                t.setIdUsuario(usr.getIdUsuario());
                t.setNombreTarjeta(request.getParameter("nombre_tarjeta"));
                t.setNumeroTarjeta(request.getParameter("numero_tarjeta"));
                t.setFechaVencimiento(java.sql.Date.valueOf(request.getParameter("fecha_vencimiento")));
                t.setCvv(request.getParameter("cvv"));
                t.setNombreTitular(request.getParameter("nombre_titular"));
                t.setTipoTarjeta(request.getParameter("tipo_tarjeta"));
                t.setSaldo(Double.parseDouble(request.getParameter("saldo")));

                if (tarjetaDAO.agregar(t)) {
                    request.setAttribute("mensaje", "Tarjeta registrada con éxito.");
                    RegistroBitacora.log(request, "Registró nueva tarjeta " + t.getNombreTarjeta(), "Tarjetas", con);
                } else {
                    request.setAttribute("error", "No se pudo registrar la tarjeta.");
                }
                request.setAttribute("listaTarjetas", tarjetaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = "vistasResidente/tarjetas.jsp";
                break;

            case "editartarjeta":
                int idTarjeta = Integer.parseInt(request.getParameter("id"));
                Tarjeta tarjeta = tarjetaDAO.buscarPorId(idTarjeta);
                request.setAttribute("tarjeta", tarjeta);
                acceso = "vistasResidente/editarTarjeta.jsp";
                break;

            case "actualizartarjeta":
                int idT = Integer.parseInt(request.getParameter("id_tarjeta"));
                Tarjeta original = tarjetaDAO.buscarPorId(idT);

                Tarjeta tEdit = new Tarjeta();
                tEdit.setIdTarjeta(idT);
                tEdit.setIdUsuario(usr.getIdUsuario());

                String nombreTarjeta = request.getParameter("nombre_tarjeta");
                tEdit.setNombreTarjeta((nombreTarjeta != null && !nombreTarjeta.isEmpty()) ? nombreTarjeta : original.getNombreTarjeta());

                String numeroTarjeta = request.getParameter("numero_tarjeta");
                tEdit.setNumeroTarjeta((numeroTarjeta != null && !numeroTarjeta.isEmpty()) ? numeroTarjeta : original.getNumeroTarjeta());

                String fechaVencimientoStr = request.getParameter("fecha_vencimiento");
                tEdit.setFechaVencimiento((fechaVencimientoStr != null && !fechaVencimientoStr.isEmpty())
                        ? java.sql.Date.valueOf(fechaVencimientoStr)
                        : original.getFechaVencimiento());

                String cvv = request.getParameter("cvv");
                tEdit.setCvv((cvv != null && !cvv.isEmpty()) ? cvv : original.getCvv());

                String nombreTitular = request.getParameter("nombre_titular");
                tEdit.setNombreTitular((nombreTitular != null && !nombreTitular.isEmpty()) ? nombreTitular : original.getNombreTitular());

                String tipoTarjeta = request.getParameter("tipo_tarjeta");
                tEdit.setTipoTarjeta((tipoTarjeta != null && !tipoTarjeta.isEmpty()) ? tipoTarjeta : original.getTipoTarjeta());

                String saldoStr = request.getParameter("saldo");
                tEdit.setSaldo((saldoStr != null && !saldoStr.isEmpty()) ? Double.parseDouble(saldoStr) : original.getSaldo());

                if (tarjetaDAO.actualizar(tEdit)) {
                    request.setAttribute("mensaje", "Tarjeta actualizada correctamente.");
                    RegistroBitacora.log(request, "Actualizó tarjeta con ID " + idT, "Tarjetas", con);
                } else {
                    request.setAttribute("error", "No se pudo actualizar la tarjeta.");
                }
                request.setAttribute("listaTarjetas", tarjetaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = "vistasResidente/tarjetas.jsp";
                break;

            case "eliminartarjeta":
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                if (tarjetaDAO.eliminar(idEliminar)) {
                    request.setAttribute("mensaje", "Tarjeta eliminada correctamente.");
                    RegistroBitacora.log(request, "Eliminó tarjeta con ID " + idEliminar, "Tarjetas", con);
                } else {
                    request.setAttribute("error", "No se pudo eliminar la tarjeta.");
                }
                request.setAttribute("listaTarjetas", tarjetaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = "vistasResidente/tarjetas.jsp";
                break;

            case "pagos":
                request.setAttribute("listaPagos", pagoDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = pagos;
                break;

            case "pagosfiltrados": {
                int usuarioId = usr.getIdUsuario();

                String tipo = request.getParameter("tipo");
                String metodo = request.getParameter("metodo");
                String estado = request.getParameter("estado");
                String periodo = request.getParameter("periodo"); 
                String fechaReal = request.getParameter("fecha");

                List<Pago> listaFiltrada = pagoDAO.listarPorUsuarioFiltroAvanzado(
                    usuarioId, tipo, metodo, estado, periodo, fechaReal
                );

                request.setAttribute("listaPagos", listaFiltrada);
                acceso = pagos;
                break;
            }

            
            case "consultarpagos": {
                int idUsuario = usr.getIdUsuario();
                List<TipoPago> listaTipos = tipoDAO.listar();

                boolean tieneMultas = pagoDAO.usuarioTieneMultas(idUsuario);
                if (!tieneMultas) {
                    listaTipos.removeIf(tp -> "Multa".equalsIgnoreCase(tp.getNombre()));
                }
                request.setAttribute("listaTipos", listaTipos);

                String tipoSeleccionado = request.getParameter("tipo_pago");
                int idTipo = -1;

                if (tipoSeleccionado != null && !tipoSeleccionado.isEmpty()) {
                    for (TipoPago tp : listaTipos) {
                        if (tp.getNombre().equalsIgnoreCase(tipoSeleccionado)) {
                            idTipo = tp.getIdTipo();

                            if ("Multa".equalsIgnoreCase(tp.getNombre())) {
                                List<Pago> multasPendientes = pagoDAO.obtenerMultasPendientesPorUsuario(idUsuario);
                                request.setAttribute("listaMultas", multasPendientes);
                                request.setAttribute("tipoPago", "Multa");
                            } else {
                                int[] mesAnio = pagoDAO.getMesYAnioCorrespondiente(
                                        idUsuario, idTipo, usr.getFechaCreacion()
                                );
                                int mesAPagar = mesAnio[0];
                                int anioAPagar = mesAnio[1];

                                double montoBase = tp.getMonto();
                                double mora = pagoDAO.calcularMoraMensual(
                                        mesAPagar, anioAPagar, LocalDate.now()
                                );

                                request.setAttribute("tipoPago", tp.getNombre());
                                request.setAttribute("montoBase", montoBase);
                                request.setAttribute("mesAPagar", mesAPagar);
                                request.setAttribute("anioAPagar", anioAPagar);
                                request.setAttribute("mora", mora);
                                request.setAttribute("totalPago", montoBase);
                            }

                            break;
                        }
                    }
                }

                request.setAttribute("idTipo", idTipo);
                RegistroBitacora.log(request, "Consultó opciones de pago", "Pagos", con);
                acceso = consultarPago;
                break;
            }

            case "registrarpago":{
                try {

                    String idTipoStr = request.getParameter("id_tipo");
                    int idTipo = (idTipoStr != null && !idTipoStr.isEmpty()) ? Integer.parseInt(idTipoStr) : -1;

                    String tipoPago = request.getParameter("tipo_pago");
                    String montoStr = request.getParameter("monto");
                    String moraStr = request.getParameter("mora");
                    String mesStr = request.getParameter("mes");
                    String anioStr = request.getParameter("anio");

                    double monto = (montoStr != null && !montoStr.isEmpty()) ? Double.parseDouble(montoStr) : 0.0;
                    double mora = (moraStr != null && !moraStr.isEmpty()) ? Double.parseDouble(moraStr) : 0.0;
                    int mes = (mesStr != null && !mesStr.isEmpty()) ? Integer.parseInt(mesStr) : 0;
                    int anio = (anioStr != null && !anioStr.isEmpty()) ? Integer.parseInt(anioStr) : 0;

                    request.setAttribute("tipoPago", tipoPago);
                    request.setAttribute("monto", monto);
                    request.setAttribute("mora", mora);
                    request.setAttribute("mes", mes);
                    request.setAttribute("anio", anio);
                    request.setAttribute("idTipo", idTipo);
                    request.setAttribute("nombreUsuario", usr.getNombre());
                    request.setAttribute("apellidoUsuario", usr.getApellido());

                    String idPagoMultaStr = request.getParameter("id_pago");
                    if (idPagoMultaStr != null && !idPagoMultaStr.isEmpty()) {
                        request.setAttribute("idPagoMulta", Integer.parseInt(idPagoMultaStr));
                    }

                    MetodoPagoDAO metodoDAO = new MetodoPagoDAO(con);
                    List<MetodoPago> listaMetodos = metodoDAO.listar();
                    request.setAttribute("listaMetodos", listaMetodos);

                    acceso = registrarPago;

                } catch (Exception e) {
                    request.setAttribute("error", "Error al preparar datos para registrar pago: " + e.getMessage());
                    acceso = "vistasResidente/error.jsp";
                }
                break;
            }

            case "guardarpago": {
                try {
                    String idTipoParam = request.getParameter("id_tipo");
                    String idMetodoParam = request.getParameter("metodo_pago");
                    String montoParam = request.getParameter("monto");
                    String moraParam = request.getParameter("mora");
                    String mesParam = request.getParameter("mes");
                    String anioParam = request.getParameter("anio");
                    String tipoPagoNombre = request.getParameter("tipo_pago");

                    String idTarjetaParam = request.getParameter("id_tarjeta"); 
                    String numeroTarjetaInput = request.getParameter("numero_tarjeta");
                    String cvvInput = request.getParameter("cvv");
                    String nombreTitularInput = request.getParameter("nombre_titular");

                    System.out.println("GuardarPago - id_tipo=" + idTipoParam + " (" + tipoPagoNombre + ")");
                    System.out.println("id_pago recibido: " + request.getParameter("id_pago"));

                    if (usr == null) System.out.println("usr ES NULL");
                    if (pagoDAO == null) System.out.println("pagoDAO ES NULL");
                    if (tipoDAO == null) System.out.println("tipoDAO ES NULL");
                    if (tarjetaDAO == null) System.out.println("tarjetaDAO ES NULL");

                    if (idTipoParam == null || idTipoParam.isEmpty() || idTipoParam.equals("-1")) {
                        throw new IllegalArgumentException("El id_tipo no fue recibido correctamente");
                    }

                    int idTipo = Integer.parseInt(idTipoParam);
                    int idMetodo = Integer.parseInt(idMetodoParam);
                    double monto = Double.parseDouble(montoParam);
                    double mora = Double.parseDouble(moraParam);
                    int mesPagado = (mesParam != null && !mesParam.isEmpty()) ? Integer.parseInt(mesParam) : 0;
                    int anioPagado = (anioParam != null && !anioParam.isEmpty()) ? Integer.parseInt(anioParam) : 0;

                    int idPago = -1;
                    Pago pago = null;

                    MetodoPagoDAO metodoDAO = new MetodoPagoDAO(con);
                    MetodoPago metodoSeleccionado = metodoDAO.buscarPorId(idMetodo);
                    String nombreMetodo = (metodoSeleccionado != null) ? metodoSeleccionado.getNombre() : "";

                    boolean procesoCanceladoPorTarjeta = false;

                    if ("Tarjeta".equalsIgnoreCase(nombreMetodo)) {
                        TarjetaDAO tarjetaDAO1 = new TarjetaDAO(con);
                        Tarjeta tarjetaSel = null;

                        if (idTarjetaParam != null && !idTarjetaParam.isEmpty()) {
                            try {
                                int idTarjetaSel = Integer.parseInt(idTarjetaParam);
                                tarjetaSel = tarjetaDAO.buscarPorUsuarioYId(usr.getIdUsuario(), idTarjetaSel);
                            } catch (NumberFormatException nfe) {
                                tarjetaSel = null;
                            }
                        } else {
                            tarjetaSel = tarjetaDAO.obtenerTarjetaPrincipal(usr.getIdUsuario());
                        }

                        System.out.println("Tarjeta seleccionada: " + (tarjetaSel != null ? tarjetaSel.getNombreTarjeta() : "null"));

                        if (tarjetaSel == null) {
                            request.setAttribute("error", "No tienes ninguna tarjeta registrada. Agrega una antes de continuar.");
                            acceso = registrarPago;
                            procesoCanceladoPorTarjeta = true;
                        } else {
                            if (numeroTarjetaInput != null && !numeroTarjetaInput.trim().isEmpty()) {
                                String dbNumero = tarjetaSel.getNumeroTarjeta() != null ? tarjetaSel.getNumeroTarjeta().replaceAll("\\s+", "") : "";
                                String inNumero = numeroTarjetaInput.replaceAll("\\s+", "");
                                if (!dbNumero.equals(inNumero)) {
                                    request.setAttribute("error", "El número de tarjeta no coincide.");
                                    acceso = registrarPago;
                                    procesoCanceladoPorTarjeta = true;
                                }
                            }

                            if (!procesoCanceladoPorTarjeta && cvvInput != null && !cvvInput.trim().isEmpty()) {
                                String dbCvv = tarjetaSel.getCvv() != null ? tarjetaSel.getCvv().trim() : "";
                                if (!dbCvv.equals(cvvInput.trim())) {
                                    request.setAttribute("error", "El CVV no coincide.");
                                    acceso = registrarPago;
                                    procesoCanceladoPorTarjeta = true;
                                }
                            }

                            if (!procesoCanceladoPorTarjeta && nombreTitularInput != null && !nombreTitularInput.trim().isEmpty()) {
                                String dbTitular = tarjetaSel.getNombreTitular() != null ? tarjetaSel.getNombreTitular().trim().toLowerCase() : "";
                                String inTitular = nombreTitularInput.trim().toLowerCase();
                                if (!dbTitular.equals(inTitular)) {
                                    request.setAttribute("error", "El nombre del titular no coincide.");
                                    acceso = registrarPago;
                                    procesoCanceladoPorTarjeta = true;
                                }
                            }

                            if (!procesoCanceladoPorTarjeta) {
                                java.util.Date fechaVenc = tarjetaSel.getFechaVencimiento();
                                if (fechaVenc == null) {
                                    request.setAttribute("error", "La tarjeta no tiene fecha de vencimiento válida.");
                                    acceso = registrarPago;
                                    procesoCanceladoPorTarjeta = true;
                                } else {
                                    java.time.LocalDate vencLocal = ((java.sql.Date) fechaVenc).toLocalDate();
                                    java.time.LocalDate hoy = java.time.LocalDate.now();
                                    if (vencLocal.isBefore(hoy)) {
                                        request.setAttribute("error", "La tarjeta está vencida (" + vencLocal.toString() + ").");
                                        acceso = registrarPago;
                                        procesoCanceladoPorTarjeta = true;
                                    }
                                }
                            }
                            System.out.println("Entrando a validación final de saldo: procesoCanceladoPorTarjeta = " + procesoCanceladoPorTarjeta);

                            if (!procesoCanceladoPorTarjeta) {
                                double total = monto;
                                double saldoActual = tarjetaSel.getSaldo();
                                if (saldoActual < total) {
                                    request.setAttribute("error", "Saldo insuficiente en la tarjeta.");
                                    acceso = registrarPago;
                                    procesoCanceladoPorTarjeta = true;
                                } else {
                                    double nuevoSaldo = saldoActual - total;
                                    boolean actualizado = tarjetaDAO.actualizarSaldo(tarjetaSel.getIdTarjeta(), nuevoSaldo);
                                    System.out.println("Actualizando tarjeta ID " + tarjetaSel.getIdTarjeta() + " con nuevo saldo Q" + nuevoSaldo);

                                    if (!actualizado) {
                                        request.setAttribute("error", "Error actualizando el saldo de la tarjeta.");
                                        acceso = registrarPago;
                                        procesoCanceladoPorTarjeta = true;
                                    } else {
                                        System.out.println("Saldo actualizado Q" + nuevoSaldo);
                                        request.setAttribute("__tarjeta_usada_id", tarjetaSel.getIdTarjeta());
                                    }
                                }
                            }
                        }
                    }

                    if (procesoCanceladoPorTarjeta) {
                        request.setAttribute("tipoPago", tipoPagoNombre);
                        request.setAttribute("monto", Double.parseDouble(montoParam));
                        request.setAttribute("mora", Double.parseDouble(moraParam));
                        request.setAttribute("mes", mesPagado);
                        request.setAttribute("anio", anioPagado);
                        request.setAttribute("nombreUsuario", usr.getNombre());
                        request.setAttribute("apellidoUsuario", usr.getApellido());
                        request.setAttribute("idTipo", Integer.parseInt(idTipoParam));

                        MetodoPagoDAO metodoDAO2 = new MetodoPagoDAO(con);
                        request.setAttribute("listaMetodos", metodoDAO2.listar());

                        TarjetaDAO tarjetaDAO2 = new TarjetaDAO(con);
                        request.setAttribute("listaTarjetas", tarjetaDAO2.listarPorUsuario(usr.getIdUsuario()));

                        RequestDispatcher rd = request.getRequestDispatcher(acceso);
                        rd.forward(request, response);
                        return;
                    }

                    if (!"Multa".equalsIgnoreCase(tipoPagoNombre)) {
                        pago = new Pago();
                        pago.setIdUsuario(usr.getIdUsuario());
                        pago.setIdTipo(idTipo);
                        pago.setIdMetodo(idMetodo);

                        Object tarjetaUsadaIdObj = request.getAttribute("__tarjeta_usada_id");
                        if (tarjetaUsadaIdObj != null) {
                            try {
                                int tarjetaUsadaId = (Integer) tarjetaUsadaIdObj;
                                pago.setIdTarjeta(tarjetaUsadaId);
                            } catch (Exception ex) {
                                pago.setIdTarjeta(null);
                            }
                        } else {
                            pago.setIdTarjeta(null);
                        }

                        pago.setMonto(monto);
                        pago.setMora(mora);
                        pago.setObservaciones("Pago registrado por residente");
                        pago.setEstado("confirmado");
                        pago.setMesPagado(mesPagado);
                        pago.setAnioPagado(anioPagado);

                        idPago = pagoDAO.registrarPago(pago);
                        pago.setIdPago(idPago);

                        if (mora > 0) {
                            TipoPago tpMulta = tipoDAO.buscarPorNombre("Multa");
                            if (tpMulta != null) {
                                Pago multa = new Pago();
                                multa.setIdUsuario(usr.getIdUsuario());
                                multa.setIdTipo(tpMulta.getIdTipo());
                                multa.setIdMetodo(idMetodo);
                                multa.setMonto(mora);
                                multa.setMora(0);
                                multa.setObservaciones("Multa generada automáticamente por mora del pago de: " + tipoPagoNombre);
                                multa.setEstado("pendiente");
                                multa.setMesPagado(mesPagado);
                                multa.setAnioPagado(anioPagado);
                                pagoDAO.registrarPago(multa);
                            }
                        }

                    } else {
                        String idPagoMultaParam = request.getParameter("id_pago");
                        System.out.println("idPagoMultaParam = " + idPagoMultaParam);
                        if (idPagoMultaParam != null && !idPagoMultaParam.isEmpty()) {
                            int idPagoMulta = Integer.parseInt(idPagoMultaParam);
                            boolean actualizado = pagoDAO.actualizarEstado(idPagoMulta, "confirmado");
                            System.out.println("Actualizado multa = " + actualizado);
                            if (actualizado) {
                                idPago = idPagoMulta;
                                pago = new Pago();
                                pago.setIdPago(idPagoMulta);
                                pago.setIdUsuario(usr.getIdUsuario());
                                pago.setIdTipo(idTipo);
                                pago.setIdMetodo(idMetodo);
                                pago.setMonto(monto);
                                pago.setEstado("confirmado");
                            }
                        }
                    }

                    if (idPago > 0 && pago != null) {
                        System.out.println("Generando factura para pago ID " + idPago);
                        String rutaFactura = CrearFactura.generarFactura(pago, usr, tipoPagoNombre, con);
                        String detallePago = "Pago de " + tipoPagoNombre + " por Q." + monto + " con mora pendiente Q." + mora;
                        boolean enviado = FacturaServiceCorreo.enviarFactura(usr, rutaFactura, detallePago);
                        System.out.println("Correo enviado = " + enviado);
                    }

                    request.setAttribute("listaPagos", pagoDAO.listarPorUsuario(usr.getIdUsuario()));
                    acceso = pagos;

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error en guardarpago: " + e.getMessage());
                    request.setAttribute("error", "Error al guardar pago: " + e.getMessage());
                    acceso = "vistasResidente/error.jsp";
                }
                break;
            }

            
            case "reservas":
                String buscar = request.getParameter("buscar");
                List<Reserva> lista;
                if (buscar != null && !buscar.trim().isEmpty()) {
                    lista = reservaDAO.buscarPorNombre(usr.getIdUsuario(), buscar);
                    request.setAttribute("busqueda", buscar);
                } else {
                    lista = reservaDAO.listarPorUsuario(usr.getIdUsuario());
                }
                request.setAttribute("listaReservas", lista);
                acceso = reservas;
                break;
            
            case "crearreserva":
                acceso = crearReserva;
                break;
                
            case "guardarreserva":
                try {
                    System.out.println("Acción: guardareserva");
                    System.out.println("id_area = " + request.getParameter("id_area"));
                    System.out.println("fecha = " + request.getParameter("fecha"));
                    System.out.println("hora_inicio = " + request.getParameter("hora_inicio"));
                    System.out.println("hora_fin = " + request.getParameter("hora_fin"));

                    Reserva r = new Reserva();
                    r.setId_usuario(usr.getIdUsuario());
                    r.setId_area(Integer.parseInt(request.getParameter("id_area")));
                    r.setFecha_reserva(java.sql.Date.valueOf(request.getParameter("fecha")));

                    String horaInicioParam = request.getParameter("hora_inicio");
                    String horaFinParam = request.getParameter("hora_fin");

                    if (horaInicioParam != null && horaInicioParam.length() == 5) {
                        horaInicioParam += ":00";
                    }
                    if (horaFinParam != null && horaFinParam.length() == 5) {
                        horaFinParam += ":00";
                    }

                    r.setHora_inicio(java.sql.Time.valueOf(horaInicioParam));
                    r.setHora_fin(java.sql.Time.valueOf(horaFinParam));
                    r.setEstado("Activa");

                    if (reservaDAO.validarDisponibilidad(r)) {
                        int idReserva = reservaDAO.insertar(r);

                        if (idReserva > 0) {
                            r.setId_reserva(idReserva);
                            String rutaPDF = CrearReservaPDF.generarReserva(r, usr);

                            if (rutaPDF != null) {
                                new CorreoEnviarReserva().enviarCorreo(usr, rutaPDF);
                                request.setAttribute("mensaje", "Reserva creada y comprobante enviado al correo.");
                            } else {
                                request.setAttribute("error", "No se pudo generar el PDF de la reserva.");
                            }
                        } else {
                            request.setAttribute("error", "Error al guardar la reserva (insertar devolvió 0).");
                        }
                    } else {
                        request.setAttribute("error", "El área no está disponible en ese horario.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Error al procesar la reserva: " + e.getMessage());
                }

                request.setAttribute("listaReservas", reservaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = reservas;
                break;

            case "cancelarreserva":
                int idReserva = Integer.parseInt(request.getParameter("id"));
                reservaDAO.cancelar(idReserva);
                request.setAttribute("mensaje", "Reserva cancelada con éxito.");

                request.setAttribute("listaReservas", reservaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = reservas;
                break;

            case "visitas":
                request.setAttribute("listaVisitas", visitaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = "vistasResidente/visitas.jsp";
                break;

            case "registrarvisita":
                System.out.println("Entró a registrarVisita.jsp");
                acceso = "vistasResidente/registrarVisita.jsp";
                break;

            case "guardarvisita":{
                Visita v = new Visita();
                v.setId_usuario(usr.getIdUsuario());
                v.setNombre(request.getParameter("nombre"));
                v.setDpi_visita(request.getParameter("dpi"));
                v.setTipo_visita(request.getParameter("tipo"));
                v.setCorreo_visita(request.getParameter("correo"));
                v.setMotivo(request.getParameter("motivo")); 
                v.setEstado("activo");

                int idVisita = visitaDAO.registrarVisita(v);

                if (idVisita > 0) {
                    String codigo = CrearCodigoVisita.generarCodigo();
                    String rutaQR = CrearQrVisita.generarQR(idVisita, codigo);

                    RegistrarQrAccesoVisita registrar = new RegistrarQrAccesoVisita();

                    String validezTexto = "";
                    if ("Visita".equalsIgnoreCase(v.getTipo_visita())) {
                        java.sql.Timestamp fechaValidaHasta = java.sql.Timestamp.valueOf(
                                request.getParameter("fechaValidaHasta") + " 23:59:59"
                        );
                        registrar.registrarQrYAcceso(idVisita, codigo, fechaValidaHasta, null, rutaQR);
                        validezTexto = "hasta " + fechaValidaHasta.toLocalDateTime().toLocalDate();

                    } else if ("Por intentos".equalsIgnoreCase(v.getTipo_visita())) {
                        int intentos = Integer.parseInt(request.getParameter("intentos"));
                        registrar.registrarQrYAcceso(idVisita, codigo, null, intentos, rutaQR);
                        validezTexto = intentos + " intentos";
                    }

                    String correoResidente = usr.getCorreo();
                    String correoVisitante = v.getCorreo_visita();
                    String nombreVisitante = v.getNombre();
                    String detalleValidez = validezTexto;
                    String qrGenerado = rutaQR;

                    new Thread(() -> {
                        try {
                            CorreoEnviarQrVisita correo = new CorreoEnviarQrVisita();

                            correo.enviarAlResidente(
                                    correoResidente,
                                    nombreVisitante,
                                    java.time.LocalDateTime.now().toString(),
                                    detalleValidez,
                                    qrGenerado
                            );

                            correo.enviarAlVisitante(
                                    correoVisitante,
                                    nombreVisitante,
                                    detalleValidez,
                                    qrGenerado
                            );

                            System.out.println("Correos enviados en background para visita ID " + idVisita);

                        } catch (Exception e) {
                            System.err.println("Error enviando correos en background: " + e.getMessage());
                        }
                    }).start();

                    RegistroBitacora.log(request, "Creó la visita con ID " + idVisita, "Visitas", con);
                    request.setAttribute("mensaje", "Visita creada con éxito.");

                } else {
                    request.setAttribute("error", "Error al registrar la visita.");
                }

                request.setAttribute("listaVisitas", visitaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = "vistasResidente/visitas.jsp";
                break;
            }

            case "cancelarvisita": {
                int idVisita = Integer.parseInt(request.getParameter("id"));

                QrVisitaDAO qrDAO = new QrVisitaDAO(con);

                String rutaQR = qrDAO.obtenerRutaPorVisita(idVisita);

                qrDAO.cancelarPorVisita(idVisita);

                ArchivoQRUtil.eliminarArchivo(rutaQR);

                visitaDAO.eliminarVisita(idVisita);

                RegistroBitacora.log(request, "Eliminó la visita con ID " + idVisita, "Visitas", con);

                request.setAttribute("mensaje", "Visita, QR y archivo eliminado con éxito.");
                request.setAttribute("listaVisitas", visitaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = "vistasResidente/visitas.jsp";
                break;
            }
           case "descargarqrvisita":{
                int idVisita = Integer.parseInt(request.getParameter("id"));

                QrVisitaDAO qrDAO = new QrVisitaDAO(con);
                String rutaQR = qrDAO.obtenerRutaPorVisita(idVisita);

                if (rutaQR != null) {
                    File file = new File(rutaQR);
                    if (file.exists()) {
                        response.setContentType("application/octet-stream");
                        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());

                        try (FileInputStream fis = new FileInputStream(file);
                            OutputStream os = response.getOutputStream()) {

                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = fis.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                            os.flush();
                        } catch (Exception e) {
                            System.err.println("Error al descargar QR: " + e.getMessage());
                        }
                        return;
                    } else {
                        request.setAttribute("error", "El archivo QR no existe en el servidor.");
                    }
                } else {
                    request.setAttribute("error", "No se encontró la ruta del QR.");
                }

                request.setAttribute("listaVisitas", visitaDAO.listarPorUsuario(usr.getIdUsuario()));
                acceso = "vistasResidente/visitas.jsp";
                break;
            }

            case "directorio": {
                String texto = request.getParameter("texto");
                UsuarioDAO usuarioDAO = new UsuarioDAO(con);
                List<Usuario> resultados = new ArrayList<>();

                if (texto != null && !texto.trim().isEmpty()) {
                    texto = texto.trim();
                    
                    resultados = usuarioDAO.buscarDirectorio(texto, texto, texto, texto);
                    request.setAttribute("listaUsuarios", resultados);

                    if (resultados.isEmpty()) {
                        request.setAttribute("mensaje", "No se encontró ningún usuario con los datos ingresados.");
                    }
                } else {
                    request.setAttribute("mensaje", "Busca por nombre, apellido, lote o número de casa.");
                }

                RegistroBitacora.log(request, "Consultó el directorio residencial (AJAX)", "Directorio", con);

                RequestDispatcher vistaAjax = request.getRequestDispatcher("vistasResidente/directorio.jsp");
                vistaAjax.forward(request, response);
                return;
            }

            case "reportemantenimiento": {
                ModeloDAO.TipoInconvenienteDAO tipoDAORep = new ModeloDAO.TipoInconvenienteDAO(con);
                List<Modelo.TipoInconveniente> listaTipos = tipoDAORep.listar();
                request.setAttribute("listaTipos", listaTipos);
                String idTipoStr = request.getParameter("id_tipo_inconveniente");
                if (idTipoStr != null && !idTipoStr.isEmpty()) {
                    try {
                        int idTipoSeleccionado = Integer.parseInt(idTipoStr);
                        System.out.println("Tipo de inconveniente seleccionado: " + idTipoSeleccionado);
                        request.setAttribute("idSeleccionado", idTipoSeleccionado);
                    } catch (NumberFormatException e) {
                        System.err.println("ID de tipo inválido: " + idTipoStr);
                    }
                }

                acceso = "vistasResidente/reporteMantenimiento.jsp";
                break;
            }


            case "guardarreportemantenimiento": {
                String idTipoStr = request.getParameter("id_tipo_inconveniente");
                String descripcion = request.getParameter("descripcion");
                String fechaStr = request.getParameter("fecha_incidente");

                HttpSession sessionActiva = request.getSession(false);
                Modelo.Usuario usuarioSesion = (sessionActiva != null)
                        ? (Modelo.Usuario) sessionActiva.getAttribute("usuario")
                        : null;

                if (usuarioSesion == null) {
                    response.sendRedirect(request.getContextPath() + "/ControladorLogin?accion=login");
                    return;
                }

                java.sql.Timestamp fechaIncidente;
                try {
                    fechaIncidente = java.sql.Timestamp.valueOf(fechaStr.replace("T", " ") + ":00");
                } catch (Exception e) {
                    fechaIncidente = new java.sql.Timestamp(System.currentTimeMillis());
                }

                int idTipo = Integer.parseInt(idTipoStr);

                Modelo.ReporteMantenimiento reporte = new Modelo.ReporteMantenimiento();
                reporte.setIdResidente(usuarioSesion.getIdUsuario());
                reporte.setIdTipoInconveniente(idTipo);
                reporte.setDescripcion(descripcion);
                reporte.setFechaIncidente(fechaIncidente);

                ModeloDAO.ReporteMantenimientoDAO reporteDAO = new ModeloDAO.ReporteMantenimientoDAO(con);
                boolean insertado = reporteDAO.insertar(reporte);

                if (insertado) {
                    final String descripcionFinal = descripcion;
                    final java.sql.Timestamp fechaFinal = fechaIncidente;
                    final String nombreFinal = usuarioSesion.getNombre();
                    final String apellidoFinal = usuarioSesion.getApellido();
                    final int idTipoFinal = idTipo;

                    new Thread(() -> {
                        try {
                            ModeloDAO.UsuarioDAO usuarioDAO = new ModeloDAO.UsuarioDAO(con);
                            ModeloDAO.CatalogoIncidenteDAO catalogoDAO = new ModeloDAO.CatalogoIncidenteDAO(con);

                            String nombreTipo = catalogoDAO.obtenerNombrePorId(idTipoFinal);
                            List<String> correosAdmins = usuarioDAO.listarCorreosAdministradores();

                            String asunto = "Nuevo reporte de mantenimiento";
                            String cuerpo = "El residente " + nombreFinal + " " + apellidoFinal
                                    + " ha reportado un nuevo inconveniente.\n\n"
                                    + "Tipo de inconveniente: " + nombreTipo + "\n"
                                    + "Descripción: " + descripcionFinal + "\n"
                                    + "Fecha del incidente: " + fechaFinal + "\n\n"
                                    + "Por favor, tomar las acciones correspondientes.";

                            for (String correo : correosAdmins) {
                                util.CorreoNotificacion.enviar(correo, asunto, cuerpo);
                            }
                        } catch (Exception e) {
                            System.err.println("Error enviando notificaciones: " + e.getMessage());
                        }
                    }).start();

                    request.setAttribute("mensaje", "Reporte guardado correctamente y notificación enviada.");
                } else {
                    request.setAttribute("mensaje", "Error al guardar el reporte.");
                }

                ModeloDAO.CatalogoIncidenteDAO catalogoDAO2 = new ModeloDAO.CatalogoIncidenteDAO(con);
                List<Modelo.CatalogoIncidente> listaTipos = catalogoDAO2.listar();
                request.setAttribute("listaTipos", listaTipos);

                acceso = "vistasResidente/reporteMantenimiento.jsp";
                break;
            }



                        default:
                acceso = menuResidente;
        }

        if (acceso != null) {
            RequestDispatcher vista = request.getRequestDispatcher(acceso);
            vista.forward(request, response);
        }

        if (conexionDB != null) {
            //conexionDB.closeConnection();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
