package mx.gob.imss.dpes.promotorfront.service;

import mx.gob.imss.dpes.common.enums.TipoEstadoSolicitudEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.solicitud.model.EstadoSolicitud;
import mx.gob.imss.dpes.interfaces.solicitud.model.Solicitud;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.SolicitudClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class CancelaSolicitudService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private SolicitudClient solicitudClient;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            if (request.getPayload().getValidacionTablaAmortizacionExitosa())
                return request;

            log.log(Level.WARNING,
                "SE CAMBIARA ESTADO A {0}-{1}, DEBIDO A MONTOS NEGATIVOS Y/O DESCUENTO DIFERENTE, ID SOLICITUD: {2}",
                new Object[] {
                    TipoEstadoSolicitudEnum.CANCELADO_DIFERENTE_TABLA_AMORTIZACION.getDescripcion(),
                    TipoEstadoSolicitudEnum.CANCELADO_DIFERENTE_TABLA_AMORTIZACION.toValue(),
                    request.getPayload().getSolicitud().getId()
                }
            );

            EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
            estadoSolicitud.setId(TipoEstadoSolicitudEnum.CANCELADO_DIFERENTE_TABLA_AMORTIZACION.getTipo());

            Solicitud solicitud = new Solicitud();
            solicitud.setId(request.getPayload().getSolicitud().getId());
            solicitud.setCveEstadoSolicitud(estadoSolicitud);
            solicitud.setEstadoSolicitud(
                TipoEstadoSolicitudEnum.forValue(
                    TipoEstadoSolicitudEnum.CANCELADO_DIFERENTE_TABLA_AMORTIZACION.getTipo()
                )
            );

            solicitudClient.updateEstado(solicitud);
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR CambiarEstatusService.execute: [" + request + "]", e);
        }

        throw new RegistrarReinstalacionEndPointException(
            RegistrarReinstalacionEndPointException.ERROR_SERVICIO_CANCELA_SOLICITUD);
    }
}
