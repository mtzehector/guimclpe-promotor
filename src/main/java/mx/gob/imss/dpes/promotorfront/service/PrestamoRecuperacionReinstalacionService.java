package mx.gob.imss.dpes.promotorfront.service;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.prestamo.model.PrestamoRecuperacion;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.PrestamoClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Provider
public class PrestamoRecuperacionReinstalacionService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private PrestamoClient client;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            if (!(request.getPayload().getPrestamosRecuperacionArreglo() != null &&
                    !request.getPayload().getPrestamosRecuperacionArreglo().isEmpty()))
                throw new RegistrarReinstalacionEndPointException(
                    RegistrarReinstalacionEndPointException.ERROR_SOLICITUD_SIN_PRESTAMOS_EN_RECUPERACION);

            List<PrestamoRecuperacion> prestamoRecuperacionOut = new ArrayList<>();

            for (PrestamoRecuperacion p : request.getPayload().getPrestamosRecuperacionArreglo()) {
                p.setSolicitud(request.getPayload().getSolicitud().getId());

                p.setPrestamo(request.getPayload().getPrestamo().getId());

                p.setImpRealPrestamo(p.getCanMontoSol());

                p.setCanMontoSol(p.getSaldoCapital());

                p.setNumEntidadFinanciera(p.getNumEntidadFinanciera().trim());

                Response load = client.prestamoEnRecuperacion(p);
                if (load.getStatus() == Response.Status.OK.getStatusCode()) {
                    PrestamoRecuperacion prestamoRecuperacion = load.readEntity(PrestamoRecuperacion.class);
                    prestamoRecuperacion.setCorreoAdminEF(p.getCorreoAdminEF());
                    prestamoRecuperacionOut.add(prestamoRecuperacion);
                }
            }

            request.getPayload().setPrestamosRecuperacionArreglo(prestamoRecuperacionOut);

            return new Message<>(request.getPayload());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR PrestamoRecuperacionReinstalacionService.execute: [" + request + "]", e);
        }

        throw new RegistrarReinstalacionEndPointException(
            RegistrarReinstalacionEndPointException.ERROR_SERVICIO_PRESTAMO_RECUPERACION_REINSTALACION);
    }
}
