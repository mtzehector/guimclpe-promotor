/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.service;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.ext.Provider;
import javax.inject.Inject;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.promotorfront.restclient.CorreoClient;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.Correo;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.gob.imss.dpes.interfaces.prestamo.model.PrestamoRecuperacion;
import org.eclipse.microprofile.config.Config;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.Adjunto;
import mx.gob.imss.dpes.common.enums.TipoCreditoEnum;
/**
 *
 * @author juanf.barragan
 */

@Provider
public class CorreoMontoLiquidarService extends ServiceDefinition<PromotorPrestamoModel, PromotorPrestamoModel>{
    @Inject
    @RestClient
    private CorreoClient correoClient;
    
    @Inject
    private Config config;

    @Override
    public Message<PromotorPrestamoModel> execute(Message<PromotorPrestamoModel> request) throws BusinessException {
        log.log(Level.INFO, ">>>promotorfront|CorreoMontoLiquidarService {0}", request.getPayload());
        
        if(request.getPayload().getPrestamo().getTipoCredito().equals(TipoCreditoEnum.MIXTO)||
           request.getPayload().getPrestamo().getTipoCredito().equals(TipoCreditoEnum.RENOVACION)){
            Correo correo = new Correo();
            String entidadFinanciera = request.getPayload().getPersonaEF().getEntidadFinanciera().getNombreComercial();

            String nombre = request.getPayload().getPersona().getNombre();
            String pApellido = request.getPayload().getPersona().getPrimerApellido();
            String sApellido = request.getPayload().getPersona().getSegundoApellido();
            String pensionado = nombre+" "+pApellido+" "+sApellido;

            String nss = request.getPayload().getSolicitud().getNss();
            String curp= request.getPayload().getSolicitud().getCurp();
            
            double monto = 0;
            String ef= "";
            String correoAdmin = "";
            for(PrestamoRecuperacion p : request.getPayload().getPrestamosRecuperacionArreglo()){
                if(p.getNumEntidadFinanciera().equalsIgnoreCase(request.getPayload().getPersonaEF().getEntidadFinanciera().getCveEntidadFinancieraSipre())){
                    ef = p.getNombreComercial();
                    monto += p.getCanMontoSol();
                    correoAdmin = p.getCorreoAdminEF();
                }                
            }
            
            String plantilla = String.format(config.getValue("plantillaMontoLiquidar", String.class),
                                                ef,nss,curp,pensionado,entidadFinanciera,monto);
            
            try {
                correo.setCuerpoCorreo(new String(plantilla.getBytes(), StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(CorreoService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
                       
            correo.setAsunto("Confirmación de Monto a Liquidar");
            
            ArrayList<String> correos = new ArrayList<>();
            correos.add(request.getPayload().getPersona().getCorreoElectronico());
            correos.add(correoAdmin);
            correo.setCorreoPara(correos);
            
            ArrayList<Adjunto> adjuntos = new ArrayList<Adjunto>();
            correo.setAdjuntos(adjuntos);
         
            Response respuesta = correoClient.enviaCorreo(correo);
            
            if(respuesta.getStatus() == 200 || respuesta.getStatus() == 204) {
                log.log(Level.INFO, ">>>>>>SE ENVIO CORREO DE CONFIRMACION DEL MONTO A LIQUIDAR={0}", respuesta.getStatus());
                return request;
            }
            return request;
        }else{
            return request;
        }
    }
    
}
