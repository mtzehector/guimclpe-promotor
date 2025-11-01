/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.EntidadFinanciera;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.EntidadFinancieraClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class EntidadFinancieraService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {
    
    @Inject
    @RestClient
    private EntidadFinancieraClient clientEF;
    
    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        String id = request.getPayload().getCveEntidadFinSIPRE().replace(" ", "");
        Response resp = clientEF.load(id);
        if (resp.getStatus() == 200) {
            EntidadFinanciera ef = resp.readEntity(EntidadFinanciera.class);
            request.getPayload().setEntidadFinanciera(ef);
        }
        return request;
        }
}
