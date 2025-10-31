package mx.gob.imss.dpes.promotorfront.service;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.exception.VariableMessageException;
import mx.gob.imss.dpes.common.model.ErrorInfo;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.sipre.model.ConsultaEstatusRequest;
import mx.gob.imss.dpes.interfaces.sipre.model.ConsultaEstatusResponse;
import mx.gob.imss.dpes.promotorfront.exeption.PromotorEndPointExeption;
import mx.gob.imss.dpes.promotorfront.exeption.SistrapException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.ConsultaEstatusPrestamoReinstalacionSistrapClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class ConsultaEstatusPrestamoReinstalacionSistrap extends
    ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private ConsultaEstatusPrestamoReinstalacionSistrapClient consultaEstatusPrestamoReinstalacionSistrapClient;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            ConsultaEstatusRequest consultaEstatusRequest = new ConsultaEstatusRequest();
            consultaEstatusRequest.setIdPrestamo(request.getPayload().getIdSolPrestamo());

            Response respuesta =
                consultaEstatusPrestamoReinstalacionSistrapClient.obtenerEstatus(consultaEstatusRequest);

            if (respuesta.getStatus() == Response.Status.PARTIAL_CONTENT.getStatusCode())
                throw new VariableMessageException((respuesta.readEntity(ErrorInfo.class)).getMessage());

            if (respuesta.getStatus() == Response.Status.OK.getStatusCode()) {
                ConsultaEstatusResponse response =
                    respuesta.readEntity(ConsultaEstatusResponse.class);

                String idMovimiento = response.getConsultaStatusPrestamo().getIdMovimineto();

                if(
                    !(
                        "PO".equals(idMovimiento) || //Préstamo Otorgado
                        "RP".equals(idMovimiento) //Prestamo en recuperación
                    )
                ) {
                    throw new VariableMessageException(
                        "El préstamo no es reinstalable.");
                }

                return request;
            }

        } catch(VariableMessageException e) {
            log.log(Level.SEVERE,
                "ERROR ConsultaEstatusPrestamoReinstalacionSistrap.execute = [" + request + "]", e);
            throw e;
        } catch(Exception e) {
            log.log(Level.SEVERE,
                "ERROR ConsultaEstatusPrestamoReinstalacionSistrap.execute = [" + request + "]", e);
        }

        throw new PromotorEndPointExeption();
    }

}
