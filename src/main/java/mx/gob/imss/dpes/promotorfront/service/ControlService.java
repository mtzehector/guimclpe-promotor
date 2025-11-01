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
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.promotorfront.exeption.ControlException;
import mx.gob.imss.dpes.promotorfront.model.ControlFolioModel;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.ControlClient;
import mx.gob.imss.dpes.promotorfront.rule.ControlFolio;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class ControlService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {
    
    @Inject
    @RestClient
    private ControlClient client;
    
    @Inject
    private ControlFolio rule;

    
    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        try {
            //log.log(Level.INFO,">>>INICIA STEP CREAR CONTROLFOLIO: {0}", request.getPayload());
            rule.apply(request.getPayload());
            Response load = client.load(request.getPayload().getControlFolio());
            if (load.getStatus() == Response.Status.OK.getStatusCode()) {
                ControlFolioModel controlOut = load.readEntity(ControlFolioModel.class);
                request.getPayload().setControlFolio(controlOut);
                return new Message<>(request.getPayload());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR ControlService.execute - [" + request + "]", e);
        }

        return response(null, ServiceStatusEnum.EXCEPCION, new ControlException(), null);
    }
}
