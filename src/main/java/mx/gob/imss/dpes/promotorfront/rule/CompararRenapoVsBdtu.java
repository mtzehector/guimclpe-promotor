/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.rule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.promotorfront.exeption.RenapoBDTUInfoNotMacthingException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class CompararRenapoVsBdtu extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel>{
    
    Date renapoFecNac = null;
    Date bdtuFecNac = null;
    
    final static String DATE_FORMAT = "dd/MM/yyyy";
    final static DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    
    @Override
    public PromotorPrestamoModel apply(PromotorPrestamoModel request)throws BusinessException{
        
        log.log(Level.SEVERE, ">>>>>>RULE RenapoVsBdtu {0} ", request);
        
        if (!request.getRenapoRequest().getRenapoCurpOut().getCurp().equals(request.getBdtuRequest().getBdtuOut().getCurp())){
            throw new RenapoBDTUInfoNotMacthingException();
        }
        
        if(!request.getNss().equals(request.getBdtuRequest().getBdtuOut().getNss())){
            throw new RenapoBDTUInfoNotMacthingException();
        }
        
        if (!request.getRenapoRequest().getRenapoCurpOut().getNombres().equals(request.getBdtuRequest().getBdtuOut().getNombre())){
            log.log(Level.SEVERE, ">>>>>>>>>  RenapoBDTUInfoNotMacthingException getNombres");
            throw new RenapoBDTUInfoNotMacthingException();
        }
        
        if (!request.getRenapoRequest().getRenapoCurpOut().getApellido1().equals(request.getBdtuRequest().getBdtuOut().getPrimerApellido())){
            log.log(Level.SEVERE, ">>>>>>>>>  RenapoBDTUInfoNotMacthingException getPrimerApellido");
            throw new RenapoBDTUInfoNotMacthingException();
        }
            
        if (!request.getRenapoRequest().getRenapoCurpOut().getApellido2().equals(request.getBdtuRequest().getBdtuOut().getSegundoApellido())){
            log.log(Level.SEVERE, ">>>>>>>>>  RenapoBDTUInfoNotMacthingException getSegundoApellido");
            throw new RenapoBDTUInfoNotMacthingException();
        }
        
       //Comparacion Fecha Nacimiento
        try{
            renapoFecNac = format.parse(request.getRenapoRequest().getRenapoCurpOut().getFechNac());
            bdtuFecNac = format.parse(request.getBdtuRequest().getBdtuOut().getFechaNacimiento());
        } catch(Exception e){
            e.printStackTrace();
        }
        if (renapoFecNac != null && bdtuFecNac != null && renapoFecNac.compareTo(bdtuFecNac) !=0){
            log.log(Level.SEVERE, ">>>>>>>>>  RenapoBDTUInfoNotMacthingException renapo.getFechNac()={0}",request.getRenapoRequest().getRenapoCurpOut().getFechNac());
            log.log(Level.SEVERE, ">>>>>>>>>  RenapoBDTUInfoNotMacthingException bdtu.getFechaNacimiento()={0}",request.getBdtuRequest().getBdtuOut().getFechaNacimiento());
            log.log(Level.SEVERE, ">>>>>>>>>  RenapoBDTUInfoNotMacthingException getFechaNacimiento");
            throw new RenapoBDTUInfoNotMacthingException();
        }
           
        return request;
    }
}
