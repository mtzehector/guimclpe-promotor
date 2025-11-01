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
import mx.gob.imss.dpes.common.exception.VariableMessageException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;

import mx.gob.imss.dpes.promotorfront.exeption.PromotorEndPointExeption;
import mx.gob.imss.dpes.promotorfront.exeption.SolicitudVigenteException;
import mx.gob.imss.dpes.promotorfront.model.PersonaModel;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.ConsultaPensionadoClient;
import mx.gob.imss.dpes.promotorfront.rule.ComparaRenapoVsSipre;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class PersonaService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private ConsultaPensionadoClient consultaPensionadoClient;
    @Inject
    private ComparaRenapoVsSipre rule1;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        //log.log(Level.INFO, ">>>promotorfront|PersonaService: {0}", request.getPayload());
        Response consultaPensionadoResponse = null;

        try {
            consultaPensionadoResponse =
                consultaPensionadoClient.load(request.getPayload().getPensionado().getCveCurp());
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PersonaService.execute = [" + request + "]", e);
            throw new PromotorEndPointExeption();
        }

        try {
            if (consultaPensionadoResponse != null &&
                    consultaPensionadoResponse.getStatus() == Response.Status.OK.getStatusCode()) {

                PersonaModel persona = consultaPensionadoResponse.readEntity(PersonaModel.class);
                //log.log(Level.INFO, ">>>promotorfront|PersonaService|Response: {0}", persona);

                if (persona != null && persona.getCorreoElectronico() != null) {
                    request.getPayload().setPersona(persona);
                    rule1.apply(request.getPayload());
                } else
                    request.getPayload().setPersona(this.obtainPersonModelDefault());
            } else
                request.getPayload().setPersona(this.obtainPersonModelDefault());

            return request;
        } catch(BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PersonaService.execute = [" + request + "]", e);
        }

        throw new PromotorEndPointExeption();
    }

    private PersonaModel obtainPersonModelDefault() {
        PersonaModel persona = new PersonaModel();
        persona.setCorreoElectronico("sinregistro");

        return persona;
    }
}
