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
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.CondicionesEntidadFinanciera;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesDescuentoException;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesEFException;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesEFMonotoMinimoException;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesEFMontoMaximoException;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesTasaException;
import mx.gob.imss.dpes.promotorfront.model.CondicionesOfertaModel;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.CondicionesClient;
import mx.gob.imss.dpes.promotorfront.rule.CondicionesDelPrestamo;
import mx.gob.imss.dpes.promotorfront.rule.CondicionesEF;
//import mx.gob.imss.dpes.promotorfront.rule.FechaNominal;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class CondicionesPrestamoService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    //@Inject
    //FechaNominal fechaNominalRule;

    @Inject
    CondicionesDelPrestamo condicionesPrestamoRule;

    @Inject
    CondicionesEF condicionesEFRule;

    @Inject
    @RestClient
    CondicionesClient client;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        //log.log(Level.INFO, ">>>prestamoFront|CondicionesPrestamoService|execute: {0}", request.getPayload());
        try {
            if (request.getPayload().getPrestamosRecuperacionArreglo() != null &&
                    !request.getPayload().getPrestamosRecuperacionArreglo().isEmpty()) {

                CondicionesEntidadFinanciera condicionesEF = new CondicionesEntidadFinanciera();
                condicionesEF.setCveEntidadFinanciera(
                    request.getPayload().getPersonaEF().getEntidadFinanciera().getId());
                condicionesEF.setCveSexo(Integer.parseInt(request.getPayload().getPensionado().getSexo()));
                condicionesEF.setCveDelegacion(Long.parseLong(request.getPayload().getPensionado().getCveDelegacion()));

                Response response = client.validarCondicionesEF(condicionesEF);

                if (response.getStatus() == 200) {
                    CondicionesEntidadFinanciera cef = response.readEntity(CondicionesEntidadFinanciera.class);
                    request.getPayload().setCondicionesEF(cef);
                    request.getPayload().setError("0");

                    condicionesEFRule.apply(request.getPayload());

                    if (!"0".equals(request.getPayload().getError())) {
                        switch (request.getPayload().getError()) {
                            case "msg359":
                                return response(null, ServiceStatusEnum.EXCEPCION,
                                    new CondicionesEFMontoMaximoException(
                                            request.getPayload().getError()), null);
                            case "msg358":
                                return response(null, ServiceStatusEnum.EXCEPCION,
                                    new CondicionesEFMonotoMinimoException(
                                        request.getPayload().getError()), null);
                            case "msg357":
                                return response(null, ServiceStatusEnum.EXCEPCION,
                                    new CondicionesEFException(request.getPayload().getError()), null);
                            default:
                                return response(null, ServiceStatusEnum.EXCEPCION,
                                    new BusinessException(), null);
                        }
                    }
                } else {
                    log.log(Level.SEVERE, ">>>CondicionesPrestamoService.execute - request = [" + request + "]");
                    return response(null, ServiceStatusEnum.EXCEPCION, new CondicionesEFException(), null);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, ">>>CondicionesPrestamoService.execute - request = [" + request + "]");
            return response(null, ServiceStatusEnum.EXCEPCION, new CondicionesEFException(), null);
        }

        CondicionesOfertaModel co = new CondicionesOfertaModel();
        co.setPlazo(request.getPayload().getPersonaRequest().getPrestamo().getPlazo().getId());
        co.setEntidadFinanciera(request.getPayload().getPersonaEF().getEntidadFinanciera().getId());

        Response load = client.load(co);

        if (load.getStatus() == 200) {
            CondicionesOfertaModel condiciones = load.readEntity(CondicionesOfertaModel.class);
            request.getPayload().setCondiciones(condiciones);
            request.getPayload().setError("0");
            //fechaNominalRule.apply(request.getPayload());
            condicionesPrestamoRule.apply(request.getPayload());
            if (!"0".equals(request.getPayload().getError())) {
                switch (request.getPayload().getError()) {
                    case "err003":
                        return response(null, ServiceStatusEnum.EXCEPCION,
                            new CondicionesTasaException(request.getPayload().getError()), null);
                    case "msg00006":
                    case "msg009":
                        return response(null, ServiceStatusEnum.EXCEPCION,
                            new CondicionesDescuentoException(request.getPayload().getError()), null);
                    default:
                        return response(null, ServiceStatusEnum.EXCEPCION,
                            new BusinessException(), null);
                }
            }
            return request;

        }

        return request;
    }

}
