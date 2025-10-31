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
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.prestamo.model.AmortizacionInsumos;
import mx.gob.imss.dpes.promotorfront.assembler.AmortizacionDatosBaseAssembler;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.AmortizacionClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class AmortizacionDatosBaseService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {
    
    @Inject
    @RestClient
    private AmortizacionClient client;
    
    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            //log.log(Level.INFO, ">>>Datos Base Amortizacion {0}", request.getPayload());
            AmortizacionDatosBaseAssembler assembler = new AmortizacionDatosBaseAssembler();

            AmortizacionInsumos amortizacionInsumos = assembler.assemble(request.getPayload());

            if (amortizacionInsumos == null)
                throw new RegistrarReinstalacionEndPointException(
                    RegistrarReinstalacionEndPointException.ERROR_ENSAMBLADO_AMORTIZACION);

            Response load = client.guardarDatosBaseAmortizacion(amortizacionInsumos);

            if (load.getStatus() == Response.Status.OK.getStatusCode()) {
                AmortizacionInsumos insumos = load.readEntity(AmortizacionInsumos.class);
                request.getPayload().setAmortizacionInsumos(insumos);
                return new Message<>(request.getPayload());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR AmortizacionDatosBaseService.execute: [" + request + "]", e);
        }

        throw new RegistrarReinstalacionEndPointException (
            RegistrarReinstalacionEndPointException.ERROR_SERVICIO_AMORTIZACION);
    }
}
