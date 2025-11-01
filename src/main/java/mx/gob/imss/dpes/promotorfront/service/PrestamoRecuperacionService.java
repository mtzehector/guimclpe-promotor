/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.enums.TipoEstadoSolicitudEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.prestamo.model.PrestamoRecuperacion;
import mx.gob.imss.dpes.interfaces.solicitud.model.EstadoSolicitud;
import mx.gob.imss.dpes.interfaces.solicitud.model.Solicitud;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.PrestamoClient;
import mx.gob.imss.dpes.promotorfront.restclient.SolicitudClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class PrestamoRecuperacionService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private PrestamoClient client;

    @Inject
    @RestClient
    private SolicitudClient solicitudClient;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        log.log(Level.INFO, ">>>promotorfront|PrestamoRecuperacionService {0}", request.getPayload());

        Boolean flatMejorOferta = false;
        // Se ejecuta si contiene prestamos en recuperación.
        if (request.getPayload().getPrestamosRecuperacionArreglo() != null) {

            if (!request.getPayload().getPrestamosRecuperacionArreglo().isEmpty()) {

                List<PrestamoRecuperacion> prestamoRecuperacionOut = new ArrayList<>();

                for (PrestamoRecuperacion p : request.getPayload().getPrestamosRecuperacionArreglo()) {
                    p.setSolicitud(
                            request.getPayload().getSolicitud().getId()
                    );

                    p.setPrestamo(
                            request.getPayload().getPrestamo().getId()
                    );

                    p.setImpRealPrestamo(
                            p.getCanMontoSol()
                    );
                    if(p.getNumEntidadFinanciera().equals(request.getPayload().getPersonaEF().getEntidadFinanciera().getCveEntidadFinancieraSipre())){
                        p.setCanMontoSol(p.getSaldoCapital());
                    }else{
                        p.setCanMontoSol(null);
                    }
                    
                    String cveEFSipre = p.getNumEntidadFinanciera().trim();
                    p.setNumEntidadFinanciera(cveEFSipre);

                    if (p.getMejorOferta() == 1) {
                        flatMejorOferta = true;
                    }
                    Response load = client.prestamoEnRecuperacion(p);
                    if (load.getStatus() == 200) {
                        PrestamoRecuperacion prestamoRecuperacion = load.readEntity(PrestamoRecuperacion.class);
                        prestamoRecuperacion.setCorreoAdminEF(p.getCorreoAdminEF());
                        prestamoRecuperacionOut.add(prestamoRecuperacion);
                    }
                }

                request.getPayload().setPrestamosRecuperacionArreglo(prestamoRecuperacionOut);
                //if (flatMejorOferta) {
                if(request.getPayload().getPrestamo().getTipoCredito().getId() == 3L){
                    EstadoSolicitud edo = new EstadoSolicitud();
                    edo.setId(5L);
                    edo.setDesEstadoSolicitud(
                            TipoEstadoSolicitudEnum.PENDIENTE_MONTO_LIQUIDAR.getDescripcion()
                    );
                    request.getPayload().getSolicitud().setCveEstadoSolicitud(
                            edo
                    );
                }
                return new Message<>(request.getPayload());

            } else {
                return new Message<>(request.getPayload());
            }
        } else {
            return new Message<>(request.getPayload());
        }
    }
}
