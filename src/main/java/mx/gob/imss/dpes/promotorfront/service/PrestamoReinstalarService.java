package mx.gob.imss.dpes.promotorfront.service;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.prestamo.model.Prestamo;
import mx.gob.imss.dpes.promotorfront.assembler.PrestamoReinstalacionAssembler;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.PrestamoClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;

@Provider
public class PrestamoReinstalarService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel>{
    
    @Inject
    @RestClient
    PrestamoClient client;
    
    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        try {
            PrestamoReinstalacionAssembler assembler = new PrestamoReinstalacionAssembler();
            Prestamo prestamo = assembler.assemble(request.getPayload());

            if(prestamo == null)
                throw new RegistrarReinstalacionEndPointException (
                    RegistrarReinstalacionEndPointException.ERROR_ENSAMBLADO_PRESTAMO_REINSTALACION);

            Response load = client.create(prestamo);

            if (load.getStatus() == Response.Status.OK.getStatusCode()) {
                prestamo = load.readEntity(Prestamo.class);
                request.getPayload().setPrestamo(prestamo);

                return request;
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PrestamoReinstalarService.execute: [" + request + "]", e);
        }

        throw new RegistrarReinstalacionEndPointException (
            RegistrarReinstalacionEndPointException.ERROR_SERVICIO_PRESTAMO_REINSTALACION);
    }

}
