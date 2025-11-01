/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.CorreoSinAdjuntos;
import mx.gob.imss.dpes.promotorfront.exeption.RegistrarReinstalacionEndPointException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.CorreoClient;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class CorreoService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel> {
    
    @Inject
    @RestClient
    CorreoClient correoClient;
    
    @Inject
    private Config config;
    
    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {

        try {
            String liga = "https:///mclprestamos.imss.gob.mx/mclpe/auth/login";
            String entidadFinanciera = request.getPayload().getPersonaEF().getEntidadFinanciera().getRazonSocial();
            String plantilla = String.format(config.getValue("plantillaAvisoInicioSolicitud", String.class),
                    entidadFinanciera, liga);

            CorreoSinAdjuntos correo = new CorreoSinAdjuntos();

            correo.setCuerpoCorreo(new String(plantilla.getBytes(), StandardCharsets.UTF_8.name()));

            correo.setAsunto("Aviso Solicitud de Prestamo");
            ArrayList<String> correos = new ArrayList<>();
            correos.add(request.getPayload().getPersonaRequest().getCorreoElectronico());
            correo.setCorreoPara(correos);

            Response response = correoClient.enviaCorreo(correo);

            if (response.getStatus() == Response.Status.OK.getStatusCode() ||
                response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
                return request;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR CorreoService.execute - [" + request + "]", e);
        }
        
        throw new RegistrarReinstalacionEndPointException(
            RegistrarReinstalacionEndPointException.ERROR_SERVICIO_CORREO);
    }
}
