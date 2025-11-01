package mx.gob.imss.dpes.promotorfront.service;

import mx.gob.imss.dpes.common.constants.Constantes;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.prestamo.model.AmortizacionInsumos;
import mx.gob.imss.dpes.interfaces.prestamo.model.AmortizacionPorDescuento;
import mx.gob.imss.dpes.interfaces.prestamo.model.PrestamoInsumoTa;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.AmortizacionClient;
import mx.gob.imss.dpes.promotorfront.restclient.PrestamoInsumoTaClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

@Provider
public class ValidaTablaAmortizacionService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private AmortizacionClient amortizacionClient;

    @Inject
    @RestClient
    private PrestamoInsumoTaClient prestamoInsumoTAClient;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        try {
            AmortizacionInsumos prestamoTA = new AmortizacionInsumos();
            prestamoTA.setCveSolicitud(request.getPayload().getSolicitud().getId());

            Response respuestaPrestamoTA = amortizacionClient.getTablaAmortizacionByCveSolicitud(prestamoTA);

            Collection<AmortizacionPorDescuento> tablaAmortizacion = null;
            if (respuestaPrestamoTA != null && respuestaPrestamoTA.getStatus() == Response.Status.OK.getStatusCode())
                tablaAmortizacion =
                    respuestaPrestamoTA.readEntity(new GenericType<Collection<AmortizacionPorDescuento>>() {});

            Response respuestaPrestamoInsumoTA = prestamoInsumoTAClient.obtenerPrestamoInsumoPorCveSolicitud(
                request.getPayload().getSolicitud().getId());

            PrestamoInsumoTa prestamoInsumo = null;
            if (respuestaPrestamoInsumoTA != null &&
                respuestaPrestamoInsumoTA.getStatus() == Response.Status.OK.getStatusCode())
                prestamoInsumo = respuestaPrestamoInsumoTA.readEntity(PrestamoInsumoTa.class);

            request.getPayload().setValidacionTablaAmortizacionExitosa(
                this.esValidaTablaAmortizacion(
                    new ArrayList<AmortizacionPorDescuento>(tablaAmortizacion), prestamoInsumo
                )
            );

            return request;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR ValidaTablaAmortizacionService.execute - [" + request + "]", e);
        }

        throw new RegistrarReinstalacionEndPointException(
            RegistrarReinstalacionEndPointException.ERROR_SERVICIO_VALIDAR_MONTOS_DESCUENTOS);
    }

    private boolean esValidaTablaAmortizacion(List<AmortizacionPorDescuento> tablaAmortizacion,
        PrestamoInsumoTa insumoTa) {

        if (!(tablaAmortizacion != null && !tablaAmortizacion.isEmpty() && insumoTa != null))
            return false;

        double descuentoMensual = insumoTa.getDescuentoMensual() != null ?
            insumoTa.getDescuentoMensual().doubleValue() : 0.0;

        for (AmortizacionPorDescuento amortizacion : tablaAmortizacion) {
            if (amortizacion.getPeriodo().equals(0))
                continue;

            if (amortizacion.getSaldo() < Constantes.CERO.doubleValue() ||
                amortizacion.getCapital() < Constantes.CERO.doubleValue() ||
                amortizacion.getIntereses() < Constantes.CERO.doubleValue() ||
                amortizacion.getIva() < Constantes.CERO.doubleValue()) {
                log.log(Level.WARNING, "SE ENCONTRARON MONTOS NEGATIVOS EN TABLA AMORTIZACION: {0}",
                    amortizacion);
                return false;
            }

            if (descuentoMensual != amortizacion.getDescuento()) {
                log.log(Level.WARNING,
                    "SE ENCONTRO DESCUENTO DIFERENTE EN TABLA AMORTIZACION: {0}, " +
                            "DESCUENTO PRESTAMO_INSUMO_TA: {1}, DESCUENTO PRESTAMO_TA: {2}",
                    new Object[] {
                        amortizacion,
                        descuentoMensual,
                        amortizacion.getDescuento()
                    }
                );
                return false;
            }
        }

        return true;
    }
}
