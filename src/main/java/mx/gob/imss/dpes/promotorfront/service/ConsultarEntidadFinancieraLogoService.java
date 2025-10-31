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
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.EntidadFinanciera;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.Oferta;
import mx.gob.imss.dpes.promotorfront.model.EntidadFinancieraLogo;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.EntidadFinancieraFrontClient;

import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author salvador.pocteco
 */
@Provider
public class ConsultarEntidadFinancieraLogoService
    extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private EntidadFinancieraFrontClient entidadFinancieraFrontClient;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        //log.log(Level.INFO, "getPayload.getEntidadFinanciera() LOGO: {0}",
        //  request.getPayload().getPersonaEF().getEntidadFinanciera());
        Oferta oferta = null;

        try {
            oferta = request.getPayload().getOferta();

            if(oferta == null)
                oferta = new Oferta();

            Response imgEF = entidadFinancieraFrontClient.obtieneLogo(
                request.getPayload().getPersonaEF().getEntidadFinanciera().getId());

            if (imgEF.getStatus() == Response.Status.OK.getStatusCode()) {
                EntidadFinancieraLogo efl = imgEF.readEntity(EntidadFinancieraLogo.class);

                EntidadFinanciera ef = new EntidadFinanciera();
                ef.setImgB64(efl.getArchivo());

                oferta.setEntidadFinanciera(ef);

                request.getPayload().setOferta(oferta);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR ConsultarEntidadFinancieraLogoService.execute - [" + request + "]", e);
            oferta = new Oferta();
            request.getPayload().setOferta(oferta);
        }

        return new Message<>(request.getPayload());
    }
}

/*
@Provider
public class ConsultarEntidadFinancieraService extends ServiceDefinition<EntidadFinancieraRequest, EntidadFinancieraRequest> {

    @Inject
    @RestClient
    private EntidadFinancieraClient entidadFinancieraClient;

    @Override
    public Message<EntidadFinancieraRequest> execute(Message<EntidadFinancieraRequest> request) throws BusinessException {

        CondicionOfertaRequest condicionOfertaRequest = new CondicionOfertaRequest();
        condicionOfertaRequest.setClave(request.getPayload().getCondicionOfertaRequest().getClave());
        request.getPayload().setCondicionOfertaRequest(condicionOfertaRequest);

        log.log(Level.INFO, "El request de la condicion oferta es : {0}", request.getPayload().getCondicionOfertaRequest());
        Response respuesta = entidadFinancieraClient.load(request.getPayload().getCondicionOfertaRequest());

        if (respuesta.getStatus() == 200) {
            Oferta oferta = respuesta.readEntity(Oferta.class);
            request.getPayload().setOferta(oferta);
            log.log(Level.INFO, "Los datos de la condicion oferta y entidad financiera son : {0}", request.getPayload().getOferta());

            return new Message<>(request.getPayload());
        }
        return response(null, ServiceStatusEnum.EXCEPCION, new ResumenSimulacionException(), null);
    }
}
 */
