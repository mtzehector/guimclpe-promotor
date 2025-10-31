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
import mx.gob.imss.dpes.interfaces.prestamo.model.Prestamo;

import mx.gob.imss.dpes.promotorfront.assembler.PrestamoAssembler;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.PrestamoClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class PrestamoService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel>{
    
    @Inject
    @RestClient
    PrestamoClient client;
    
    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        
        log.log(Level.INFO,">>>promotorfront|PrestamoService: {0}", request.getPayload());
        PrestamoAssembler assembler = new PrestamoAssembler();
        Response load = client.create(assembler.assemble(request.getPayload()));
        if (load.getStatus() == 200) {
            Prestamo prestamo = load.readEntity(Prestamo.class);
            request.getPayload().setPrestamo(prestamo);
            return request;
            
        }
        return response(null, ServiceStatusEnum.EXCEPCION, null, null);
        
    }

}
