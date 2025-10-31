/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.assembler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mx.gob.imss.dpes.common.assembler.BaseAssembler;
import mx.gob.imss.dpes.promotorfront.model.PersonaRequest;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

/**
 *
 * @author edgar.arenas
 */
public class PersonaAssembler extends BaseAssembler<PromotorPrestamoModel, PersonaRequest>{
    
    @Override
    public PersonaRequest assemble(PromotorPrestamoModel source) {
        
        PersonaRequest persona = new PersonaRequest();
         
        try {
        Date fn = new SimpleDateFormat("dd/MM/yyyy").parse(source.getPensionado().getFecNacimiento());
        persona.setFecNacimiento(fn);
        } catch (ParseException ex) {
            Logger.getLogger(PersonaAssembler.class.getName()).log(Level.SEVERE, null, ex);
        }
        persona.setTelCelular(source.getPersonaRequest().getTelCelular());
        persona.setTelLocal(source.getPersonaRequest().getTelLocal());
        persona.setCorreoElectronico(source.getPersonaRequest().getCorreoElectronico());
        persona.setNombre(source.getPensionado().getNomNombre());
        persona.setPrimerApellido(source.getPensionado().getNomApellidoPaterno());
        persona.setSegundoApellido(source.getPensionado().getNomApellidoMaterno());
        persona.setCurp(source.getPensionado().getCveCurp());
        persona.setCveSexo(Short.parseShort(source.getPensionado().getSexo()));
        persona.setNss(source.getPensionado().getIdNss());
        persona.setIndRegistro(0);
                
        log.log(Level.INFO,">>>>PersonaAssembler : {0}", persona);
        return persona;
        
    }
}
