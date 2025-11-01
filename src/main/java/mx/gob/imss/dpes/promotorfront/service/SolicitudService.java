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
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.EntidadFinanciera;
import mx.gob.imss.dpes.interfaces.prestamo.model.PrestamoRecuperacion;
import mx.gob.imss.dpes.interfaces.solicitud.model.EstadoSolicitud;

import mx.gob.imss.dpes.promotorfront.assembler.SolicitudAssembler;

import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.model.SolicitudModel;
import mx.gob.imss.dpes.promotorfront.restclient.EntidadFinancieraClient;
import mx.gob.imss.dpes.promotorfront.restclient.SolicitudClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class SolicitudService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    SolicitudClient client;

    @Inject
    @RestClient
    private EntidadFinancieraClient clientEF;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        log.log(Level.INFO, ">>>INICIA STEP CREAR SOLICITUD: {0}", request.getPayload());
        SolicitudAssembler assembler = new SolicitudAssembler();
        Response load = client.load(assembler.assemble(request.getPayload()));
        if (load.getStatus() == 200) {
            SolicitudModel solicitud = load.readEntity(SolicitudModel.class);
            request.getPayload().setSolicitud(solicitud);

            EstadoSolicitud edo = new EstadoSolicitud();
            edo.setId(TipoEstadoSolicitudEnum.PRE_SIMULACION.toValue());
            edo.setDesEstadoSolicitud(
                    TipoEstadoSolicitudEnum.PRE_SIMULACION.getDescripcion()
            );
            request.getPayload().getSolicitud().setCveEstadoSolicitud(edo);
                    
            log.log(Level.INFO, ">>>INICIA STEP CREAR SOLICITUD SOLICITUD CREADA: {0}", request.getPayload().getSolicitud());

                    
            return request;

        }
        return response(null, ServiceStatusEnum.EXCEPCION, null, null);

    }
}
