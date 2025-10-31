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
import mx.gob.imss.dpes.promotorfront.assembler.PersonaAssembler;
import mx.gob.imss.dpes.promotorfront.model.PersonaRequest;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.PersonaClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class PensionadoService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {
    
    @Inject
    PersonaClient client;
    
    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        
        log.log(Level.INFO,">>>INICIA STEP PERSISTIR PENSIONADO: {0}", request.getPayload());
        if(request.getPayload().getPersonaRequest().getFlat().equals("sinregistro")){
            PersonaAssembler persona = new PersonaAssembler();
            Response load = client.persistirPersona(persona.assemble(request.getPayload()));
            if (load.getStatus() == 200) {
                //PersonaRequest pr  = load.readEntity(PersonaRequest.class);
                //request.getPayload().setPersonaRequest(pr);
                return request;
            }
        }else{
            return request;
        }
   
        return response(null, ServiceStatusEnum.EXCEPCION, null, null);
        
    }
    
}
