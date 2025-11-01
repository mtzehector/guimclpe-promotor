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
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.PersonaResponse;
import mx.gob.imss.dpes.promotorfront.exeption.OperadorNoExistenteExeption;
import mx.gob.imss.dpes.promotorfront.exeption.PromotorNoExistenteExeption;
import mx.gob.imss.dpes.promotorfront.model.DocumentoRq;
import mx.gob.imss.dpes.promotorfront.model.DocumentoRs;
import mx.gob.imss.dpes.promotorfront.model.RequestPromotorModel;
import mx.gob.imss.dpes.promotorfront.restclient.DocumentoBackClient;
import mx.gob.imss.dpes.promotorfront.restclient.DocumentoFrontClient;
import mx.gob.imss.dpes.promotorfront.restclient.PersonaFrontClient;
import mx.gob.imss.dpes.promotorfront.restclient.PromotorClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author osiris.hernandez
 */
@Provider
public class PromotorPorCvePersonalEFService extends ServiceDefinition<RequestPromotorModel, RequestPromotorModel> {

    @Inject
    @RestClient
    private PromotorClient service;

    @Inject
    @RestClient
    private PersonaFrontClient personaFrontClient;

    @Inject
    @RestClient
    private DocumentoBackClient docBackClient;

    @Inject
    @RestClient
    private DocumentoFrontClient docFrontClient;

    @Override
    public Message<RequestPromotorModel> execute(Message<RequestPromotorModel> request) throws BusinessException {
        //log.log(Level.INFO, ">>>>promotorFront PromotorService.execute request.getPayload()= {0}", request.getPayload());
        Response load = null;

        try {
            request.getPayload().setTipoPersonaEF(TipoPersonaEFEnum.PROMOTOR);
            load = service.obtenerPorClave(request.getPayload());
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorPorCvePersonalEFService.execute = {0}", e);
            return response(null, ServiceStatusEnum.EXCEPCION, new PromotorNoExistenteExeption(), null);
        }

        if (!(load != null && load.getStatus() == Response.Status.OK.getStatusCode()))
            return response(null, ServiceStatusEnum.EXCEPCION, new PromotorNoExistenteExeption(), null);

        RequestPromotorModel response = null;
        PersonaResponse pr = null;

        try {
            response = load.readEntity(RequestPromotorModel.class);
            Response loadP = personaFrontClient.load(response.getCurp(), response.getCveEntidadFinanciera());
            pr = loadP.readEntity(PersonaResponse.class);

            response.setNombre(pr.getNombre());
            response.setPrimerApellido(pr.getPrimerApellido());
            response.setSegundoApellido(pr.getSegundoApellido());
            response.setCorreoElectronico(pr.getCorreoElectronico());
            response.setTelefono(pr.getTelCelular());
            response.setCvePersona(pr.getCvePersona());
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PromotorPorCvePersonalEFService.execute = {0}", e);
            return response(null, ServiceStatusEnum.EXCEPCION, new PromotorNoExistenteExeption(), null);
        }

        try {
            Response responseDocBack = docBackClient.load(new DocumentoRq(pr.getCvePersona(), 9L));
            DocumentoRs documento = responseDocBack.readEntity(DocumentoRs.class);

            Response responseDocFront = docFrontClient.load(documento.getId());
            documento = responseDocFront.readEntity(DocumentoRs.class);
            response.setImgB64(documento.getArchivo());
        } catch (Exception e) {
            log.log(Level.WARNING, "ERROR PromotorPorCvePersonalEFService.execute = {0}", e);
            response.setImgB64(null);
        }

        //log.log(Level.INFO, ">>>>promotorFront PromotorService.execute response= {0}", response);
        return new Message<>(response);
    }
    
/*
    public Message<RequestPromotorModel> PromoNoImage(Message<RequestPromotorModel> request) throws BusinessException {
        request.getPayload().setTipoPersonaEF(TipoPersonaEFEnum.PROMOTOR);
        log.log(Level.INFO, ">>>>promotorFront PromotorService.execute request.getPayload()= {0}", request.getPayload());
        Response load = service.obtenerPorClave(request.getPayload());

        if (load.getStatus() == 200) {
            RequestPromotorModel response = load.readEntity(RequestPromotorModel.class);

            Response loadP = personaFrontClient.loadExcel(response.getCurp(), response.getCveEntidadFinanciera());
            PersonaResponse pr = loadP.readEntity(PersonaResponse.class);

            response.setNombre(pr.getNombre());
            response.setPrimerApellido(pr.getPrimerApellido());
            response.setSegundoApellido(pr.getSegundoApellido());
            response.setCorreoElectronico(pr.getCorreoElectronico());
            response.setTelefono(pr.getTelCelular());
            response.setCvePersona(pr.getCvePersona());
            log.log(Level.INFO, ">>>>promotorFront PromotorService.execute response= {0}", response);
            return new Message<>(response);
        }
        return response(null, ServiceStatusEnum.EXCEPCION, new PromotorNoExistenteExeption(), null);
    }
*/
}
