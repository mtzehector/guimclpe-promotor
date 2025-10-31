/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import java.util.List;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.promotorfront.exeption.SolicitudVigenteException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.model.SolicitudesVigentesModel;
import mx.gob.imss.dpes.promotorfront.restclient.SolicitudesVigentesClient;
import mx.gob.imss.dpes.promotorfront.rule.SolicitudVigente;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class SolicitudesVigentesService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {
    
    @Inject
    @RestClient
    private SolicitudesVigentesClient solicitudesVigentesClient;
    
    @Inject
    SolicitudVigente rule;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        try {
            //log.log(Level.INFO,">>>promotorfront|SolicitudesVigentesService: {0}", request.getPayload());

            SolicitudesVigentesModel sv = new SolicitudesVigentesModel();
            sv.setGrupoFamiliar(request.getPayload().getPensionado().getIdGrupoFamiliar());
            sv.setNss(request.getPayload().getPensionado().getIdNss());

            rule.apply(sv);
            Response solicitudesVigentesResponse = solicitudesVigentesClient.load(sv);

            if (solicitudesVigentesResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                List<SolicitudesVigentesModel> solicitudesVigentes =
                    solicitudesVigentesResponse.readEntity(new GenericType<List<SolicitudesVigentesModel>>() {});

                if (solicitudesVigentes.isEmpty())
                    return request;
            }
        } catch(Exception e) {
            log.log(Level.SEVERE, "ERROR SolicitudesVigentesService.execute = [" + request + "]", e);
        }

        throw new SolicitudVigenteException();
    }
}
