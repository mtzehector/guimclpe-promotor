package mx.gob.imss.dpes.promotorfront.service;

import mx.gob.imss.dpes.common.enums.TipoEstadoSolicitudEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.solicitud.model.EstadoSolicitud;
import mx.gob.imss.dpes.promotorfront.assembler.SolicitudReinstalacionAssembler;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.model.SolicitudModel;
import mx.gob.imss.dpes.promotorfront.restclient.SolicitudClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class SolicitudReinstalacionService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    SolicitudClient solicitudClient;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        try {
            SolicitudReinstalacionAssembler assembler = new SolicitudReinstalacionAssembler();

            SolicitudModel solicitudModel = assembler.assemble(request.getPayload());

            if(solicitudModel == null)
                throw new RegistrarReinstalacionEndPointException (
                    RegistrarReinstalacionEndPointException.ERROR_ENSAMBLADO_SOLICITUD_REINSTALACION);

            Response load = solicitudClient.reinstalacion(solicitudModel);

            if (load.getStatus() == Response.Status.OK.getStatusCode()) {
                solicitudModel = load.readEntity(SolicitudModel.class);

                request.getPayload().setSolicitud(solicitudModel);

                EstadoSolicitud edo = new EstadoSolicitud();
                edo.setId(TipoEstadoSolicitudEnum.PRE_SIMULACION.toValue());
                edo.setDesEstadoSolicitud(
                    TipoEstadoSolicitudEnum.PRE_SIMULACION.getDescripcion()
                );

                request.getPayload().getSolicitud().setCveEstadoSolicitud(edo);

                return request;
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR SolicitudReinstalacionService.execute: [" + request + "]", e);
        }

        throw new RegistrarReinstalacionEndPointException (
            RegistrarReinstalacionEndPointException.ERROR_SERVICIO_SOLICITUD_REINSTALACION);
    }
}
