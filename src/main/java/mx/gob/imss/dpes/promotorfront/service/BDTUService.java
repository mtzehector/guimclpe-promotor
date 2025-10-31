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
import mx.gob.imss.dpes.common.exception.UnknowException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.bdtu.BDTURequest;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.bdtu.BDTURequestOut;
import mx.gob.imss.dpes.promotorfront.exeption.RenapoBDTUInfoNotMacthingException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.BDTUClient;
import mx.gob.imss.dpes.promotorfront.rule.CompararRenapoVsBdtu;
import mx.gob.imss.dpes.promotorfront.rule.ValidarEstatusCurp;
import mx.gob.imss.dpes.support.util.ExceptionUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class BDTUService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel>{
    
    @Inject
    @RestClient
    BDTUClient bdtuClient;
    
    @Inject
    CompararRenapoVsBdtu rule1;
    
    @Inject
    ValidarEstatusCurp rule2;
    
    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        
        log.log(Level.INFO,">>>promotorfront|BDTUService: {0}", request.getPayload());
        BDTURequest bdtuRequest = new BDTURequest();
        
        Response response = null;
        try {
            response = bdtuClient.load(request.getPayload().getCurp(), request.getPayload().getNss());  
        } catch (RuntimeException e) {
                        
            if (e.getMessage().contains("Unknown error, status code 404") || e.getMessage().contains("Unknown error, status code 502")) {
                ExceptionUtils.throwServiceException("BDTU");
            }
            
            if (e.getMessage().contains("Unknown error, status code 400")) {
                throw new RenapoBDTUInfoNotMacthingException("msg355");
            }
            
            if (e.getMessage().contains("Unknown error, status code 500")) {
                throw new RenapoBDTUInfoNotMacthingException("msg356");
            }
            
            throw new UnknowException();
        }
        
            BDTURequestOut out = response.readEntity(BDTURequestOut.class);
            bdtuRequest.setBdtuOut(out);
            request.getPayload().setBdtuRequest(bdtuRequest);
            rule1.apply(request.getPayload());
            rule2.apply(request.getPayload());
            return request;
        
    }
    
}
