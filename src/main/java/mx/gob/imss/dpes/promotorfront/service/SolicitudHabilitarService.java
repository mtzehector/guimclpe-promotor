/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.enums.TipoEstadoSolicitudEnum;
import mx.gob.imss.dpes.interfaces.solicitud.model.Solicitud;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.solicitud.model.EstadoSolicitud;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.SolicitudClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author juan.garfias
 */
@Provider
public class SolicitudHabilitarService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private SolicitudClient service;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        log.log(Level.WARNING, "SolicitudHabilitarService 1 {0}", request.getPayload().getSolicitud());

        EstadoSolicitud edo = new EstadoSolicitud();
        edo.setId(request.getPayload().getSolicitud().getCveEstadoSolicitud().getId());
        edo.setDesEstadoSolicitud(request.getPayload().getSolicitud().getCveEstadoSolicitud().getDesEstadoSolicitud());

        Solicitud s = new Solicitud();
        s.setEstadoSolicitud(
                TipoEstadoSolicitudEnum.forValue(
                        request.getPayload().getSolicitud().getCveEstadoSolicitud().getId()
                )
        );
        s.setId(request.getPayload().getSolicitud().getId());
        s.setCveEstadoSolicitud(edo);

        Response load = service.updateEstado(s);

        if (load.getStatus() == 200) {

            Solicitud solicitudOut = load.readEntity(Solicitud.class);
            log.log(Level.WARNING, "SolicitudHabilitarService 2 solicitudOut{0}", solicitudOut);

            return new Message<>(request.getPayload());
        }
        return response(null, ServiceStatusEnum.EXCEPCION, null, null);
        
    }

}
