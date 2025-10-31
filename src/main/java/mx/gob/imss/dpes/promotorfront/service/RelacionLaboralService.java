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
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.RegistrosPatronalesL;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.ot2.model.RegistrosPatronales;
import mx.gob.imss.dpes.interfaces.relacionlaboral.model.InfoCIRelacionLaboral;
import mx.gob.imss.dpes.interfaces.relacionlaboral.model.RelacionLaboralIn;
import mx.gob.imss.dpes.interfaces.relacionlaboral.model.RelacionLaboralOut;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.EstadoPersonaEf;
import mx.gob.imss.dpes.promotorfront.exeption.RelacionLaboralException;
import mx.gob.imss.dpes.promotorfront.model.BajaPromotorRq;
import mx.gob.imss.dpes.promotorfront.model.BitacoraPersonaRq;
import mx.gob.imss.dpes.promotorfront.model.PersonaModel;
import mx.gob.imss.dpes.promotorfront.model.RequestPromotorModel;
import mx.gob.imss.dpes.promotorfront.restclient.BitacoraBackClient;
import mx.gob.imss.dpes.promotorfront.restclient.EntidadFinancieraClient;
import mx.gob.imss.dpes.promotorfront.restclient.PersonaBackClient;
import mx.gob.imss.dpes.promotorfront.restclient.PersonaFrontClient;
import mx.gob.imss.dpes.promotorfront.restclient.RelacionLaboralClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author juanf.barragan
 */

@Provider
public class RelacionLaboralService extends ServiceDefinition<RequestPromotorModel, RequestPromotorModel>{
    
    @Inject
    @RestClient
    private EntidadFinancieraClient efClient;
    
    @Inject
    @RestClient
    private RelacionLaboralClient rlClient;
    
    @Inject
    @RestClient
    private PersonaBackClient pbClient;
    
    @Inject
    @RestClient
    private PersonaFrontClient pfClient;
    
    @Inject
    @RestClient
    private BitacoraBackClient bbClient;

    @Override
    public Message<RequestPromotorModel> execute(Message<RequestPromotorModel> request) throws BusinessException {
        List<String> response = new ArrayList<String>();
        RelacionLaboralOut responserl;
        RelacionLaboralIn rl = new RelacionLaboralIn();
        List<String> rlActiva = new ArrayList<String>();
        boolean flagRegistro = true;
        
        rl.setNss(request.getPayload().getNss());
        
        Response load = efClient.loadRegistrosPatronales(request.getPayload().getCveEntidadFinanciera());
        
        if (load.getStatus() == 200){
            response = load.readEntity(new GenericType<List<String>>(){});
        }
        Response loadrl = rlClient.load(rl);
        if(loadrl.getStatus() == 200){
            responserl = loadrl.readEntity(RelacionLaboralOut.class);
            if (responserl.getCode().equals("000")) {
                for(InfoCIRelacionLaboral irl : responserl.getListInfoRelacionesLaborales()){
                    if(irl.getLstInfoRelacionesLaborales().getFecFinRelLab().equals("9999-12-31")){
                        rlActiva.add(irl.getLstInfoRelacionesLaborales().getRegPatron());
                    }
                }
            }
        }
        if(rlActiva.size() == 1){
            for(String rlr : response){
                if(rlr.equalsIgnoreCase(rlActiva.get(0))){
                    flagRegistro = false;
                }
            }
        }
        if(flagRegistro){
            throw new RelacionLaboralException();
        }
        
        
        return null;
        
    }
    
    public List<String> validaRelacion(Message<RequestPromotorModel> request)throws BusinessException{
        log.log(Level.INFO, ">>>>> promotorFront RelacionLaboralService.validaRelacion request= {0}", request);
        List<String> validacion = new ArrayList<>();
        List<String> response = new ArrayList<String>();
        RelacionLaboralOut responserl;
        PersonaModel persona;
        RelacionLaboralIn rl = new RelacionLaboralIn();
        List<String> rlActiva = new ArrayList<String>();
        boolean flagRegistro = true;
        
        rl.setNss(request.getPayload().getNss());
        log.log(Level.INFO, ">>>>> promotorFront RelacionLaboralService antes de cargar RPs cveEF= {0}", request.getPayload().getCveEntidadFinanciera());
        Response load = efClient.loadRegistrosPatronales(request.getPayload().getCveEntidadFinanciera());
        log.log(Level.INFO, ">>>>> promotorFront RelacionLaboralService despues de cargar RPs cveEF= {0}", request.getPayload().getCveEntidadFinanciera());
        if (load.getStatus() == 200){
            response = load.readEntity(new GenericType<List<String>>(){});
        }
        log.log(Level.INFO, ">>>>> promotorFront RelacionLaboralService antes de cargar RLs rl= {0}", rl);
        Response loadrl = rlClient.load(rl);
        log.log(Level.INFO, ">>>>> promotorFront RelacionLaboralService despues de cargar RLs rl= {0}", rl);
        if(loadrl.getStatus() == 200){
            responserl = loadrl.readEntity(RelacionLaboralOut.class);
            if (responserl.getCode().equals("000")) {
                for(InfoCIRelacionLaboral irl : responserl.getListInfoRelacionesLaborales()){
                    if(irl.getLstInfoRelacionesLaborales().getFecFinRelLab().equals("9999-12-31")){
                        rlActiva.add(irl.getLstInfoRelacionesLaborales().getRegPatron());
                        log.log(Level.INFO, "tiene una relacion activa");
                    }
                }
            }else{
                validacion.add("1");//Error al validar la relacion laboral
                //flagRegistro = false;
                log.log(Level.INFO, "error de validacion {0}", responserl.getMessage());
            }
        }
        if(rlActiva.size() > 0 ){
            for (String rla : rlActiva){
                for(String rlr : response){
                    if(rlr.equalsIgnoreCase(rla)){
                        flagRegistro = false;
                    }
                }
                
            }
            
        }
        log.log(Level.INFO, "flagRegistro {0}", flagRegistro);
        if(flagRegistro){
            if(validacion.size() ==0){
                validacion.add("1");
            }
            //El promotor no tiene una relacion laboral con una EF
            //throw new RelacionLaboralException();
            log.log(Level.INFO, "Antes del persona Back curp: {0}", request.getPayload().getCurp());
            Response res = pbClient.load(request.getPayload().getCurp());
            log.log(Level.INFO, "Despues del persona Back curp: {0}", request.getPayload().getCurp());
            if(res.getStatus() == 200){
                persona = res.readEntity(PersonaModel.class);
                EstadoPersonaEf estadoEF = new EstadoPersonaEf();
                estadoEF.setId(3L);
                BajaPromotorRq bajaRq = new BajaPromotorRq();
                bajaRq.setId(persona.getId());
                bajaRq.setCveEntidadFinanciera(request.getPayload().getCveEntidadFinanciera());
                bajaRq.setCvePersonalEf(request.getPayload().getIdPersonaEF());
                bajaRq.setBaja(3L);
                bajaRq.setCveEstadoPersonaEf(3L);
                bajaRq.setEmail(persona.getCorreoElectronico());
                bajaRq.setEstadoPersonaEf(estadoEF);
                log.log(Level.INFO, "Antes de la baja del promotor: {0}", bajaRq);
                Response bajaRes = pfClient.bajaPromotor(bajaRq);
                log.log(Level.INFO, "Despues de la baja del promotor: {0}", bajaRq);
                if(bajaRes.getStatus() == 202){
                    BitacoraPersonaRq bPersona = new BitacoraPersonaRq();
                    bPersona.setCvePersona(persona.getId());
                    bPersona.setCvePersonalEF(request.getPayload().getIdPersonaEF());
                    bPersona.setCveRegistroPromotor(request.getPayload().getIdPersonaEF());
                    bPersona.setMotivo("3");
                    bPersona.setActividad("2");
                    log.log(Level.INFO, "Antes de la bitacora: {0}", bPersona);
                    bbClient.load(bPersona);
                    log.log(Level.INFO, "Despues de la bitacora: {0}", bPersona);
                }
            }
            
        }
                
        if(validacion.size() == 0){
            validacion.add("3");
        }
        log.log(Level.INFO, "Termina la validacion : {0}", validacion);
        return validacion;
        
        
    }
    
}
