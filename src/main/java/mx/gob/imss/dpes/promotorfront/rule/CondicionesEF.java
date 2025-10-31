/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.promotorfront.rule;

import java.util.logging.Level;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.rule.BaseRule;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesEFException;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesEFMonotoMinimoException;
import mx.gob.imss.dpes.promotorfront.exeption.CondicionesEFMontoMaximoException;
import mx.gob.imss.dpes.promotorfront.model.PromotorPrestamoModel;

/**
 *
 * @author edgar.arenas
 */
@Provider
public class CondicionesEF extends BaseRule<PromotorPrestamoModel, PromotorPrestamoModel> {

    @Override
    public PromotorPrestamoModel apply(PromotorPrestamoModel input) throws BusinessException {

        log.log(Level.INFO, ">>>REGLAS CONDICIONES EF");
        CalcularEdad regla = new CalcularEdad();
        CalcularEdad.Input entrada = regla.new Input(input.getPensionado().getFecNacimiento());
        CalcularEdad.Output salida = regla.apply(entrada);
        Integer edadPensionado = salida.getEdad();
        log.log(Level.INFO, ">>>EdadPensionado {0}", edadPensionado);
        if (Double.parseDouble(input.getPersonaRequest().getPrestamo().getMontoSolicitado()) > input.getCondicionesEF().getMontoMaximo()) {
            input.setError("msg359");
            return input;
            //throw new CondicionesEFMontoMaximoException();
        } else if (Double.parseDouble(input.getPersonaRequest().getPrestamo().getMontoSolicitado()) < input.getCondicionesEF().getMontoMinimo()) {
            input.setError("msg358");
            return input;
            //throw new CondicionesEFMonotoMinimoException();
        } else if (edadPensionado > input.getCondicionesEF().getEdadLimite()){
            input.setError("msg357");
            return input;
            //throw new CondicionesEFException();
        }else{
            return input;
        }
    }
}
