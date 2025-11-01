/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.exception.VariableMessageException;
import mx.gob.imss.dpes.common.model.ErrorInfo;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.sistrap.model.Pension;
import mx.gob.imss.dpes.interfaces.sistrap.model.Pensionado;
import mx.gob.imss.dpes.interfaces.sistrap.model.PensionadoRequest;
import mx.gob.imss.dpes.promotorfront.exeption.PromotorEndPointExeption;
import mx.gob.imss.dpes.promotorfront.exeption.RenapoVsSistrapNoMacthingException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.PensionadoSistrapClient;
import mx.gob.imss.dpes.promotorfront.rule.CompararRenapoVsSistrap;
import mx.gob.imss.dpes.promotorfront.rule.ValidarEstatusCurp;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.logging.Level;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class PensionadoSistrap extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {
    
    @Inject
    @RestClient
    private PensionadoSistrapClient pensionadoSistrapClient;
    
    @Inject
    CompararRenapoVsSistrap rule1;
    
    @Inject
    ValidarEstatusCurp rule2;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            //log.log(Level.INFO,">>>promotorfront|PensionadoSistrap: {0}", request.getPayload());
            String tipoPension = null;

            PensionadoRequest pr = new PensionadoRequest();
            pr.setIdNss(request.getPayload().getNss());

            for (Pension p : request.getPayload().getPensiones().getPensiones()) {
                if (p.getIdNss().equals(pr.getIdNss())) {
                    pr.setIdGrupoFamiliar(p.getIdGrupoFamiliar());
                    tipoPension = p.getDesTipoPension();

                    break;
                }
            }

            Response pensionadoSistrapResponse = pensionadoSistrapClient.load(pr);

            if (pensionadoSistrapResponse.getStatus() == Response.Status.PARTIAL_CONTENT.getStatusCode())
                throw new VariableMessageException((pensionadoSistrapResponse.readEntity(ErrorInfo.class)).
                    getMessage());

            if (pensionadoSistrapResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                Pensionado response = pensionadoSistrapResponse.readEntity(Pensionado.class);

                request.getPayload().setPensionado(response);
                request.getPayload().getPensionado().setDescTipoPension(tipoPension);

                rule1.apply(request.getPayload());
                if (request.getPayload().getFlagError())
                    throw new VariableMessageException(
                        "Existen diferencias en la información del pensionado y RENAPO, favor de revisar.");

                rule2.apply(request.getPayload());
                return new Message<>(request.getPayload());
            }

        } catch(VariableMessageException e) {
            log.log(Level.SEVERE, "ERROR PensionadoSistrap.execute = [" + request + "]", e);
            throw e;
        } catch(BusinessException e) {
            throw e;
        } catch(Exception e) {
            log.log(Level.SEVERE, "ERROR PensionadoSistrap.execute = [" + request + "]", e);
        }

        throw new PromotorEndPointExeption();
    }
    
}
