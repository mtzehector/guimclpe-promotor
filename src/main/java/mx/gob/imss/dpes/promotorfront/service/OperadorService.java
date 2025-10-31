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
import mx.gob.imss.dpes.common.enums.TipoPersonaEFEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.promotorfront.exeption.OperadorNoExistenteExeption;
import mx.gob.imss.dpes.promotorfront.model.RequestPromotorModel;
import mx.gob.imss.dpes.promotorfront.restclient.PromotorClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author osiris.hernandez
 */
@Provider
public class OperadorService extends ServiceDefinition<RequestPromotorModel, RequestPromotorModel> {
    
    @Inject
    @RestClient
    private PromotorClient service;

    @Override
    public Message<RequestPromotorModel> execute(Message<RequestPromotorModel> request) throws BusinessException {
        request.getPayload().setTipoPersonaEF(TipoPersonaEFEnum.OPERADOR_ENTIDAD_FINANCIERA);
        log.log( Level.INFO, ">>> OperadorService.execute Request hacia PromotorBack: {0}", request.getPayload());
        try {
            Response load = service.obtenerPorCurpOP(request.getPayload());
            log.log( Level.INFO, ">>> OperadorService.execute Sale de  PromotorBack: {0}", request.getPayload());
            if (load != null && load.getStatus() == 200) {
                RequestPromotorModel response = load.readEntity(RequestPromotorModel.class);
                return new Message<>(response);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, ">>>>ERROR!!! OperadorService = {0}", e);
        }
        return response(null, ServiceStatusEnum.EXCEPCION, new OperadorNoExistenteExeption(), null);
    }
}
