package mx.gob.imss.dpes.promotorfront.exeption;

import mx.gob.imss.dpes.common.exception.BusinessException;

public class RegistrarReinstalacionEndPointException extends BusinessException {
    public final static String ERROR_DESCONOCIDO = "msg000";
    public final static String ERROR_ENSAMBLADO_SOLICITUD_REINSTALACION = "msg367";
    public final static String ERROR_SERVICIO_SOLICITUD_REINSTALACION = "msg368";
    public final static String ERROR_REGLA_CONTROL_FOLIO = "msg369";
    public final static String ERROR_SERVICIO_FOLIO = "msg370";
    public final static String ERROR_ENSAMBLADO_PRESTAMO_REINSTALACION = "msg371";
    public final static String ERROR_SERVICIO_PRESTAMO_REINSTALACION = "msg372";
    public final static String ERROR_SOLICITUD_SIN_PRESTAMOS_EN_RECUPERACION = "msg373";
    public final static String ERROR_SERVICIO_PRESTAMO_RECUPERACION_REINSTALACION = "msg374";
    public final static String ERROR_ENSAMBLADO_AMORTIZACION = "msg375";
    public final static String ERROR_SERVICIO_AMORTIZACION = "msg376";
    public final static String ERROR_SERVICIO_CORREO = "msg377";
    public final static String ERROR_SERVICIO_VALIDAR_MONTOS_DESCUENTOS = "msg378";
    public final static String ERROR_SERVICIO_CANCELA_SOLICITUD = "msg379";
    public final static String ERROR_SOLICITUD_SIN_PRESTAMOS_RECUPERACION = "msg380";
    public final static String ERROR_EN_RECUPERACION_TOKEN = "msg0381";

    public RegistrarReinstalacionEndPointException(String causa) {
        super(causa);
    }
}
