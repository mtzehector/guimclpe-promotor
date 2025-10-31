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
import mx.gob.imss.dpes.interfaces.sistrap.model.ConsultaPensionesResponse;
import mx.gob.imss.dpes.interfaces.sistrap.model.CurpRequestAux;
import mx.gob.imss.dpes.interfaces.sistrap.model.Pension;
import mx.gob.imss.dpes.promotorfront.exeption.PromotorEndPointExeption;
import mx.gob.imss.dpes.promotorfront.exeption.SistrapException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.PensionSistrapClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.logging.Level;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class PensionSistrap extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private PensionSistrapClient pensionSistrapClient;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            //log.log(Level.INFO, ">>>promotorfront|PensionSistrap|execute: {0}", request.getPayload());

            CurpRequestAux cr = new CurpRequestAux();
            cr.setCurp(request.getPayload().getCurp());
            cr.setSesion(request.getPayload().getSesion());
/*
            Response load = pensionSistrapClient.load(cr);
            ConsultaPensionesResponse pensiones = load.readEntity(ConsultaPensionesResponse.class);
            switch (pensiones.getCodigoError()) {
                case "401"://Curp incorrecta
                    throw new SistrapException("msg360");
                case "402"://Sin pensiones vigentes
                    throw new SistrapException("msg361");
                case "407"://no cuenta con nomina vigente o no fue localizado.
                    throw new SistrapException("msg362");

                case "200":
                    boolean pensionOK = false;
                    for (Pension p : pensiones.getPensiones()) {
                        if (request.getPayload().getNss().equals(p.getIdNss())) {
                            pensionOK = true;
                        }
                    }
                    if (!pensionOK) {
                        throw new SistrapException("msg356");
                    }
                    request.getPayload().setPensiones(pensiones);
                    break;
                default:
                    throw new SistrapException();
            }
*/

            Response respuesta = pensionSistrapClient.load(cr);

            if (respuesta.getStatus() == Response.Status.PARTIAL_CONTENT.getStatusCode())
                throw new VariableMessageException((respuesta.readEntity(ErrorInfo.class)).getMessage());

            if (respuesta.getStatus() == Response.Status.OK.getStatusCode()) {
                ConsultaPensionesResponse consultaPensionesResponse =
                    respuesta.readEntity(ConsultaPensionesResponse.class);

                boolean pensionOK = false;
                for (Pension p : consultaPensionesResponse.getPensiones()) {
                    if (request.getPayload().getNss().equals(p.getIdNss())) {
                        pensionOK = true;
                        break;
                    }
                }

                if (!pensionOK)
                    throw new VariableMessageException(
                            "El pensionado no cuenta con una pensión vigente, favor de revisar.");

                request.getPayload().setPensiones(consultaPensionesResponse);

                return request;
            }

        } catch(VariableMessageException e) {
            log.log(Level.SEVERE, "ERROR PensionSistrap.execute = [" + request + "]", e);
            throw e;
        } catch(Exception e) {
            log.log(Level.SEVERE, "ERROR PensionSistrap.execute = [" + request + "]", e);
        }

        throw new PromotorEndPointExeption();
    }

}
