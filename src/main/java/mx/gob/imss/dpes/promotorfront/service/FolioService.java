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
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.solicitud.model.Solicitud;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.model.SolicitudModel;
import mx.gob.imss.dpes.promotorfront.restclient.SolicitudClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class FolioService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {
    
    @Inject
    @RestClient
    SolicitudClient client;
    
    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        try {
            //log.log(Level.INFO,">>>INICIA STEP CREAR FOLIO 1: {0}", request.getPayload());
            Solicitud s = new Solicitud();
            s.setConsecutivo(request.getPayload().getControlFolio().getNumConsecutivo());
            s.setDelegacion(request.getPayload().getSolicitud().getDelegacion());
            s.setId(request.getPayload().getSolicitud().getId());
            s.setAltaRegistro(request.getPayload().getSolicitud().getAltaRegistro());
            Response load = client.crearFolio(s);
            if (load.getStatus() == Response.Status.OK.getStatusCode()) {
                SolicitudModel solicitud = load.readEntity(SolicitudModel.class);
                //log.log(Level.INFO,">>>INICIA STEP CREAR FOLIO 2: {0}", solicitud);
                request.getPayload().getSolicitud().setNumFolioSolicitud(solicitud.getNumFolioSolicitud());
                return request;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR FolioService.execute: [" + request + "]", e);
        }

        throw new RegistrarReinstalacionEndPointException(
            RegistrarReinstalacionEndPointException.ERROR_SERVICIO_FOLIO);
    }
    
}
