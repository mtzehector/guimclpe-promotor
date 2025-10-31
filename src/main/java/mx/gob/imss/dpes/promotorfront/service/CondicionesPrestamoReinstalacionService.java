package mx.gob.imss.dpes.promotorfront.service;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.CondicionesEntidadFinanciera;
import mx.gob.imss.dpes.promotorfront.exeption.*;
import mx.gob.imss.dpes.promotorfront.model.CondicionesOfertaModel;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.CondicionesClient;
import mx.gob.imss.dpes.promotorfront.rule.CondicionesPrestamoReinstalacionRule;
import mx.gob.imss.dpes.promotorfront.rule.CondicionesReinstalacionRule;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class CondicionesPrestamoReinstalacionService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    CondicionesPrestamoReinstalacionRule condicionesPrestamoReinstalacionRule;

    @Inject
    CondicionesReinstalacionRule condicionesReinstalacionRule;

    @Inject
    @RestClient
    CondicionesClient client;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            if (!(request.getPayload().getPrestamosRecuperacionArreglo() != null &&
                    !request.getPayload().getPrestamosRecuperacionArreglo().isEmpty()))
                throw new CondicionesEFException();

            CondicionesEntidadFinanciera condicionesEF = new CondicionesEntidadFinanciera();
            condicionesEF.setCveEntidadFinanciera(
                request.getPayload().getPersonaEF().getEntidadFinanciera().getId());
            condicionesEF.setCveSexo(Integer.parseInt(request.getPayload().getPensionado().getSexo()));
            condicionesEF.setCveDelegacion(Long.parseLong(request.getPayload().getPensionado().getCveDelegacion()));

            Response response = client.validarCondicionesEF(condicionesEF);

            if (response != null && response.getStatus() == Response.Status.OK.getStatusCode()) {
                CondicionesEntidadFinanciera cef = response.readEntity(CondicionesEntidadFinanciera.class);
                request.getPayload().setCondicionesEF(cef);
                request.getPayload().setError("0");

                if (condicionesReinstalacionRule.apply(request.getPayload()) == null)
                    throw new CondicionesEFException();

                if (!"0".equals(request.getPayload().getError())) {
                    switch (request.getPayload().getError()) {
                        case "msg359":
                            throw new CondicionesEFMontoMaximoException(request.getPayload().getError());
                        case "msg358":
                            throw new CondicionesEFMonotoMinimoException(request.getPayload().getError());
                        case "msg357":
                            throw new CondicionesEFException(request.getPayload().getError());
                        default:
                            throw new BusinessException();
                    }
                }

            } else
                throw new CondicionesEFException();

        } catch (BusinessException e) {
            log.log(Level.SEVERE, "ERROR CondicionesPrestamoReinstalacionService.execute - request = [" +
                request + "]", e);
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR CondicionesPrestamoReinstalacionService.execute - request = [" +
                request + "]", e);
            throw new CondicionesEFException();
        }

        try {
            CondicionesOfertaModel co = new CondicionesOfertaModel();
            co.setPlazo(request.getPayload().getPersonaRequest().getPrestamo().getPlazo().getId());
            co.setEntidadFinanciera(request.getPayload().getPersonaEF().getEntidadFinanciera().getId());

            Response load = client.load(co);

            if (load != null && load.getStatus() == Response.Status.OK.getStatusCode()) {
                CondicionesOfertaModel condiciones = load.readEntity(CondicionesOfertaModel.class);
                request.getPayload().setCondiciones(condiciones);
                request.getPayload().setError("0");

                condicionesPrestamoReinstalacionRule.apply(request.getPayload());
                if (!"0".equals(request.getPayload().getError())) {
                    switch (request.getPayload().getError()) {
                        case "err007":
                            throw new CondicionesTasaException(request.getPayload().getError());
                        case "msg365":
                            throw new CondicionesDescuentoException(request.getPayload().getError());
                        default:
                            throw new BusinessException();
                    }
                }

                return request;
            }
        } catch (BusinessException e) {
            log.log(Level.SEVERE, "ERROR CondicionesPrestamoReinstalacionService.execute - request = [" +
                request + "]", e);
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR CondicionesPrestamoReinstalacionService.execute - request = [" +
                    request + "]", e);
            throw new CondicionesEFException();
        }

        throw new CondicionesEFException();
    }
}
