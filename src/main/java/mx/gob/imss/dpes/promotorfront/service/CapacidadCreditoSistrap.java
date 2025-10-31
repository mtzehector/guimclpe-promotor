/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import mx.gob.imss.dpes.common.constants.Constantes;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.exception.VariableMessageException;
import mx.gob.imss.dpes.common.model.ErrorInfo;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.sipre.model.Capacidad;
import mx.gob.imss.dpes.interfaces.sipre.model.ConsultaCapacidadRequestAux;
import mx.gob.imss.dpes.promotorfront.exeption.PromotorEndPointExeption;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.CapacidadCreditoSistrapClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.logging.Level;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class CapacidadCreditoSistrap extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private CapacidadCreditoSistrapClient capacidadCreditoSistrapClient;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            //log.log(Level.INFO, ">>>promotorfront|CapacidadCreditoSistrap: {0}", request.getPayload());

            ConsultaCapacidadRequestAux cc = new ConsultaCapacidadRequestAux();
            cc.setGrupoFamiliar(request.getPayload().getPensionado().getIdGrupoFamiliar());
            cc.setNss(request.getPayload().getPensionado().getIdNss());
            cc.setSesion(request.getPayload().getSesion());
            //log.log(Level.INFO, ">>>promotorfront|CapacidadCreditoSistrap ConsultaCapacidadRequestAux: {0}", cc);

            Response capacidadCreditoSistrapResponse = capacidadCreditoSistrapClient.load(
                    (Constantes.BEARER + Constantes.ESPACIO_BLANCO + request.getPayload().getToken()) ,cc);

            if (capacidadCreditoSistrapResponse.getStatus() == Response.Status.PARTIAL_CONTENT.getStatusCode())
                throw new VariableMessageException((capacidadCreditoSistrapResponse.readEntity(ErrorInfo.class)).
                    getMessage());

            if (capacidadCreditoSistrapResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                Capacidad capacidad = capacidadCreditoSistrapResponse.readEntity(new GenericType<Capacidad>() {});

                if (capacidad != null) {
                    request.getPayload().setCapacidad(capacidad);
                    return new Message<>(request.getPayload());
                }
            }
        } catch(VariableMessageException e) {
            log.log(Level.SEVERE, "ERROR CapacidadCreditoSistrap.execute = [" + request + "]", e);
            throw e;
        } catch(Exception e) {
            log.log(Level.SEVERE, "ERROR CapacidadCreditoSistrap.execute = [" + request + "]", e);
        }

        throw new PromotorEndPointExeption();
    }
}
