package mx.gob.imss.dpes.promotorfront.service;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.exception.VariableMessageException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.sipre.model.PrestamoDescuentoNoAplicado;
import mx.gob.imss.dpes.promotorfront.exeption.PromotorEndPointExeption;
import mx.gob.imss.dpes.promotorfront.exeption.SistrapException;
import mx.gob.imss.dpes.promotorfront.model.PrestamoVigenteModel;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.PrestamoDescuentoNoAplicadoSistrapClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.logging.Level;

@Provider
public class ConsultaPrestamoDescuentoNoAplicadoSistrap extends
    ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Inject
    @RestClient
    private PrestamoDescuentoNoAplicadoSistrapClient prestamoDescuentoNoAplicadoSistrapClient;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            Response respuesta =
                prestamoDescuentoNoAplicadoSistrapClient.obtenerPrestamoDescuentoNoAplicado(
                    request.getPayload().getIdSolPrestamo());

            if (respuesta.getStatus() == Response.Status.OK.getStatusCode()) {
                PrestamoDescuentoNoAplicado prestamoDescuentoNoAplicado =
                    respuesta.readEntity(PrestamoDescuentoNoAplicado.class);

                BigDecimal descuentoMensual = prestamoDescuentoNoAplicado.getDescuentoMensual();

                BigDecimal impCapacidadTotal = new BigDecimal(
                    request.getPayload().getCapacidad().getImpCapacidadTotal(), MathContext.DECIMAL64);

                impCapacidadTotal = impCapacidadTotal.add(descuentoMensual);

                if (impCapacidadTotal.doubleValue() < 0) {
                    throw new VariableMessageException(
                        "No se cuenta con capacidad suficiente para realizar la reinstalaci\\u00f3n del pr\\u00e9stamo, favor de revisar.");
                }

                PrestamoVigenteModel prestamoVigente = new PrestamoVigenteModel();
                prestamoVigente.setDescripcionEntidadFinanciera(prestamoDescuentoNoAplicado.getEntidadFinancieraSIPRE());
                prestamoVigente.setCat(prestamoDescuentoNoAplicado.getCat().doubleValue());
                prestamoVigente.setMontoSolicitado(prestamoDescuentoNoAplicado.getMontoSolicitado().toString());
                prestamoVigente.setDescuentoMensual(prestamoDescuentoNoAplicado.getDescuentoMensual().toString());
                prestamoVigente.setMensualidadesSinDescuento(
                    prestamoDescuentoNoAplicado.getNumMesesSinRecuperacion().toString());
                prestamoVigente.setDescripcionPlazo(prestamoDescuentoNoAplicado.getPlazo().toString());
                prestamoVigente.setIdSolicitud(prestamoDescuentoNoAplicado.getFolioSipre());
                prestamoVigente.setNombreComercialEF(prestamoDescuentoNoAplicado.getEntidadFInanciera());
                prestamoVigente.setSaldoCapital(prestamoDescuentoNoAplicado.getSaldoCapital().toString());
                prestamoVigente.setNumFolioSolicitud(prestamoDescuentoNoAplicado.getFolio());

                request.getPayload().setPrestamoVigente(prestamoVigente);

                return request;
            }

        } catch(VariableMessageException e) {
            log.log(Level.SEVERE,
                "ERROR ConsultaPrestamoDescuentoNoAplicadoSistrap.execute = [" + request + "]", e);
            throw e;
        } catch(Exception e) {
            log.log(Level.SEVERE,
                "ERROR ConsultaPrestamoDescuentoNoAplicadoSistrap.execute = [" + request + "]", e);
        }

        throw new PromotorEndPointExeption();
    }

}
