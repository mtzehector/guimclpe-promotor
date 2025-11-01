/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.renapo.model.RenapoCurpIn;
import mx.gob.imss.dpes.interfaces.renapo.model.RenapoCurpRequest;
import mx.gob.imss.dpes.promotorfront.exeption.CurpInvalidStatusException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.renapo.exception.RenapoCurpException;
import mx.gob.imss.dpes.renapo.service.RenapoCurpService;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class RenapoService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {
    
    @Inject
    RenapoCurpService renapo;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            //log.log(Level.INFO,">>>promotorfront|RenapoService: {0}", request.getPayload());
            RenapoCurpIn in = new RenapoCurpIn();
            in.setCurp(request.getPayload().getCurp());

            RenapoCurpRequest renapoRequest = new RenapoCurpRequest();
            renapoRequest.setRenapoCurpIn(in);

            Message<RenapoCurpRequest> renapoResponse = renapo.execute(new Message<>(renapoRequest));
            //log.log(Level.INFO, ">>>>  ObtenDatosRenapo renapoResponse= {0}", renapoResponse);

            if (Message.isExito(renapoResponse)) {
                String estatusCURP = renapoResponse.getPayload().getRenapoCurpOut().getEstatusCURP();
                if (!("AN".equals(estatusCURP)
                        || "AH".equals(estatusCURP)
                        || "CRA".equals(estatusCURP)
                        || "RCN".equals(estatusCURP)
                        || "RCC".equals(estatusCURP))) {

                    List parameters = new LinkedList();
                    parameters.add(renapoResponse.getPayload().getRenapoCurpOut().getDesEstatusCURP());
                    throw new CurpInvalidStatusException(parameters);
                }

                request.getPayload().setRenapoRequest(renapoResponse.getPayload());
                return request;
            }
            //log.log(Level.SEVERE, "!!!   ERROR >>>>ObtenDatosRenapo Request: {0}", request.getPayload());
        } catch(BusinessException e) {
            throw e;
        } catch(Exception e) {
            log.log(Level.SEVERE, "ERROR RenapoService.execute = [" + request + "]", e);
        }

        throw new RenapoCurpException();
    }
}
