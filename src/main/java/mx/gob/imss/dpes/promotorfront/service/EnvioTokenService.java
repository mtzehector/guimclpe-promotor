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
import mx.gob.imss.dpes.common.enums.PerfilUsuarioEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.promotorfront.model.BDTUIn;
import mx.gob.imss.dpes.promotorfront.model.BDTUModel;
import mx.gob.imss.dpes.promotorfront.model.BDTUOut;
import mx.gob.imss.dpes.promotorfront.model.CondicionesOfertaModel;
import mx.gob.imss.dpes.promotorfront.model.PensionadoRequest;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.BDTUClient;
import mx.gob.imss.dpes.promotorfront.restclient.CondicionesClient;
import mx.gob.imss.dpes.promotorfront.restclient.EnviarTokenPensionadoClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class EnvioTokenService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    EnviarTokenPensionadoClient client;

    @Inject
    @RestClient
    CondicionesClient client2;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        log.log(Level.INFO, ">>>promotorFront|EnvioTokenService|execute: {0}", request.getPayload());

        CondicionesOfertaModel co = new CondicionesOfertaModel();
        co.setPlazo(request.getPayload().getPersonaRequest().getPrestamo().getPlazo().getId());
        co.setEntidadFinanciera(request.getPayload().getPersonaEF().getEntidadFinanciera().getId());
        Response load = client2.load(co);
        if (load.getStatus() == 200) {
            CondicionesOfertaModel condiciones = load.readEntity(CondicionesOfertaModel.class);
            request.getPayload().setCondiciones(condiciones);
        }

        if (request.getPayload().getPersonaRequest().getFlat().equals("sinregistro")) {
            PensionadoRequest pensionado = new PensionadoRequest();
            pensionado.setCorreo(request.getPayload().getPersonaRequest().getCorreoElectronico());
            pensionado.setCorreoConfirmar(request.getPayload().getPersonaRequest().getCorreoElectronico());
            pensionado.setCurp(request.getPayload().getPensionado().getCveCurp());
            pensionado.setCvePerfil(PerfilUsuarioEnum.PENSIONADO.toValue());
            pensionado.setNss(request.getPayload().getPensionado().getIdNss());
            if (request.getPayload().getPersonaRequest().getTelCelular() != null && !request.getPayload().getPersonaRequest().getTelCelular().isEmpty()) {
                pensionado.setNumTelefono(Long.parseLong(request.getPayload().getPersonaRequest().getTelCelular()));
            } else {
                pensionado.setNumTelefono(Long.parseLong(request.getPayload().getPersonaRequest().getTelLocal()));
            }

            Response response = client.load(pensionado);
            if (response.getStatus() == 200) {

                return request;
            }
        } else {
            return request;
        }

        return response(request.getPayload(), ServiceStatusEnum.EXCEPCION, null, null);
    }
}
