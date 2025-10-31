/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.rule;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.promotorfront.assembler.PersonaAssembler;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesFechaNominalException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;
import mx.gob.imss.dpes.support.config.CustomDateDeserializer;
import mx.gob.imss.dpes.support.config.CustomDateSerializer;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class FechaNominal extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel> {
    
      @JsonDeserialize(using = CustomDateDeserializer.class)
      @JsonSerialize(using = CustomDateSerializer.class)
      Date fechaActual = new Date();
      final Integer DIAS = 4;
      Integer validar;
    
  @Override
  public PromotorPrestamoModel apply(PromotorPrestamoModel input) throws BusinessException {
       log.log(Level.INFO,">>>>Fecha Nomina RULES: {0}", input);
   
      //Fecha Nominal 
      try {
        Date fn = new SimpleDateFormat("dd/MM/yyyy").parse(input.getPersonaRequest().getFechaNominal());
        log.log(Level.INFO,">>>>FechaNominal fn= {0}", fn);
        Date fechaLimite = restarHoras(fn);
        log.log(Level.INFO,">>>>FechaNominal fechaLimite= {0}", fechaLimite);
        validar = fechaLimite.compareTo(fechaActual);
        log.log(Level.INFO,">>>>FechaNominal validar= {0}", validar);
        switch(validar){
            case 0:
            case 1:
                break;
            case -1:
                throw new CondicionesFechaNominalException();  
                
            default:
                break;
        }
      
        } catch (ParseException ex) {
            Logger.getLogger(PersonaAssembler.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    return input;
  }
  
  

    protected Date restarHoras(Date request){
        Calendar c = Calendar.getInstance();
        c.setTime(request);
        c.add(Calendar.DAY_OF_MONTH, -DIAS);
        return c.getTime();
    }
  
}
