/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.service.BaseService;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.Adjunto;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.Correo;
import mx.gob.imss.dpes.promotorfront.restclient.CorreoClient;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author gabriel.rios
 */
@Provider
public class CorreoComponentService extends BaseService {

    @Inject
    @RestClient
    private CorreoClient correoClient;
    @Inject
    private Config config;

    protected void sendEmailWithAttach(String plantilla, String asunto, ArrayList<String> correos, ArrayList<Adjunto> adjuntos) {
        log.log(Level.INFO, ">>>CorreoComponentService sendEmail Init ");
//        int i=0;
//        if(isTest){
//            correos = new ArrayList();
//            correos.add("gabriel.rios@softtek.com");
//        }
        for (String to : correos) {
            log.log(Level.INFO, ">>>Correos {0}", to);
        }

        Correo correo = new Correo();

        try {
            correo.setCuerpoCorreo(new String(plantilla.getBytes(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CorreoComponentService.class.getName()).log(Level.SEVERE, null, ex);
        }

        correo.setAsunto(asunto);

        correo.setCorreoPara(correos);
        if (adjuntos != null && adjuntos.size() > 0) {
            correo.setAdjuntos(adjuntos);
        }
        Response response = correoClient.enviaCorreo(correo);

        if (response.getStatus() == 200 || response.getStatus() == 204) {
            log.log(Level.INFO, ">>>>>>CorreoComponentService sendEmail SE ENVIO CORREO DE AVISO={0}", response.getStatus());

        }
    }

    protected void sendEmail(String plantilla, String asunto, ArrayList<String> correos) {
        ArrayList<Adjunto> adjuntos = new ArrayList();
        sendEmailWithAttach(plantilla, asunto, correos, adjuntos);

    }
}
